package com.gyh.gis.domain;

import java.util.Date;
import lombok.Data;

/**
 * create by GYH on 2022/9/26
 */
@Data
public class DeviceTatus {
    private Integer id;

    /**
    * 站点id
    */
    private Integer stationId;

    /**
    * 异常状态
    */
    private String errorState;

    /**
    * 报警状态
    */
    private String alarmState;

    /**
    * 异常时间
    */
    private Date errorTime;

    /**
    * 报警时间
    */
    private Date alarmTime;

    /**
    * 报警值
    */
    private Float alarmValue;

    /**
    * 是否取消报警
    */
    private Object cancelAlarm;

    /**
    * 取消报警时间
    */
    private Date cancelTime;

    /**
    * 报警相机截图
    */
    private String screenshotUrl;
}