package com.tfjt.pay.external.unionpay.job.checkbill.strategy.impl;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tfjt.pay.external.unionpay.dao.LoanOrderDetailsDao;
import com.tfjt.pay.external.unionpay.entity.LoanOrderDetailsEntity;
import com.tfjt.pay.external.unionpay.enums.CheckBillTypeEnum;
import com.tfjt.pay.external.unionpay.job.checkbill.strategy.CheckBillBaseStrategy;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author songx
 * @Date: 2023/11/06/14:23
 * @Description: 订单确认
 */
@Component
public class OrderDetailCheckBillStrategyImpl implements CheckBillBaseStrategy {

    @Resource
    private LoanOrderDetailsDao loanOrderDetailsDao;

    @Override
    public Integer unCheckCount(Date date) {
        return loanOrderDetailsDao.countUnCheckBill(date);
    }

    @Override
    public List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, Integer pageNo, Integer pageSize) {
        Page<LoanUnionpayCheckBillDetailsEntity> page = Page.of(pageNo, pageSize);
        Page<LoanUnionpayCheckBillDetailsEntity> result = loanOrderDetailsDao.listUnCheckBill(date,page);
        return result.getRecords();
    }

    @Override
    public String getTableName() {
        return LoanOrderDetailsEntity.class.getAnnotation(TableName.class).value();
    }

    @Override
    public String getTypeName() {
        return CheckBillTypeEnum.LOAN_ORDER_CONFIRM.getTypeName();
    }
}
