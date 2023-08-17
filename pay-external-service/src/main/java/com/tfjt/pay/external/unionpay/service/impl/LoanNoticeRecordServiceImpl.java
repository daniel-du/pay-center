package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.LoanNoticeRecordDao;
import com.tfjt.pay.external.unionpay.entity.LoanNoticeRecordEntity;
import com.tfjt.pay.external.unionpay.service.LoanNoticeRecordService;


@Service("loanNoticeRecordService")
public class LoanNoticeRecordServiceImpl extends ServiceImpl<LoanNoticeRecordDao, LoanNoticeRecordEntity> implements LoanNoticeRecordService {

    @Override
    public boolean existEventId(String eventId) {
        LambdaQueryWrapper<LoanNoticeRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoanNoticeRecordEntity::getEventId,eventId)
                .last("limit 1");
        return this.count(wrapper)>0;

    }
}