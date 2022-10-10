package com.gyh.gis.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * create by GYH on 2022/9/26
 */
@Data
@Schema(description = "设备信息")
public class DeviceAlarmListResp {
    @Schema(description = "已处理")
    private Integer canceled;

    @Schema(description = "警报中")
    private Integer alarm;

    @Schema(description = "设备报警列表")
    private List<DeviceAlarmInfoResp> alarmInfoResps;

}