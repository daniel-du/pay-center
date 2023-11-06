package com.tfjt.pay.external.unionpay.job.checkbill.strategy.impl;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tfjt.pay.external.unionpay.dao.LoanOrderDao;
import com.tfjt.pay.external.unionpay.dao.LoanWithdrawalOrderDao;
import com.tfjt.pay.external.unionpay.entity.LoanWithdrawalOrderEntity;
import com.tfjt.pay.external.unionpay.enums.CheckBillTypeEnum;
import com.tfjt.pay.external.unionpay.job.checkbill.strategy.CheckBillBaseStrategy;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author songx
 * @Date: 2023/11/06/14:20
 * @Description 提现对账业务
 */
@Component
public class WithdrawalCheckBillStrategyImpl implements CheckBillBaseStrategy {

    @Resource
    private LoanWithdrawalOrderDao loanWithdrawalOrderDao;

    @Override
    public Integer unCheckCount(Date date) {
        return loanWithdrawalOrderDao.countUnCheckBill(date);
    }

    @Override
    public List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, Integer pageNo, Integer pageSize) {
        Page<LoanUnionpayCheckBillDetailsEntity> page = Page.of(pageNo, pageSize);
        Page<LoanUnionpayCheckBillDetailsEntity> result = loanWithdrawalOrderDao.listUnCheckBill(date,page);
        return result.getRecords();
    }

    @Override
    public String getTableName() {
        return LoanWithdrawalOrderEntity.class.getAnnotation(TableName.class).value();
    }

    @Override
    public String getTypeName() {
        return CheckBillTypeEnum.LOAN_WITHDRAWAL_ORDER.getTypeName();
    }
}
