package com.gyh.gis.domain;

import lombok.Data;

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
    private Date datatime;

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
    private Short onlineCount;

    /**
     * 总次数
     */
    private Short totalCount;
}