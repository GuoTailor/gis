package com.gyh.gis.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * create by GYH on 2023/9/11
 */
@Data
public class StatisticResp {
    /**
     * 水电站id
     */
    @Schema(description = "水电站id")
    private Integer stationId;
    /**
     * 站名
     */
    @Schema(description = "站名")
    private String station;

    /**
     * 所属流域
     */
    @Schema(description = "所属流域")
    private String area;
    /**
     * 核定流量
     */
    private BigDecimal flow;
    /**
     * 下泄达标率
     */
    @Schema(description = "下泄达标率")
    private BigDecimal flowTargetRate;

    /**
     * 在线达标率
     */
    @Schema(description = "在线达标率")
    private BigDecimal onlineTargetRate;

    /**
     * 时间
     */
    @Schema(description = "时间")
    private LocalDateTime time;
}
