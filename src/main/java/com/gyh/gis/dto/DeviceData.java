package com.gyh.gis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * create by GYH on 2022/9/27
 */
@Data
@Schema(description = "设备数据")
public class DeviceData {
    @Schema(description = "id")
    private Long id;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private Integer stationId;

    /**
     * 值
     */
    @Schema(description = "值")
    private BigDecimal value;

    /**
     * 核定流量
     */
    @Schema(description = "核定流量")
    private BigDecimal flow;

    /**
     * 时间
     */
    @Schema(description = "时间")
    private LocalDateTime time;
}