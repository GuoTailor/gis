package com.gyh.gis.enums;

import lombok.Getter;

/**
 * create by GYH on 2022/10/10
 */
public enum AlarmStationEnum implements Messageable {
    /**
     * 已处理
     */
    CANCELED("已处理"),
    /**
     * 警报
     */
    ALARM("警报");
    @Getter
    final String message;

    AlarmStationEnum(String message) {
        this.message = message;
    }

    @Override
    public String code() {
        return name();
    }

    @Override
    public String message() {
        return message;
    }
}
