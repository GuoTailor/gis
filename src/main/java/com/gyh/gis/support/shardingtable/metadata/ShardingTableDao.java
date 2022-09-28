package com.gyh.gis.support.shardingtable.metadata;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gyh.gis.support.shardingtable.TableShardingPolicyTypeEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShardingTableDao extends BaseMapper<ShardingTable> {

    default ShardingTable selectCurrentByOriginTableAndPolicyType(String originTable, TableShardingPolicyTypeEnum type) {
        return this.selectOne(Wrappers.lambdaQuery(ShardingTable.class)
                .eq(ShardingTable::getOriginName, originTable)
                .eq(ShardingTable::getPolicyType, type)
                .orderByDesc(ShardingTable::getCreateTime)
                .last("limit 1 for update")
        );
    }

    default List<ShardingTable> selectAllShardingByOriginTableAndPolicyType(String originTable, TableShardingPolicyTypeEnum type) {
        return selectList(Wrappers.lambdaQuery(ShardingTable.class)
                .eq(ShardingTable::getOriginName, originTable)
                .eq(ShardingTable::getPolicyType, type)
                .orderByDesc(ShardingTable::getCreateTime)
                .last("for update"));
    }

    default void updateRowsById(Long id, long newRows) {
        this.update(null, Wrappers.lambdaUpdate(ShardingTable.class)
                .eq(ShardingTable::getId, id)
                .set(ShardingTable::getTotalRows, newRows)
        );
    }

}
