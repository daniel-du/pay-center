package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.tfcommon.core.cache.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.LoanOrderDao;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;
import com.tfjt.pay.external.unionpay.service.LoanOrderService;


@Service("payLoanOrderService")
public class LoanOrderServiceImpl extends ServiceImpl<LoanOrderDao, LoanOrderEntity> implements LoanOrderService {
    @Autowired
    private RedisCache redisCache;

    @Override
    public boolean checkExistBusinessOrderNo(String businessOrderNo, String appId) {
        Object cacheObject = redisCache.getCacheObject(businessOrderNo+appId);
        if (cacheObject != null) {
            return true;
        }
        LambdaQueryWrapper<LoanOrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoanOrderEntity::getAppId, appId)
                .eq(LoanOrderEntity::getBusinessOrderNo, businessOrderNo)
                .last("limit 1");
        return this.count(queryWrapper) > 0;
    }
}