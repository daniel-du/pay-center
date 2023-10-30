package com.tfjt.pay.external.unionpay.biz.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.biz.LoanUnionpayCheckBillDetailsServiceBiz;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.UnionpayLoanWarningEntity;
import com.tfjt.pay.external.unionpay.service.LoanUnionpayCheckBillDetailsService;
import com.tfjt.pay.external.unionpay.service.LoanUnionpayCheckBillService;
import com.tfjt.pay.external.unionpay.service.UnionpayLoanWarningService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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
    public List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, String treadType) {
        return loanUnionpayCheckBillDetailsService.list(Wrappers.<LoanUnionpayCheckBillDetailsEntity>lambdaQuery()
                .eq(LoanUnionpayCheckBillDetailsEntity::getTreadType, treadType).eq(LoanUnionpayCheckBillDetailsEntity::getBillDate, date));
    }

    @Override
    public void saveBatchUnionpayLoanWarningEntity(List<UnionpayLoanWarningEntity> diff) {
        unionpayLoanWarningService.saveBatch(diff);
    }
}
