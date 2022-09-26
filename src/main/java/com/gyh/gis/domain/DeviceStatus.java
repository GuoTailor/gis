package com.gyh.gis.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gyh.gis.enums.StateEnum;
import lombok.Data;
import org.apache.ibatis.type.BooleanTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * create by GYH on 2022/9/26
 */
@TableName(value = "device_status", autoResultMap = true)
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
    private Boolean cancelAlarm;

    /**
     * 取消报警时间
     */
    private Date cancelTime;

    /**
     * 报警相机截图
     */
    private String screenshotUrl;
}