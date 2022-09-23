package com.gyh.gis.support.sqlfileexecute.cmd;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Reader;
import java.sql.Connection;
import java.util.Map;

@Data
@Accessors(chain = true)
public class DDLSqlFileJDBCCmd {

    Reader ddlSqlFileReader;
    Connection connection;
    //替换变量
    Map<String,Object> params;

    String commentPrefix="--";

    String statementSeparator=";";

    String blockCommentStartDelimiter="/*";

    String blockCommentEndDelimiter="*/";

}
