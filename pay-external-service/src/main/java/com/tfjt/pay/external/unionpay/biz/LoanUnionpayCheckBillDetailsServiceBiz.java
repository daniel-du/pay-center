package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.UnionpayLoanWarningEntity;

import java.util.Date;
import java.util.List;

/**
 * @Auther: songx
 * @Date: 2023/10/28/11:27
 * @Description:
 */
public interface LoanUnionpayCheckBillDetailsServiceBiz {
    List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, String typeName);

    void saveBatchUnionpayLoanWarningEntity(List<UnionpayLoanWarningEntity> diff);
}
