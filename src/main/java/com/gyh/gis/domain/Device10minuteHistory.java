package com.gyh.gis.domain;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

/**
 * create by GYH on 2022/9/27
 */
@Data
public class Device10minuteHistory {
    private Long id;

    /**
     * 站点id
     */
    private Integer stationId;

    /**
     * 值
     */
    private Float value;

    /**
     * 时间
     */
    private LocalDateTime time;
}