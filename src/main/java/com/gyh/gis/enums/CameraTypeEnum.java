package com.gyh.gis.enums;

import lombok.Getter;

/**
 * create by GYH on 2023/9/16
 */
@Getter
public enum CameraTypeEnum implements Messageable {
    /**
     * 大华
     */
    DH("大华"),
    /**
     * 海康
     */
    HK("海康");
    final String message;

    CameraTypeEnum(String message) {
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
