package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.pay.external.unionpay.dao.PayCallbackLogDao;
import com.tfjt.pay.external.unionpay.entity.PayCallbackLogEntity;
import com.tfjt.pay.external.unionpay.service.PayCallbackLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service("payCallbackLogService")
public class PayCallbackLogServiceImpl extends ServiceImpl<PayCallbackLogDao, PayCallbackLogEntity> implements PayCallbackLogService {

    @Override
    @Async("asyncServiceExecutor")
    public void saveCallBackLog(String url, String appId, String inParam, String outParam, int type, Date requestTime, String merOrderId) {
        PayCallbackLogEntity payCallbackLogEntity = new PayCallbackLogEntity();
        payCallbackLogEntity.setUrl(url);
        payCallbackLogEntity.setAppId(appId);
        payCallbackLogEntity.setInParam(inParam);
        payCallbackLogEntity.setOutParam(outParam);
        payCallbackLogEntity.setType(type);
        payCallbackLogEntity.setMerOrderId(merOrderId);
        payCallbackLogEntity.setRequestTime(requestTime);
        payCallbackLogEntity.setResponseTime(new Date());
        this.baseMapper.insert(payCallbackLogEntity);
    }
}
