package com.gyh.gis.support.shardingtable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class TableShardingConfig {

    //分片表最大行数
    protected long shardingTableMaxRows = 2;
    //原始表名称
    protected String originTableName;
    //mysql建表语句sql文件地址
    protected String createShardingTableSQLFile;
    //分片策略
    protected TableShardingPolicyTypeEnum policyType;
    //是否使用原始表
    protected boolean useOriginTable = true;
}
