package com.gyh.gis.support.shardingtable;

import lombok.Getter;

/**
 * 分表策略
 *
 */
@Getter
public enum TableShardingPolicyTypeEnum {

    YEAR,
    YEAR_QUARTER,
    YEAR_MONTH,

}
