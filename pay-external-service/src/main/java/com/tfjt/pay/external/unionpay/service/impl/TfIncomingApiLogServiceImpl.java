package com.tfjt.pay.external.unionpay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pingan.openbank.api.sdk.common.http.HttpResult;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.PnSdkConstant;
import com.tfjt.pay.external.unionpay.entity.TfIncomingApiLogEntity;
import com.tfjt.pay.external.unionpay.dao.TfIncomingApiLogDao;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessChannelTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessTypeEnum;
import com.tfjt.pay.external.unionpay.service.TfIncomingApiLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 进件日志记录表 服务实现类
 * </p>
 *
 * @author Du Penglun
 * @since 2024-01-03
 */
@Service
public class TfIncomingApiLogServiceImpl extends BaseServiceImpl<TfIncomingApiLogDao, TfIncomingApiLogEntity> implements TfIncomingApiLogService {

    private static final String REQUEST_TYPE = "POST";

    @Async
    @Override
    public void logProcessAsync(JSONObject jsonObject, String serviceId, HttpResult httpResult, LocalDateTime reqTime, LocalDateTime respTime, Long comsumingTime) {
        TfIncomingApiLogEntity incomingApiLogEntity = new TfIncomingApiLogEntity();
        incomingApiLogEntity.setUrl(serviceId);
        incomingApiLogEntity.setApiCode(jsonObject.getString("TxnCode"));
        incomingApiLogEntity.setRequestType(REQUEST_TYPE);
        incomingApiLogEntity.setRequestTime(reqTime);
        incomingApiLogEntity.setResponseTime(respTime);
        incomingApiLogEntity.setRequestParam(jsonObject.toJSONString());
        incomingApiLogEntity.setResponseBody(JSONObject.toJSONString(httpResult));
        incomingApiLogEntity.setConsumeTime(comsumingTime.intValue());
        incomingApiLogEntity.setStatus(NumberConstant.ONE);
        incomingApiLogEntity.setAccessChannelType(IncomingAccessChannelTypeEnum.PINGAN.getCode().byteValue());
        incomingApiLogEntity.setAccessType(IncomingAccessTypeEnum.COMMON.getCode().byteValue());
        if (!PnSdkConstant.HTTP_SUCCESS_CODE.equals(httpResult.getCode())) {
            incomingApiLogEntity.setStatus(NumberConstant.TWO);
        }
        JSONObject resultJson = JSONObject.parseObject(httpResult.getData());
        if (!PnSdkConstant.API_SUCCESS_CODE.equals(resultJson.getString("Code"))) {
            incomingApiLogEntity.setStatus(NumberConstant.TWO);
        }
        this.baseMapper.insert(incomingApiLogEntity);
    }
}
