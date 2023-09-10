package com.gyh.gis.domain;

import com.gyh.gis.enums.PeriodEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * create by GYH on 2023/9/10
 */
@Data
public class ExamineInfo {
    /**
     * id
     */
    private Integer id;

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

    /**
     * 水电站id
     */
    private Integer stationId;

    /**
     * 统计次数
     */
    private Integer stationCount;

    /**
     * 下泄达标率
     */
    private BigDecimal flowTargetRate;

    /**
     * 在线达标率
     */
    private BigDecimal onlineTargetRate;
}