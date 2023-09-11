package com.gyh.gis.dto.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * create by GYH on 2023/9/11
 */
@Data
public class ExamineResp {
    /**
     * 水电站id
     */
    private Integer stationId;
    /**
     * 站名
     */
    private String station;

    /**
     * 所属流域
     */
    private String area;

    /**
     * 下泄达标率
     */
    private BigDecimal flowTargetRate;

    /**
     * 在线达标率
     */
    private BigDecimal onlineTargetRate;

    /**
     * 时间
     */
    private LocalDateTime time;
}
