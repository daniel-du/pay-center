package com.tfjt.pay.external.unionpay.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.BankInfoDTO;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansSettleAcctDTO;
import com.tfjt.pay.external.unionpay.entity.CustBankInfoEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;

import java.util.List;

/**
 * 客户银行信息
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-20 09:27:39
 */
public interface CustBankInfoService extends IService<CustBankInfoEntity> {

    /**
     * 根据业务 id 和 来源来类型 查询用户绑定银行卡信息
     * @param loanUserId
     * @return
     */
    List<BankInfoDTO> getBankInfoByBus(Integer loanUserId);

    LoanUserEntity getTfLoanUserEntity(Integer loanUserId);

    CustBankInfoEntity getByLoanUserId(Integer loanUserId);

    UnionPayLoansSettleAcctDTO updateCustBankInfo(CustBankInfoEntity custBankInfo);


    void updateCustBankVerifyStatus(Integer loanUserId, String bankAcctno, String verifystatus);

    CustBankInfoEntity getBankInfoByBankCardNoAndLoanUserId(String destAcctNo, Long loanUserId);
}

