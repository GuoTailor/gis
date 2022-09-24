package com.gyh.gis.config;

import com.gyh.gis.support.shardingtable.TableShardingConfig;
import com.gyh.gis.support.shardingtable.TableShardingPolicyTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@EqualsAndHashCode(callSuper = true)
@Component
@ConfigurationProperties("gis.storage-metadata")
public class StorageMetadataTableShardingConfig extends TableShardingConfig {
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
