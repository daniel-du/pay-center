package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.PayApplicationCallbackUrlEntity;


/**
 * 应用表-回调
 *
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-18 20:59:16
 */
public interface PayApplicationCallbackUrlService extends IService<PayApplicationCallbackUrlEntity> {
    /**
     * 获取指定事件指定appId的回调URL
     * @param eventType 事件类型
     * @param appId  appId
     * @return 通知URL
     */
    String getCallBackUrlByTypeAndAppId(String eventType, String appId);
}

