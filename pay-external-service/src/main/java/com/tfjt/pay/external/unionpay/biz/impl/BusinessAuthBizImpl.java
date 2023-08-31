package com.tfjt.pay.external.unionpay.biz.impl;

import com.tfjt.api.BusinessAuthApiService;
import com.tfjt.pay.external.unionpay.biz.BusinessAuthBiz;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.tfcommon.core.exception.TfException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @author tony
 * @version 1.0
 * @title BusinessAuthBizImpl
 * @description
 * @create 2023/8/29 10:48
 */
@Service
public class BusinessAuthBizImpl implements BusinessAuthBiz {

    @DubboReference
    BusinessAuthApiService businessAuthApiService;
    @Override
    public boolean businessAuth(String token) {
        boolean result;
        try {
            result = businessAuthApiService.businessAuth(token);
        }catch (Exception ex){
            throw new TfException(PayExceptionCodeEnum.SERVICE_ERROR);
        }
        return result;
    }
}
