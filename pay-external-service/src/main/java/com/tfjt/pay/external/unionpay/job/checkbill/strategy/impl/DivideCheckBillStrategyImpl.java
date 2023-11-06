package com.tfjt.pay.external.unionpay.job.checkbill.strategy.impl;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tfjt.pay.external.unionpay.dao.LoanBalanceDivideDetailsDao;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceDivideDetailsEntity;
import com.tfjt.pay.external.unionpay.enums.CheckBillTypeEnum;
import com.tfjt.pay.external.unionpay.job.checkbill.strategy.CheckBillBaseStrategy;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author songx
 * @Date: 2023/11/06/12:22
 * @Description:
 */
@Component
public class DivideCheckBillStrategyImpl implements CheckBillBaseStrategy {
    @Resource
    private LoanBalanceDivideDetailsDao loanBalanceDivideDetailsDao;
    @Override
    public Integer unCheckCount(Date date) {
        return loanBalanceDivideDetailsDao.countUnCheckBill(date);
    }

    @Override
    public List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, Integer pageNo, Integer pageSize) {
        Page<LoanUnionpayCheckBillDetailsEntity> page = Page.of(pageNo, pageSize);
        Page<LoanUnionpayCheckBillDetailsEntity> result = loanBalanceDivideDetailsDao.listUnCheckBill(date,page);
        return result.getRecords();
    }

    @Override
    public String getTableName() {
        return LoanBalanceDivideDetailsEntity.class.getAnnotation(TableName.class).value();
    }

    @Override
    public String getTypeName() {
        return CheckBillTypeEnum.DIVIDE_BALANCE.getTypeName();
    }
}
