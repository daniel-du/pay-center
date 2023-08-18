package com.tfjt.pay.external.unionpay.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.LoanRequestApplicationRecordDao;
import com.tfjt.pay.external.unionpay.entity.LoanRequestApplicationRecordEntity;
import com.tfjt.pay.external.unionpay.service.LoanRequestApplicationRecordService;


@Service("loanRequestApplicationRecordService")
public class LoanRequestApplicationRecordServiceImpl extends ServiceImpl<LoanRequestApplicationRecordDao, LoanRequestApplicationRecordEntity> implements LoanRequestApplicationRecordService {


}