package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.UnionpayLoanWarningEntity;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Auther: songx
 * @Date: 2023/10/28/11:27
 * @Description:
 */
public interface LoanUnionpayCheckBillDetailsBizService {
    List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, String typeName, List<String> platformOrderNoList, List<String> systemOrderNo);

    void saveBatchUnionpayLoanWarningEntity(List<UnionpayLoanWarningEntity> diff);

    Integer countByTradeTypeAndDate(String typeName, Date date, Integer checkStatus);

    List<LoanUnionpayCheckBillDetailsEntity> listByPage(String treadType, Date date,Integer checkStatus, Integer pageNo, Integer pageSize);

    void updateCheckStatus(Set<Long> ids);
}
