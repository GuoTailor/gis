package com.gyh.gis.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * create by GYH on 2023/9/17
 */
@Data
public class AlarmImgResp {
    private Long id;
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
    @Schema(description = "图片本地地址")
    private String localFilePath;
    @Schema(description = "图片url")
    private String imgDescribe;
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
}
