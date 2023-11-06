package com.tfjt.pay.external.unionpay.biz.impl;

import com.tfjt.pay.external.unionpay.biz.LoanWithdrawalOrderBizService;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import com.tfjt.pay.external.unionpay.service.LoanWithdrawalOrderService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author songx
 * @Date: 2023/11/06/15:59
 * @Description: 提现biz
 */
@Component
public class LoanWithdrawalOrderBizServiceImpl implements LoanWithdrawalOrderBizService {

    @Resource
    private LoanWithdrawalOrderService loanWithdrawalOrderService;
    @Override
    public Integer unCheckCount(Date date) {
        return loanWithdrawalOrderService.countUnCheckBill(date);
    }
    @Override
    public List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, Integer pageNo, Integer pageSize) {
        return loanWithdrawalOrderService.listUnCheckBill(date,pageNo,pageSize);
    }
}
