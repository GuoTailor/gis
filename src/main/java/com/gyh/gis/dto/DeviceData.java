package com.gyh.gis.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * create by GYH on 2022/9/27
 */
@Data
public class DeviceData {
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