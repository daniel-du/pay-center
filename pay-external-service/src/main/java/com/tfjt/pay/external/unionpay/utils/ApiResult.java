/*
 *
 *      Copyright (c) 2018-2025, zt All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: zt
 *
 */

package com.tfjt.pay.external.unionpay.utils;

import com.tfjt.pay.external.unionpay.constants.CommonConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 对外响应信息主体
 *
 * @param <T>
 * @author lixiaolei
 */
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "对外响应信息主体")
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    @ApiModelProperty(value = "返回标记：成功标记=0，失败标记=1")
    private int code;

    @Getter
    @Setter
    @ApiModelProperty(value = "返回信息")
    private String msg;

    @Getter
    @Setter
    @ApiModelProperty(value = "数据")
    private T data;

    public static <T> ApiResult<T> ok() {
        return restResult(null, CommonConstants.SUCCESS_200, null);
    }

    public static <T> ApiResult<T> ok(T data) {
        return restResult(data, CommonConstants.SUCCESS_200, null);
    }

    public static <T> ApiResult<T> ok(T data, String msg) {
        return restResult(data, CommonConstants.SUCCESS_200, msg);
    }

    public static <T> ApiResult<T> failed() {
        return restResult(null, CommonConstants.FAIL, null);
    }

    public static <T> ApiResult<T> failed(String msg) {
        return restResult(null, CommonConstants.FAIL, msg);
    }

    public static <T> ApiResult<T> failed(T data) {
        return restResult(data, CommonConstants.FAIL, null);
    }

    public static <T> ApiResult<T> failed(T data, String msg) {
        return restResult(data, CommonConstants.FAIL, msg);
    }

    public static <T> ApiResult<T> failed(int code, String msg, T data) {
        return restResult(null, code, msg);
    }

    private static <T> ApiResult<T> restResult(T data, int code, String msg) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }
}
