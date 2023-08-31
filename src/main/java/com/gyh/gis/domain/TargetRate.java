package com.gyh.gis.domain;

import java.util.Date;
import lombok.Data;

/**
 * create by GYH on 2023/8/31
 */
/**
    * 水电站达标率
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