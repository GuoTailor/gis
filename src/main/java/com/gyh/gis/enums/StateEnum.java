package com.gyh.gis.enums;

import lombok.Getter;

/**
 * create by GYH on 2022/9/26
 */
@Getter
public enum StateEnum implements Messageable {
    /**
     * 正常
     */
    NORMAL("正常"),
    /**
     * 异常
     */
    ERROR("异常"),
    /**
     * 警报
     */
    ALARM("警报");

    final String message;

    StateEnum(String message) {
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
