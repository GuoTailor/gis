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
     * 异常时间
     */
    private LocalDateTime errorTime;

    /**
     * 上传时间
     */
    private LocalDateTime time;

    /**
     * 当前值
     */
    private BigDecimal value;
}