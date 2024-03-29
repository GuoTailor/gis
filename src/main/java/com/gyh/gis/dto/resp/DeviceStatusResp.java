package com.gyh.gis.dto.resp;

import com.gyh.gis.enums.StateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * create by GYH on 2022/9/26
 */
@Data
@Schema(description = "设备信息")
public class DeviceStatusResp {
    @Schema(description = "id")
    private Integer id;
    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private Integer stationId;

    /**
     * 站点名字
     */
    @Schema(description = "站点名字")
    private String stationName;

    /**
     * 所属流域
     */
    @Schema(description = "所属流域")
    private String area;

    /**
     * 经度
     */
    @Schema(description = "经度")
    private Double longitude;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private Double latitude;

    /**
     * 异常状态
     */
    @Schema(description = "异常状态")
    private StateEnum errorState;

    /**
     * 报警状态
     */
    @Schema(description = "报警状态")
    private StateEnum alarmState;

    /**
     * 异常时间
     */
    @Schema(description = "异常时间")
    private LocalDateTime errorTime;

    /**
     * 报警时间
     */
    @Schema(description = "报警时间")
    private LocalDateTime alarmTime;

    /**
     * 报警值
     */
    @Schema(description = "报警值")
    private BigDecimal alarmValue;

    /**
     * 标准值
     */
    @Schema(description = "标准值")
    private BigDecimal evaluate;

    /**
     * 相机播放地址
     */
    @Schema(description = "相机播放地址")
    private String playUrl;

}