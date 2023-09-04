package com.gyh.gis.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 水电站达标率
 * create by GYH on 2023/8/31
 */
@Data
public class TargetRate {
    private Integer id;

    /**
     * 时间
     */
    private LocalDateTime datatime;

    /**
     * 站点id
     */
    private Integer stationId;

    /**
     * 达标率
     */
    private Float targetRate;

    /**
     * 在线次数
     */
    private Integer onlineCount;

    /**
     * 总次数
     */
    private Integer totalCount;
}