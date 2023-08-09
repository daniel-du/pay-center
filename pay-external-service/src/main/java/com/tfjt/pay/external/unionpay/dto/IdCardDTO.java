package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class IdCardDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 证件人像面照片
     */
    private String copy;

    /**
     * 1-中国大陆居民-身份证
     * 2-其他国家或地区居民-护照
     * 3-中国香港居民–来往内地通行证
     * 4-中国澳门居民–来往内地通行证
     * 5-中国台湾居民–来往大陆通行证
     */
    private String type;

    /**
     * 证件国徽面照片
     */
    private String national;

    /**
     * 证件号码
     */
    private String number;

    /**
     * 证件名称
     */
    private String name;

    /**
     * 证件有效期
     */
    private String validTime;

}
