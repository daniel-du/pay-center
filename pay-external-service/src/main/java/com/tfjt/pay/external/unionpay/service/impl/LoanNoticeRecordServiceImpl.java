package com.tfjt.pay.external.unionpay.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.LoanNoticeRecordDao;
import com.tfjt.pay.external.unionpay.entity.LoanNoticeRecordEntity;
import com.tfjt.pay.external.unionpay.service.LoanNoticeRecordService;


@Service("loanNoticeRecordService")
public class LoanNoticeRecordServiceImpl extends ServiceImpl<LoanNoticeRecordDao, LoanNoticeRecordEntity> implements LoanNoticeRecordService {

}