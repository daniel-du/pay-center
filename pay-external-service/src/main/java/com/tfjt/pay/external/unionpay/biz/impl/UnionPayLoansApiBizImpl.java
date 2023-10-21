package com.tfjt.pay.external.unionpay.biz.impl;

import com.tfjt.pay.external.unionpay.api.dto.resp.BankCodeRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.UnionPayLoansSettleAcctDTO;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansApiBizService;
import com.tfjt.pay.external.unionpay.dto.IncomingReturn;
import com.tfjt.pay.external.unionpay.dto.ReqDeleteSettleAcctParams;
import com.tfjt.pay.external.unionpay.dto.SettleAcctsMxDTO;
import com.tfjt.pay.external.unionpay.entity.BankInterbankNumberEntity;
import com.tfjt.pay.external.unionpay.entity.CustBankInfoEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.service.BankInterbankNumberService;
import com.tfjt.pay.external.unionpay.service.UnionPayLoansApiService;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zxy
 * @Date 2023/09/02 10:50
 */
@Service
@Slf4j
public class UnionPayLoansApiBizImpl implements UnionPayLoansApiBizService {
    @Resource
    private UnionPayLoansApiService unionPayLoansApiService;

    @Resource
    BankInterbankNumberService bankInterbankNumberService;

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
        return unionPayLoansApiService.incomingEdit(tfLoanUserEntity, smsCode);
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
        return unionPayLoansApiService.querySettleAcctByOutRequestNo(loanUserId, outRequestNo);
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
        return unionPayLoansApiService.delAndBindAddSettleAcct(custBankInfo, oldBankCardNo);
    }

    @Override
    public IncomingReturn twoIncoming(LoanUserEntity tfLoanUserEntity, String smsCode) {
        return unionPayLoansApiService.twoIncoming(tfLoanUserEntity, smsCode);
    }

    @Override
    public IncomingReturn getTwoIncomingInfo(String outRequestNo) {
        return unionPayLoansApiService.getTwoIncomingInfo(outRequestNo);
    }

    @Override
    public IncomingReturn twoIncomingEdit(LoanUserEntity tfLoanUserEntity, String smsCode) {
        return unionPayLoansApiService.twoIncomingEdit(tfLoanUserEntity, smsCode);
    }

    @Override
    public UnionPayLoansSettleAcctDTO settleAcctsValidate(Long loanUserId, Integer payAmount,String settleAcctId) {
        return unionPayLoansApiService.settleAcctsValidate(loanUserId, payAmount, settleAcctId);
    }

    @Override
    public String getSettleAcctId(Long loanUserId) {
        return unionPayLoansApiService.getSettleAcctId(loanUserId);
    }

    @Override
    public Boolean getMobileStatus(String mobile) {
        return unionPayLoansApiService.getMobileStatus(mobile);
    }

    @Override
    public Result<List<BankCodeRespDTO>> getBankCodeByName(String bankName) {
        List<BankInterbankNumberEntity> bankNameListByBank = bankInterbankNumberService.getBankNameListByBank(bankName);
        List<BankCodeRespDTO> bankCodeRespDTOList = new ArrayList<>();
        bankNameListByBank.forEach(bank -> {
                    BankCodeRespDTO bankCode = new BankCodeRespDTO();
                    bankCode.setBankName(bank.getBankBranchName());
                    bankCode.setBankCode(bank.getDrecCode());
                    bankCode.setBankBranchCode(bank.getBankCode());
                    bankCodeRespDTOList.add(bankCode);
                }
        );
        return Result.ok(bankCodeRespDTOList);
    }
}
