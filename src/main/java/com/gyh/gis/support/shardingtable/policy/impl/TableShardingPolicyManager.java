package com.gyh.gis.support.shardingtable.policy.impl;

import com.gyh.gis.support.shardingtable.TableShardingPolicyTypeEnum;
import com.gyh.gis.support.shardingtable.policy.TableShardingPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TableShardingPolicyManager {

    final Map<TableShardingPolicyTypeEnum, TableShardingPolicy> tableShardingPolicyMap;

    public TableShardingPolicyManager(List<TableShardingPolicy> tableShardingPolicyList) {
        this.tableShardingPolicyMap = new EnumMap<>(TableShardingPolicyTypeEnum.class);
        for (TableShardingPolicy tableShardingPolicy : tableShardingPolicyList) {
            this.tableShardingPolicyMap.put(tableShardingPolicy.forPolicy(), tableShardingPolicy);
        }
    }

    public TableShardingPolicy loadPolicy(TableShardingPolicyTypeEnum type) {
        var impl = tableShardingPolicyMap.get(type);
        if (impl == null) {
            throw new RuntimeException(String.format("无法加载对应策略%s实现类", type));
        }
        return impl;
    }


}
