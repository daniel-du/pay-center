package com.tfjt.pay.external.unionpay.biz.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tfjt.pay.external.unionpay.biz.LoanUnionpayCheckBillDetailsServiceBiz;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.UnionpayLoanWarningEntity;
import com.tfjt.pay.external.unionpay.service.LoanUnionpayCheckBillDetailsService;
import com.tfjt.pay.external.unionpay.service.UnionpayLoanWarningService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @Auther: songx
 * @Date: 2023/10/28/11:27
 * @Description:
 */
@Component
public class LoanUnionpayCheckBillDetailsBizServiceImpl implements LoanUnionpayCheckBillDetailsServiceBiz {

    @Resource
    private LoanUnionpayCheckBillDetailsService loanUnionpayCheckBillDetailsService;

    @Resource
    private UnionpayLoanWarningService unionpayLoanWarningService;

    @Override
    public List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, String treadType, List<String> platformOrderNoList, List<String> systemOrderNo) {
        return loanUnionpayCheckBillDetailsService.list(Wrappers.<LoanUnionpayCheckBillDetailsEntity>lambdaQuery()
                .eq(LoanUnionpayCheckBillDetailsEntity::getTreadType, treadType).eq(LoanUnionpayCheckBillDetailsEntity::getBillDate, date)
                .in(LoanUnionpayCheckBillDetailsEntity::getPlatformOrderNo,platformOrderNoList)
                .in(LoanUnionpayCheckBillDetailsEntity::getSystemOrderNo,systemOrderNo));
    }

    @Override
    public void saveBatchUnionpayLoanWarningEntity(List<UnionpayLoanWarningEntity> diff) {
        unionpayLoanWarningService.saveBatch(diff);
    }
    @Override
    public Integer countByTradeTypeAndDate(String treadType, Date date, Integer checkStatus) {
        long count = loanUnionpayCheckBillDetailsService.count(Wrappers.<LoanUnionpayCheckBillDetailsEntity>lambdaQuery()
                .eq(LoanUnionpayCheckBillDetailsEntity::getTreadType, treadType).eq(LoanUnionpayCheckBillDetailsEntity::getBillDate, date)
                .eq(!Objects.isNull(checkStatus),LoanUnionpayCheckBillDetailsEntity::getCheckStatus,checkStatus));
        return (int)count;
    }

    @Override
    public List<LoanUnionpayCheckBillDetailsEntity> listByPage(String treadType, Date date,Integer checkStatus, Integer pageNo, Integer pageSize) {
        Page<LoanUnionpayCheckBillDetailsEntity> page = loanUnionpayCheckBillDetailsService.page(Page.of(pageNo, pageSize), Wrappers.<LoanUnionpayCheckBillDetailsEntity>lambdaQuery()
                .eq(LoanUnionpayCheckBillDetailsEntity::getTreadType, treadType).eq(LoanUnionpayCheckBillDetailsEntity::getBillDate, date)
                .eq(!Objects.isNull(checkStatus),LoanUnionpayCheckBillDetailsEntity::getCheckStatus,checkStatus));
        return page.getRecords();
    }

    @Override
    public void updateCheckStatus(Set<Long> ids) {
        loanUnionpayCheckBillDetailsService.update(Wrappers.<LoanUnionpayCheckBillDetailsEntity>lambdaUpdate()
                .set(LoanUnionpayCheckBillDetailsEntity::getCheckStatus, NumberConstant.ONE).in(LoanUnionpayCheckBillDetailsEntity::getId,ids));
    }
}
