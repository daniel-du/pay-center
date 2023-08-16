package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.dao.LoanBalanceDivideDao;
import com.tfjt.pay.external.unionpay.entity.LoadBalanceDivideEntity;
import com.tfjt.pay.external.unionpay.service.LoanBalanceDivideService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;



@Service("payBalanceDivideService")
public class LoanBalanceDivideServiceImpl extends ServiceImpl<LoanBalanceDivideDao, LoadBalanceDivideEntity> implements LoanBalanceDivideService {

    @Override
    public boolean checkExistBusinessOrderNo(String businessOrderNo) {
        LambdaQueryWrapper<LoadBalanceDivideEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoadBalanceDivideEntity::getBusinessOrderNo,businessOrderNo)
                .last("limit 1");
        return this.count(queryWrapper)>1;
    }
}