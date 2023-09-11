package com.gyh.gis.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * create by GYH on 2023/9/11
 */
@Data
@Schema(description = "考核统计")
public class ExamineReq {
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
     * 时刻
     */
    @Schema(description = "时刻")
    @NotNull
    private LocalDateTime time;
}
