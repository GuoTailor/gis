package com.gyh.gis.domain;

import com.gyh.gis.enums.StateEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * create by GYH on 2022/9/27
 */
@Data
public class Device10minuteHistory {
    private Long id;

    /**
     * 站点id
     */
    private Integer stationId;

    /**
     * 值
     */
    private BigDecimal value;

    /**
     * 时间
     */
    private LocalDateTime time;

    /**
     * 报警状态
     */
    private StateEnum alarmState;

    /**
     * 是否取消报警
     */
    private Boolean cancelAlarm;

    /**
     * 取消报警时间
     */
    private LocalDateTime cancelTime;

    /**
     * 报警相机截图
     */
    private String screenshotUrl;

}