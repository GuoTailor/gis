package com.gyh.gis.dto.req;

import com.gyh.gis.enums.StateEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * create by GYH on 2022/9/26
 */
@Data
public class DeviceStatusInsertReq {

    /**
     * 站点id
     */
    private Integer stationId;

    /**
     * 异常状态
     */
    private StateEnum errorState;

    /**
     * 上传值
     */
    private BigDecimal value;

    /**
     * 报警相机截图
     */
    private String screenshotUrl;

    private LocalDateTime createTime;
}
