package com.gyh.gis.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * create by GYH on 2023/9/12
 */
@Data
public class TrendReq {
    @Schema(description = "开始时间")
    @NotNull
    private LocalDateTime startTime;
    @Schema(description = "开始时间")
    @NotNull
    private LocalDateTime endTime;
    @Schema(description = "站点id")
    @NotNull
    private Integer stationId;
}
