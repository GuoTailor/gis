package com.gyh.gis.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * create by GYH on 2023/9/14
 */
@Data
public class SummarizeResp {
    /**
     * 站点id
     */
    @Schema(description = "站点数量")
    private Integer stationNum;

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

    @Schema(description = "趋势")
    private List<StatisticResp> trend;
}
