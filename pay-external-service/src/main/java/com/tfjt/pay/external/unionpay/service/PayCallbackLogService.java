package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.PayCallbackLogEntity;

import java.util.Date;

public interface PayCallbackLogService extends IService<PayCallbackLogEntity> {

    void saveCallBackLog(String url, String appId, String inParam, String outParam, int type, Date requestTime, String merOrderId);
}
