package com.tfjt.pay.external.unionpay.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.LoanRequestUnionpayRecordDao;
import com.tfjt.pay.external.unionpay.entity.LoanRequestUnionpayRecordEntity;
import com.tfjt.pay.external.unionpay.service.LoanRequestUnionpayRecordService;


@Service("loanRequestUnionpayRecordService")
public class LoanRequestUnionpayRecordServiceImpl extends ServiceImpl<LoanRequestUnionpayRecordDao, LoanRequestUnionpayRecordEntity> implements LoanRequestUnionpayRecordService {


}