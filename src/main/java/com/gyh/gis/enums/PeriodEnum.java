package com.gyh.gis.enums;

import lombok.Getter;

/**
 * create by GYH on 2023/9/5
 */
@Getter
public enum PeriodEnum implements Messageable {
    HOUR("小时"),
    DAY("日"),
    MONTH("月"),
    YEAR("年");
    final String message;

    @Override
    public String code() {
        return name();
    }

    @Override
    public String message() {
        return message;
    }

    PeriodEnum(String message) {
        this.message = message;
    }
}
