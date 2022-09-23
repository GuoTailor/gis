package com.gyh.gis.support.shardingtable.policy;

import com.gyh.gis.support.shardingtable.TableShardingPolicyTypeEnum;

import java.time.LocalDateTime;

/**
 * 分片表名称策略
 */
public interface TableShardingPolicy {
    //  根据时间，生成分片表表名
    String generateShardingTableName(String originTableName, LocalDateTime time);

    //  生成下一个分片扩展表名，根据前一个分片扩展表名
    String generateNextShardingExpandTableName(String expandTableName);

    //对应策略类型
    TableShardingPolicyTypeEnum forPolicy();
}
