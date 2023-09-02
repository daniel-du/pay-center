package com.tfjt.pay.external.unionpay.biz.impl;

import com.tfjt.pay.external.unionpay.biz.UnionPayLoansApiBiz;
import com.tfjt.pay.external.unionpay.dto.IncomingReturn;
import com.tfjt.pay.external.unionpay.dto.ReqDeleteSettleAcctParams;
import com.tfjt.pay.external.unionpay.dto.SettleAcctsMxDTO;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansSettleAcctDTO;
import com.tfjt.pay.external.unionpay.entity.CustBankInfoEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.service.UnionPayLoansApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sun.java2d.cmm.CMSManager;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author zxy
 * @Date 2023/09/02 10:50
 */
@Service
@Slf4j
public class UnionPayLoansApiBizImpl implements UnionPayLoansApiBiz {
    @Resource
    private UnionPayLoansApiService unionPayLoansApiService;
    @Override
    public LoanUserEntity incoming(LoanUserEntity tfLoanUserEntity, String smsCode) {
        return unionPayLoansApiService.incoming(tfLoanUserEntity, smsCode);
    }

    @Override
    public IncomingReturn getIncomingInfo(String outRequestNo) {
        return unionPayLoansApiService.getIncomingInfo(outRequestNo);
    }

    @Override
    public IncomingReturn incomingEdit(LoanUserEntity tfLoanUserEntity, String smsCode) {
        return unionPayLoansApiService.incomingEdit(tfLoanUserEntity,smsCode);
    }

    @Override
    public String upload(File file) {
        return unionPayLoansApiService.upload(file);
    }

    @Override
    public UnionPayLoansSettleAcctDTO bindAddSettleAcct(CustBankInfoEntity custBankInfoEntity) {
        return unionPayLoansApiService.bindAddSettleAcct(custBankInfoEntity);
    }

    @Override
    public SettleAcctsMxDTO querySettleAcct(Integer id) {
        return unionPayLoansApiService.querySettleAcct(id);
    }

    @Override
    public UnionPayLoansSettleAcctDTO querySettleAcctByOutRequestNo(Long loanUserId, String outRequestNo) {
        return unionPayLoansApiService.querySettleAcctByOutRequestNo(loanUserId,outRequestNo);
    }

    @Override
    public void deleteSettleAcct(ReqDeleteSettleAcctParams deleteSettleAcctParams) {
        unionPayLoansApiService.deleteSettleAcct(deleteSettleAcctParams);
    }

    @Override
    public String validationMobileNumber(String mobileNumber) {
        return unionPayLoansApiService.validationMobileNumber(mobileNumber);
    }

    @Override
    public UnionPayLoansSettleAcctDTO delAndBindAddSettleAcct(CustBankInfoEntity custBankInfo, String oldBankCardNo) {
        return unionPayLoansApiService.delAndBindAddSettleAcct(custBankInfo,oldBankCardNo);
    }

    @Override
    public IncomingReturn twoIncoming(LoanUserEntity tfLoanUserEntity, String smsCode) {
        return unionPayLoansApiService.twoIncoming(tfLoanUserEntity,smsCode);
    }

    @Override
    public IncomingReturn getTwoIncomingInfo(String outRequestNo) {
        return unionPayLoansApiService.getTwoIncomingInfo(outRequestNo);
    }

    @Override
    public IncomingReturn twoIncomingEdit(LoanUserEntity tfLoanUserEntity, String smsCode) {
        return unionPayLoansApiService.twoIncoming(tfLoanUserEntity,smsCode);
    }

    @Override
    public UnionPayLoansSettleAcctDTO settleAcctsValidate(Long loanUserId, Integer payAmount) {
        return unionPayLoansApiService.settleAcctsValidate(loanUserId,payAmount);
    }

    @Override
    public String getSettleAcctId(Long loanUserId) {
        return unionPayLoansApiService.getSettleAcctId(loanUserId);
    }

    @Override
    public Boolean getMobileStatus(String mobile) {
        return unionPayLoansApiService.getMobileStatus(mobile);
    }
}
