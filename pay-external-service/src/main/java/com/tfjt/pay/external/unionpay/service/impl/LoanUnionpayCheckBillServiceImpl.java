package com.tfjt.pay.external.unionpay.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.LoanUnionpayCheckBillDao;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillEntity;
import com.tfjt.pay.external.unionpay.service.LoanUnionpayCheckBillService;


@Service("loanUnionpayCheckBillService")
public class LoanUnionpayCheckBillServiceImpl extends ServiceImpl<LoanUnionpayCheckBillDao, LoanUnionpayCheckBillEntity> implements LoanUnionpayCheckBillService {


}