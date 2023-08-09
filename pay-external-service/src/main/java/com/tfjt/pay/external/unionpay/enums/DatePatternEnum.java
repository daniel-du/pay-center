package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 时间格式枚举
 *
 * @author effine
 * @Date 2022/9/30 16:23
 * @email iballad#163.com
 */
@Getter
@AllArgsConstructor
public enum DatePatternEnum {

    /**
     * 标准时间格式
     */
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    YYYY_MM_DD_HH_MM_SS_SSS("yyyy-MM-dd HH:mm:ss:SSS"),

    YYYY_MM_DD("yyyy-MM-dd"),

    YYYYMMDD("yyyyMMdd"),

    YYYYMMDDHHMMSS("yyyyMMddHHmmss");


    /**
     * 格式
     */
    private final String pattern;
}
