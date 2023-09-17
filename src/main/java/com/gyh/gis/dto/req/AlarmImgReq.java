package com.gyh.gis.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * create by GYH on 2023/9/17
 */
@Data
public class AlarmImgReq {
    @Schema(description = "开始时间")
    @NotNull
    private LocalDateTime startTime;
    @Schema(description = "结束时间")
    @NotNull
    private LocalDateTime endTime;
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
}
