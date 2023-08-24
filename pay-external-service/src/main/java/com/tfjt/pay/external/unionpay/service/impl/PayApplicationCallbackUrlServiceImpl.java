package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.PayApplicationCallbackUrlDao;
import com.tfjt.pay.external.unionpay.entity.PayApplicationCallbackUrlEntity;
import com.tfjt.pay.external.unionpay.service.PayApplicationCallbackUrlService;

import java.util.Objects;


@Slf4j
@Service("payApplicationCallbackUrlService")
public class PayApplicationCallbackUrlServiceImpl extends ServiceImpl<PayApplicationCallbackUrlDao, PayApplicationCallbackUrlEntity> implements PayApplicationCallbackUrlService {

    @Override
    public String getCallBackUrlByTypeAndAppId(String eventType, String appId) {
        LambdaQueryWrapper<PayApplicationCallbackUrlEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayApplicationCallbackUrlEntity::getType,eventType)
                .eq(PayApplicationCallbackUrlEntity::getAppId,appId);
        PayApplicationCallbackUrlEntity one = this.getOne(queryWrapper);
        if(Objects.isNull(one)){
            log.error("应用回调地址不存在:appId{},eventType:{}",appId,eventType);
            throw new TfException(PayExceptionCodeEnum.CALLBACK_URL_NOT_FOUND);
        }
        return one.getUrl();
    }
}