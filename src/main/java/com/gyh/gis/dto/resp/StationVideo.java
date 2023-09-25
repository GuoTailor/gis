package com.gyh.gis.dto.resp;

import com.gyh.gis.enums.CameraTypeEnum;
import com.gyh.gis.enums.StateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * create by GYH on 2022/10/12
 */
@Data
public class StationVideo {
    private Integer id;

    /**
     * 所属公司
     */
    @Schema(description = "所属公司")
    private String company;

    /**
     * 站名
     */
    @Schema(description = "站名")
    private String station;

    /**
     * 所属流域
     */
    @Schema(description = "所属流域")
    private String area;

    /**
     * 核定流量
     */
    @Schema(description = "核定流量")
    private BigDecimal flow;

    /**
     * 流量范围
     */
    @Schema(description = "流量范围")
    private Float range;

    /**
     * 值
     */
    @Schema(description = "实时值")
    private BigDecimal value;

    @Schema(description = "是否在线")
    private Boolean onLine;

    /**
     * 报警状态
     */
    @Schema(description = "报警状态")
    private StateEnum alarmState;

    /**
     * 相机ip地址
     */
    @Schema(description = "相机ip地址")
    private String cameraIp;

    /**
     * 相机播放地址
     */
    @Schema(description = "相机播放地址")
    private String playUrl;

    /**
     * 相机类型
     */
    @Schema(description = "相机类型")
    private CameraTypeEnum cameraType;
}