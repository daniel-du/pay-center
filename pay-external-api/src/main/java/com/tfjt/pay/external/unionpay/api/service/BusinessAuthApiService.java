package com.tfjt.pay.external.unionpay.api.service;

/**
 * @author tony
 * @version 1.0
 * @title BusniessAuthApiService
 * @description
 * @create 2023/8/29 10:59
 */
public interface BusinessAuthApiService {
    boolean businessAuth(String token);
}
