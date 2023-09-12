package com.gyh.gis.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * create by GYH on 2023/9/12
 */
@Data
public class ExamineResp extends StatisticResp {

    private List<Target> targets;

    @Data
    public static class Target {
        /**
         * 时间
         */
        @Schema(description = "时间")
        private LocalDateTime time;
        /**
         * 在线率是否合格
         */
        @Schema(description = "在线率是否合格")
        private Boolean ecoOnline;

        /**
         * 下泄率是否合格
         */
        @Schema(description = "下泄率是否合格")
        private Boolean ecoFlow;
    }
}
