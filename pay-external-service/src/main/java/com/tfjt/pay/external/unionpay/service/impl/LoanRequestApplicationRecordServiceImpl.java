package com.tfjt.pay.external.unionpay.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.LoanRequestApplicationRecordDao;
import com.tfjt.pay.external.unionpay.entity.LoanRequestApplicationRecordEntity;
import com.tfjt.pay.external.unionpay.service.LoanRequestApplicationRecordService;

import java.util.List;


@Service("loanRequestApplicationRecordService")
public class LoanRequestApplicationRecordServiceImpl extends ServiceImpl<LoanRequestApplicationRecordDao, LoanRequestApplicationRecordEntity> implements LoanRequestApplicationRecordService {


    @Async
    @Override
    public void asyncSave(LoanRequestApplicationRecordEntity record) {
        this.save(record);
    }

    @Override
    public List<LoanRequestApplicationRecordEntity> listError() {
        return this.getBaseMapper().listError();
    }
}