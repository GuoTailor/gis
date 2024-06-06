package com.gyh.gis.domain;

import com.gyh.gis.enums.CameraTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * create by GYH on 2024/4/25
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

    /**
     * 水电站编码
     */
    private String code;

    /**
     * 相机ip地址
     */
    private String cameraIp;

    /**
     * 相机播放地址
     */
    private String playUrl;

    /**
     * 相机类型
     */
    private CameraTypeEnum cameraType;

    /**
     * 系统监测编码
     */
    private String sysCode;

    /**
     * 是否是推送
     */
    private Boolean isPush;
}
