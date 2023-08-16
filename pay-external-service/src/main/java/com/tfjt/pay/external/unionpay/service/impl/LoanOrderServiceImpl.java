package com.tfjt.pay.external.unionpay.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.LoanOrderDao;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;
import com.tfjt.pay.external.unionpay.service.LoanOrderService;


@Service("payLoanOrderService")
public class LoanOrderServiceImpl extends ServiceImpl<LoanOrderDao, LoanOrderEntity> implements LoanOrderService {

}