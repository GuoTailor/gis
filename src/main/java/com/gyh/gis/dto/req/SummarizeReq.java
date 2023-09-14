package com.gyh.gis.dto.req;

import com.gyh.gis.enums.PeriodEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * create by GYH on 2023/9/14
 */
@Data
public class SummarizeReq {
    /**
     * 所属流域
     */
    @Schema(description = "所属流域")
    private String area;
    @Schema(description = "时间周期")
    @NotNull
    private PeriodEnum period;
    @Schema(description = "开始时间")
    @NotNull
    private LocalDateTime startTime;
    @Schema(description = "结束时间")
    @NotNull
    private LocalDateTime endTime;
}
