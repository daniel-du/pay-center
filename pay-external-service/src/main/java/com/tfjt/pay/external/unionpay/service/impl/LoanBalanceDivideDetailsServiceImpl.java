package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.dao.LoanBalanceDivideDetailsDao;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceDivideDetailsEntity;
import com.tfjt.pay.external.unionpay.service.LoanBalanceDivideDetailsService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;


@Service("payBalanceDivideDetailsService")
public class LoanBalanceDivideDetailsServiceImpl extends ServiceImpl<LoanBalanceDivideDetailsDao, LoanBalanceDivideDetailsEntity> implements LoanBalanceDivideDetailsService {


    @Override
    public List<LoanBalanceDivideDetailsEntity> listByDivideId(Long id) {
        LambdaQueryWrapper<LoanBalanceDivideDetailsEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoanBalanceDivideDetailsEntity::getDivideId,id);
        return this.list(queryWrapper);

    }
}