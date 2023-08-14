package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.dao.PayBalanceDivideDao;
import com.tfjt.pay.external.unionpay.entity.PayBalanceDivideEntity;
import com.tfjt.pay.external.unionpay.service.PayBalanceDivideService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;



@Service("payBalanceDivideService")
public class PayBalanceDivideServiceImpl extends ServiceImpl<PayBalanceDivideDao, PayBalanceDivideEntity> implements PayBalanceDivideService {

    @Override
    public boolean checkExistBusinessOrderNo(String businessOrderNo) {
        LambdaQueryWrapper<PayBalanceDivideEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayBalanceDivideEntity::getBusinessOrderNo,businessOrderNo)
                .last("limit 1");
        return this.count(queryWrapper)>1;
    }
}