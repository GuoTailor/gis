package com.gyh.gis.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * create by GYH on 2022/9/27
 */
@Data
public class DeviceDayHistory {
    private Long id;

    /**
     * 站点id
     */
    private Integer stationId;

    /**
     * 值
     */
    private BigDecimal value;

    /**
     * 时间
     */
    private LocalDate time;
}