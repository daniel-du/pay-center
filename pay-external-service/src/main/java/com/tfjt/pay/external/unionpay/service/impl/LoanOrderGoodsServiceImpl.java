package com.tfjt.pay.external.unionpay.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.PayLoanOrderGoodsDao;
import com.tfjt.pay.external.unionpay.entity.LoanOrderGoodsEntity;
import com.tfjt.pay.external.unionpay.service.LoanOrderGoodsService;


@Service("payLoanOrderGoodsService")
public class LoanOrderGoodsServiceImpl extends ServiceImpl<PayLoanOrderGoodsDao, LoanOrderGoodsEntity> implements LoanOrderGoodsService {

}