package com.gyh.gis.domain;

import com.gyh.gis.enums.StateEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * create by GYH on 2022/9/26
 */
@Data
public class DeviceStatus {
    private Integer id;

    /**
     * 站点id
     */
    private Integer stationId;

    /**
     * 异常状态
     */
    private StateEnum errorState;

    /**
     * 报警状态
     */
    private StateEnum alarmState;

    /**
     * 异常时间
     */
    private LocalDateTime errorTime;

    /**
     * 报警时间
     */
    private LocalDateTime alarmTime;

    /**
     * 报警值
     */
    private BigDecimal alarmValue;

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