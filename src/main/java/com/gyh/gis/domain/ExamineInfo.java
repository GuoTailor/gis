package com.gyh.gis.domain;

import com.gyh.gis.enums.PeriodEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * create by GYH on 2023/9/5
 */
@Data
public class ExamineInfo {
    private Integer id;

    private Integer stationId;

    /**
     * 水电站统计代码
     */
    private String hystCode;

    /**
     * 在线率是否合格
     */
    private Boolean ecoOnline;

    /**
     * 下泄率是否合格
     */
    private Boolean ecoFlow;

    /**
     * 考核周期
     */
    private PeriodEnum assPer;

    /**
     * 考核开始时间
     */
    private LocalDateTime assStart;
    /**
     * 考核结束时间
     */
    private LocalDateTime assEnd;
    /**
     * 记录时间
     */
    private LocalDateTime recTime;

}
