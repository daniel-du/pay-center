package com.tfjt.pay.external.unionpay.api.service.impl;

import com.tfjt.pay.external.unionpay.api.service.BusinessAuthApiService;
import com.tfjt.pay.external.unionpay.biz.BusinessAuthBiz;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author tony
 * @version 1.0
 * @title BusniessAuthApiServiceImpl
 * @description
 * @create 2023/8/29 11:00
 */

@Service("businessAuthApiService")
public class BusinessAuthApiServiceImpl implements BusinessAuthApiService {

    @Resource
    BusinessAuthBiz businessAuthBiz;
    @Override
    public boolean businessAuth(String token) {
        return businessAuthBiz.businessAuth(token);
    }
}
