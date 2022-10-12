package com.gyh.gis.domain;

import java.math.BigDecimal;
import lombok.Data;

/**
 * create by GYH on 2022/10/12
 */
@Data
public class Station {
    private Integer id;

    /**
     * 所属公司
     */
    private String company;

    /**
     * 站名
     */
    private String station;

    /**
     * 所属流域
     */
    private String area;

    /**
     * 核定流量
     */
    private BigDecimal flow;

    /**
     * 建设地址
     */
    private String location;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 主要建设内容及规模
     */
    private String power;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 流量范围
     */
    private Float range;
}