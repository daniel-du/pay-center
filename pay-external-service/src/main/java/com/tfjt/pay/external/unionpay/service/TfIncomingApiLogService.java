package com.tfjt.pay.external.unionpay.service;

import com.alibaba.fastjson.JSONObject;
import com.pingan.openbank.api.sdk.common.http.HttpResult;
import com.tfjt.pay.external.unionpay.entity.TfIncomingApiLogEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;

/**
 * <p>
 * 进件日志记录表 服务类
 * </p>
 *
 * @author Du Penglun
 * @since 2024-01-03
 */
public interface TfIncomingApiLogService extends IService<TfIncomingApiLogEntity> {

    /**
     * 异步保存调用日志
     * @param jsonObject
     * @param serviceId
     * @param httpResult
     * @param reqTime
     * @param respTime
     * @param comsumingTime
     */
    void logProcessAsync(JSONObject jsonObject, String serviceId, HttpResult httpResult, LocalDateTime reqTime,
                         LocalDateTime respTime, Long comsumingTime);
}
