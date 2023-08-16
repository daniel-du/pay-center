package com.tfjt.pay.external.unionpay.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.PayLoanOrderDetailsDao;
import com.tfjt.pay.external.unionpay.entity.LoanOrderDetailsEntity;
import com.tfjt.pay.external.unionpay.service.LoanOrderDetailsService;


@Service("payLoanOrderDetailsService")
public class LoanOrderDetailsServiceImpl extends ServiceImpl<PayLoanOrderDetailsDao, LoanOrderDetailsEntity> implements LoanOrderDetailsService {

}