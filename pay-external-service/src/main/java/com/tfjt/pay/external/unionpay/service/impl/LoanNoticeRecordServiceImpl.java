package com.tfjt.pay.external.unionpay.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.pay.external.unionpay.common.utils.PageUtils;
import com.tfjt.pay.external.unionpay.common.utils.Query;

import com.tfjt.pay.external.unionpay.dao.LoanNoticeRecordDao;
import com.tfjt.pay.external.unionpay.entity.LoanNoticeRecordEntity;
import com.tfjt.pay.external.unionpay.service.LoanNoticeRecordService;


@Service("loanNoticeRecordService")
public class LoanNoticeRecordServiceImpl extends ServiceImpl<LoanNoticeRecordDao, LoanNoticeRecordEntity> implements LoanNoticeRecordService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<LoanNoticeRecordEntity> page = this.page(
                new Query<LoanNoticeRecordEntity>().getPage(params),
                new QueryWrapper<LoanNoticeRecordEntity>()
        );

        return new PageUtils(page);
    }

}