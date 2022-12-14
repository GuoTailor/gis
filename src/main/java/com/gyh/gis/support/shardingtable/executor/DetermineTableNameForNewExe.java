package com.gyh.gis.support.shardingtable.executor;

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.gyh.gis.config.StorageMetadataTableShardingConfig;
import com.gyh.gis.support.shardingtable.TableShardingConfig;
import com.gyh.gis.support.shardingtable.TableShardingPolicyTypeEnum;
import com.gyh.gis.support.shardingtable.executor.input.DetermineTableNameForNewInput;
import com.gyh.gis.support.shardingtable.executor.output.DetermineTableNameForNewOutput;
import com.gyh.gis.support.shardingtable.metadata.ShardingTable;
import com.gyh.gis.support.shardingtable.metadata.ShardingTableDao;
import com.gyh.gis.support.shardingtable.policy.TableShardingPolicy;
import com.gyh.gis.support.shardingtable.policy.impl.TableShardingPolicyManager;
import com.gyh.gis.support.sqlfileexecute.cmd.DDLSqlFileJDBCCmd;
import com.gyh.gis.support.sqlfileexecute.executor.DDLSqlFileJDBCCmdExe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DetermineTableNameForNewExe {
    final StorageMetadataTableShardingConfig shardingConfigs;

    final TableShardingPolicyManager tableShardingPolicyManager;

    final ShardingTableDao shardingTableDao;

    final DDLSqlFileJDBCCmdExe ddlSqlFileJDBCCmdExe;

    final ResourcePatternResolver resourcePatternResolver;

    final DataSource dataSource;

    @RequiredArgsConstructor
    private static class ExecutionContext {
        final DetermineTableNameForNewInput input;
        final TableShardingConfig.ShardingConfig shardingConfig;

        public ShardingTable currentShardingTable;
        public Connection currentConnection;
        public ShardingTable previousShardingTable;
    }

    @Transactional(rollbackFor = Throwable.class)
    public DetermineTableNameForNewOutput execute(DetermineTableNameForNewInput input) {
        // ??????????????????
        var originTableName = TableInfoHelper.getTableInfo(input.getOriginTableName()).getTableName();
        // ???????????????????????????
        TableShardingConfig.ShardingConfig shardingConfig = shardingConfigs.getConfigs().get(originTableName);
        if (shardingConfig == null) throw new IllegalArgumentException("??????" + originTableName + "????????????");
        var ctx = new ExecutionContext(input, shardingConfig);
        return doExecute(ctx);
    }

    /**
     * ?????????????????????
     *
     * @param domain ?????????
     * @return ??????????????????
     */
    public List<ShardingTable> getAllSharding(Class<?> domain) {
        // ??????????????????
        var originTableName = TableInfoHelper.getTableInfo(domain).getTableName();
        TableShardingConfig.ShardingConfig shardingConfig = shardingConfigs.getConfigs().get(originTableName);
        return shardingTableDao.selectAllShardingByOriginTableAndPolicyType(shardingConfig.getOriginTableName(), shardingConfig.getPolicyType());
    }

    private void finallyReleaseResource(ExecutionContext ctx) {
        try {
            //???????????????????????????????????????
            if (ctx.currentConnection != null) {
                DataSourceUtils.releaseConnection(ctx.currentConnection, dataSource);
            }
        } catch (Exception e) {
            log.warn("???????????????????????????????????????????????????{}?????????", e.getMessage());
        }

    }

    private DetermineTableNameForNewOutput doExecute(ExecutionContext ctx) {

        //????????????????????????????????????
        if (this.isShouldUseNewShardingTable(ctx)) {
            //???????????????
            this.useNewShardingTable(ctx);
        }
        //??????????????????????????????
        if (this.isSharingTableNeedExpand(ctx)) {
            //????????????
            this.expandShardingTable(ctx);
        }
        //??????????????????
        return this.composeResult(ctx);
    }

    /**
     * ?????????
     */
    private void useNewShardingTable(ExecutionContext ctx) {
        var input = ctx.input;
        var originTableName = ctx.shardingConfig.getOriginTableName();

        //???????????????????????????
        this.updatePreviousShardingTableRows(ctx);


        //????????????????????????????????????
        var shardingTableNameShould = loadPolicy(ctx.shardingConfig).generateShardingTableName(originTableName, input.getCreateTime());

        //???????????????
        ctx.currentShardingTable = recordCurrentShardingTableToDB(shardingTableNameShould, ctx.shardingConfig);
        if (ctx.shardingConfig.getPolicyType() != TableShardingPolicyTypeEnum.NEVER) {
            //????????????????????????????????????
            createSharingTableFromFile(ctx);
        }

    }

    /**
     * ???????????????????????????
     * 1 ??????????????????
     * 2 ???????????????????????????
     */
    private boolean isShouldUseNewShardingTable(ExecutionContext ctx) {
        var input = ctx.input;
        var originTableName = ctx.shardingConfig.getOriginTableName();
        //??????????????????????????????????????????
        var currentShardingTable = shardingTableDao.selectCurrentByOriginTableAndPolicyType(originTableName, ctx.shardingConfig.getPolicyType());
        if (currentShardingTable == null) {
            //????????????????????????
            return true;
        }

        //???????????????????????????
        //????????????????????????????????????
        var shardingTableNameShould = loadPolicy(ctx.shardingConfig).generateShardingTableName(originTableName, input.getCreateTime());
        //??????????????????????????????????????????????????????????????????????????????
        if (!currentShardingTable.getTableName().startsWith(shardingTableNameShould)) {
            ctx.previousShardingTable = currentShardingTable;
            return true;
        }

        ctx.currentShardingTable = currentShardingTable;
        return false;
    }

    /**
     * ?????????
     */
    private void expandShardingTable(ExecutionContext ctx) {
        var shardingTable = ctx.currentShardingTable;
        //?????????????????????????????????
        shardingTableDao.updateRowsById(shardingTable.getId(), shardingTable.getTotalRows());
        //????????????????????????
        var currentShardingTableName = loadPolicy(ctx.shardingConfig).generateNextShardingExpandTableName(shardingTable.getTableName());
        //??????????????????
        ctx.currentShardingTable = recordCurrentShardingTableToDB(currentShardingTableName, ctx.shardingConfig);
        //??????????????????
        this.createSharingTableFromFile(ctx);
    }

    private boolean isSharingTableNeedExpand(ExecutionContext ctx) {
        var shardingTable = ctx.currentShardingTable;
        //????????????????????????
        var maxRows = countTable(loadCurrentConnection(ctx), shardingTable.getTableName());
        shardingTable.setTotalRows(maxRows);
        return maxRows >= ctx.shardingConfig.getShardingTableMaxRows();
    }

    private long countTable(Connection connection, String tableName) {
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select count(*) from " + tableName)) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("????????????????????????????????????", e);
        }
        return 0;
    }

    private void updatePreviousShardingTableRows(ExecutionContext ctx) {
        var previousShardingTable = ctx.previousShardingTable;
        if (previousShardingTable == null) {
            return;
        }
        //????????????????????????
        var rows = countTable(loadCurrentConnection(ctx), previousShardingTable.getTableName());
        previousShardingTable.setTotalRows(rows);
        //?????????????????????????????????
        shardingTableDao.updateRowsById(previousShardingTable.getId(), previousShardingTable.getTotalRows());
    }

    /**
     * ?????????????????????
     */
    private ShardingTable recordCurrentShardingTableToDB(String currentShardingTableName, TableShardingConfig.ShardingConfig shardingConfig) {
        ShardingTable table = new ShardingTable();
        table.setTableName(currentShardingTableName);
        table.setCreateTime(LocalDateTime.now());
        table.setOriginName(shardingConfig.getOriginTableName());
        table.setPolicyType(shardingConfig.getPolicyType());
        table.setTotalRows(0);
        shardingTableDao.insert(table);
        return table;
    }

    private Connection loadCurrentConnection(ExecutionContext ctx) {
        if (ctx.currentConnection == null) {
            ctx.currentConnection = DataSourceUtils.getConnection(dataSource);
        }
        return ctx.currentConnection;
    }

    private void createSharingTableFromFile(ExecutionContext ctx) {
        var shardingTable = ctx.currentShardingTable;

        var sqlFileResource = resourcePatternResolver.getResource(ctx.shardingConfig.getCreateShardingTableSQLFile());
        var paramMap = new HashMap<String, Object>(1, 1);
        paramMap.put("${" + shardingTable.getOriginName() + "}", shardingTable.getTableName());


        try (Reader sqlFileReader = new InputStreamReader(sqlFileResource.getInputStream(), StandardCharsets.UTF_8)) {

            ddlSqlFileJDBCCmdExe.execute(new DDLSqlFileJDBCCmd()
                    .setDdlSqlFileReader(sqlFileReader)
                    .setConnection(this.loadCurrentConnection(ctx))
                    .setParams(paramMap)
            );
        } catch (IOException e) {
            throw new RuntimeException("???????????????????????????sql??????", e);
        } catch (SQLException e) {
            throw new RuntimeException("???????????????sql??????????????????", e);
        }

    }

    private TableShardingPolicy loadPolicy(TableShardingConfig.ShardingConfig shardingConfig) {
        return tableShardingPolicyManager.loadPolicy(shardingConfig.getPolicyType());
    }

    private void checkCmd(ExecutionContext ctx) {
        var input = ctx.input;
        var createTime = input.getCreateTime();
        if (createTime == null) {
            throw new IllegalArgumentException("createTime????????????");
        }
    }

    private DetermineTableNameForNewOutput composeResult(ExecutionContext ctx) {
        return new DetermineTableNameForNewOutput()
                .setTableName(ctx.currentShardingTable.getTableName());
    }
}
