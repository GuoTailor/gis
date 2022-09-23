package com.gyh.gis.support.sqlfileexecute.executor;

import com.gyh.gis.support.sqlfileexecute.cmd.DDLSqlFileJDBCCmd;
import com.gyh.gis.support.sqlfileexecute.cmd.DDLSqlFileJDBCCmdResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class DDLSqlFileJDBCCmdExe {


    @RequiredArgsConstructor
    private static class ExecutionContext {
        final DDLSqlFileJDBCCmd cmd;
        public ArrayList<String> statements;
        public Map<String, String> paramsMap;
    }

    public DDLSqlFileJDBCCmdResult execute(DDLSqlFileJDBCCmd cmd) throws SQLException,IOException {
        var ctx = new ExecutionContext(cmd);
        //验证参数
        this.checkCmd(ctx);

        //解析为sql语句
        this.splitStatements(ctx);

        //替换变量
        this.replaceVars(ctx);

        //执行sql语句
        this.executeSqls(ctx);

        //组装业务结果
        return this.composeResult(ctx);
    }

    private void replaceVars(ExecutionContext ctx) {
        var cmd = ctx.cmd;
        var statementsUse = new ArrayList<String>(ctx.statements.size());
        var paramsMap = ctx.paramsMap;
        if (paramsMap == null || paramsMap.isEmpty()) {
            return;
        }
        for (String statement : ctx.statements) {
            for (var paramEntry : paramsMap.entrySet()) {
                statement = statement.replace(paramEntry.getKey(), paramEntry.getValue());
            }
            statementsUse.add(statement);
        }

        ctx.statements = statementsUse;
    }

    private void splitStatements(ExecutionContext ctx) throws IOException {
        var cmd = ctx.cmd;
        try (var fileReader = cmd.getDdlSqlFileReader()) {

            String script = ScriptUtils.readScript(new LineNumberReader(cmd.getDdlSqlFileReader()),
                    cmd.getCommentPrefix(), cmd.getStatementSeparator(), cmd.getBlockCommentEndDelimiter());
            ctx.statements = new ArrayList<>(5);
            ScriptUtils.splitSqlScript(null, script, cmd.getStatementSeparator(), new String[]{cmd.getCommentPrefix()},
                    cmd.getBlockCommentStartDelimiter(), cmd.getBlockCommentEndDelimiter(), ctx.statements);

        }


    }

    private void executeSqls(ExecutionContext ctx) throws SQLException {
        var cmd = ctx.cmd;
        var connection = cmd.getConnection();
        try (Statement stmt = connection.createStatement();) {
            for (String statement : ctx.statements) {
                stmt.execute(statement);
                int rowsAffected = stmt.getUpdateCount();
                if (log.isDebugEnabled()) {
                    log.debug(rowsAffected + " returned as update count for SQL: " + statement);
                    SQLWarning warningToLog = stmt.getWarnings();
                    while (warningToLog != null) {
                        log.debug("SQLWarning ignored: SQL state '" + warningToLog.getSQLState() +
                                "', error code '" + warningToLog.getErrorCode() +
                                "', message [" + warningToLog.getMessage() + "]");
                        warningToLog = warningToLog.getNextWarning();
                    }
                }
            }
        }
    }

    private void checkCmd(ExecutionContext ctx) {
        //参数map需要清理
        var cmd = ctx.cmd;
        var paramsMap = cmd.getParams();
        Map<String, String> paramsMapUse = Collections.emptyMap();
        if (paramsMap != null) {
            paramsMapUse = new HashMap<>(paramsMap.size(), 1);
            for (Map.Entry<String, Object> stringObjectEntry : paramsMap.entrySet()) {
                var value = stringObjectEntry.getValue();
                if (value != null) {
                    paramsMapUse.put(stringObjectEntry.getKey(), value.toString());
                }
            }
        }
        ctx.paramsMap = paramsMapUse;

    }

    private DDLSqlFileJDBCCmdResult composeResult(ExecutionContext ctx) {
        return null;
    }

}
