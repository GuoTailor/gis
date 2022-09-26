package com.gyh.gis.support.shardingtable.executor;

import com.gyh.gis.config.StorageMetadataTableShardingConfig;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class DetermineTableNameForNewExe {
    final StorageMetadataTableShardingConfig shardingConfig;

    final TableShardingPolicyManager tableShardingPolicyManager;

    final ShardingTableDao shardingTableDao;

    final DDLSqlFileJDBCCmdExe ddlSqlFileJDBCCmdExe;

    final ResourcePatternResolver resourcePatternResolver;

    final DataSource dataSource;

    @RequiredArgsConstructor
    private static class ExecutionContext {
        final DetermineTableNameForNewInput input;

        public ShardingTable currentShardingTable;
        public Connection currentConnection;
        public ShardingTable previousShardingTable;
    }

    @Transactional(rollbackFor = Throwable.class)
    public DetermineTableNameForNewOutput execute(DetermineTableNameForNewInput input) {
        var ctx = new ExecutionContext(input);
        //验证参数
        this.checkCmd(ctx);
        return doExecute(ctx);
    }

    private void finallyReleaseResource(ExecutionContext ctx) {
        try {
            //释放数据库连接，如果有的话
            if (ctx.currentConnection != null) {
                if (!ctx.currentConnection.isClosed()) {
                    ctx.currentConnection.close();
                }
            }
        } catch (Exception e) {
            log.warn("关闭当前使用的数据库连接发生异常：{}，忽略", e.getMessage());
        }

    }

    private DetermineTableNameForNewOutput doExecute(ExecutionContext ctx) {

        //判断是否应该使用新的子表
        if (this.isShouldUseNewShardingTable(ctx)) {
            //使用新子表
            this.useNewShardingTable(ctx);
        }
        //判断当前子表是否已满
        if (this.isSharingTableNeedExpand(ctx)) {
            //扩展子表
            this.expandShardingTable(ctx);
        }
        //组装业务结果
        return this.composeResult(ctx);
    }

    /**
     * 新子表
     */
    private void useNewShardingTable(ExecutionContext ctx) {
        var input = ctx.input;
        var originTableName = shardingConfig.getOriginTableName();

        //统计前一个子表行数
        this.updatePreviousShardingTableRows(ctx);


        //得到当前应该使用的子表名
        var shardingTableNameShould = loadPolicy().generateShardingTableName(originTableName, input.getCreateTime());

        //先写入记录
        ctx.currentShardingTable = recordCurrentShardingTableToDB(shardingTableNameShould);
        if (shardingConfig.getPolicyType() != TableShardingPolicyTypeEnum.NEVER) {
            //再创建子表，便于事务回滚
            createSharingTableFromFile(ctx);
        }

    }

    /**
     * 两种情况使用新子表
     * 1 没有任何分表
     * 2 到期间下一个周期了
     */
    private boolean isShouldUseNewShardingTable(ExecutionContext ctx) {
        var input = ctx.input;
        var originTableName = shardingConfig.getOriginTableName();
        //获取当前正在使用的实际的子表
        var currentShardingTable = shardingTableDao.selectCurrentByOriginTableAndPolicyType(originTableName, shardingConfig.getPolicyType());
        if (currentShardingTable == null) {
            //说明没有任何分表
            return true;
        }

        //判断是否下个周期了
        //得到当前应该使用的子表名
        var shardingTableNameShould = this.loadPolicy().generateShardingTableName(originTableName, input.getCreateTime());
        //当前使用的子表名不是应该使用的子表开头（可能扩展了）
        if (!currentShardingTable.getTableName().startsWith(shardingTableNameShould)) {
            ctx.previousShardingTable = currentShardingTable;
            return true;
        }

        ctx.currentShardingTable = currentShardingTable;
        return false;
    }

    /**
     * 扩展表
     */
    private void expandShardingTable(ExecutionContext ctx) {
        var shardingTable = ctx.currentShardingTable;
        //更新之前使用子表总行数
        shardingTableDao.updateRowsById(shardingTable.getId(), shardingTable.getTotalRows());
        //确定子表扩展表名
        var currentShardingTableName = loadPolicy().generateNextShardingExpandTableName(shardingTable.getTableName());
        //记录当前子表
        ctx.currentShardingTable = recordCurrentShardingTableToDB(currentShardingTableName);
        //新建扩展子表
        this.createSharingTableFromFile(ctx);
    }

    private boolean isSharingTableNeedExpand(ExecutionContext ctx) {
        var shardingTable = ctx.currentShardingTable;
        //统计子表总记录数
        var maxRows = countTable(loadCurrentConnection(ctx), shardingTable.getTableName());
        shardingTable.setTotalRows(maxRows);
        return maxRows >= shardingConfig.getShardingTableMaxRows();
    }

    private long countTable(Connection connection, String tableName) {
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select count(*) from " + tableName)) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计子表当前行数发生异常", e);
        }
        return 0;
    }

    private void updatePreviousShardingTableRows(ExecutionContext ctx) {
        var previousShardingTable = ctx.previousShardingTable;
        if (previousShardingTable == null) {
            return;
        }
        //统计之前子表行数
        var rows = countTable(loadCurrentConnection(ctx), previousShardingTable.getTableName());
        previousShardingTable.setTotalRows(rows);
        //更新之前使用子表总行数
        shardingTableDao.updateRowsById(previousShardingTable.getId(), previousShardingTable.getTotalRows());
    }

    /**
     * 记录当前分表名
     */
    private ShardingTable recordCurrentShardingTableToDB(String currentShardingTableName) {
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

        var sqlFileResource = resourcePatternResolver.getResource(shardingConfig.getCreateShardingTableSQLFile());
        var paramMap = new HashMap<String, Object>(1, 1);
        paramMap.put("${" + shardingTable.getOriginName() + "}", shardingTable.getTableName());


        try (Reader sqlFileReader = new InputStreamReader(sqlFileResource.getInputStream(), StandardCharsets.UTF_8)) {

            ddlSqlFileJDBCCmdExe.execute(new DDLSqlFileJDBCCmd()
                    .setDdlSqlFileReader(sqlFileReader)
                    .setConnection(this.loadCurrentConnection(ctx))
                    .setParams(paramMap)
            );
        } catch (IOException e) {
            throw new RuntimeException("无法读取建分片子表sql文件", e);
        } catch (SQLException e) {
            throw new RuntimeException("建分片子表sql语句执行异常", e);
        }

    }

    private TableShardingPolicy loadPolicy() {
        return tableShardingPolicyManager.loadPolicy(shardingConfig.getPolicyType());
    }

    private void checkCmd(ExecutionContext ctx) {
        var input = ctx.input;
        var createTime = input.getCreateTime();
        if (createTime == null) {
            throw new IllegalArgumentException("createTime不能为空");
        }

    }

    private DetermineTableNameForNewOutput composeResult(ExecutionContext ctx) {
        return new DetermineTableNameForNewOutput()
                .setTableName(ctx.currentShardingTable.getTableName());
    }
}
