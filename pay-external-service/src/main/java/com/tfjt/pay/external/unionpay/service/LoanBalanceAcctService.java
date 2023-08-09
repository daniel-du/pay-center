package com.tfjt.pay.external.unionpay.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceAcctEntity;

import java.util.List;

/**
 * 贷款用户电子账单
 *
 * @author effine
 * @email iballad@163.com
 * @date 2023-05-23 08:48:02
 */
public interface LoanBalanceAcctService extends IService<LoanBalanceAcctEntity> {

    List<LoanBalanceAcctEntity> getAccountBooksListByBus(Integer loanUserId);

    /**
     * 根据贷款用户ID、电子账单ID、电子账单号查询
     * @param relAcctNo
     * @param balanceAcctId
     * @param loanUserId
     * @return
     */
    LoanBalanceAcctEntity getTfLoanBalanceAcctEntity(String relAcctNo, String balanceAcctId, Long loanUserId);
}

