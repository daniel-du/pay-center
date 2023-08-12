package com.tfjt.pay.external.unionpay.biz.impl;


import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansBizService;
import com.tfjt.pay.external.unionpay.dto.ReqDeleteSettleAcctParams;
import com.tfjt.pay.external.unionpay.dto.req.BankInfoReqDTO;
import com.tfjt.pay.external.unionpay.entity.CustBankInfoEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.service.CustBankInfoService;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import com.tfjt.pay.external.unionpay.service.UnionPayLoansApiService;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 贷款用户业务层
 */
@Service
@Slf4j
public class UnionPayLoansBizServiceImpl implements UnionPayLoansBizService {

    @Resource
    UnionPayLoansApiService unionPayLoansApiService;

    @Resource
    CustBankInfoService custBankInfoService;
    @Resource
    LoanUserService loanUserService;


    /**
     * 解绑银行卡
     *
     * @param deleteSettleAcctParams
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#bankInfoReqDTO.bankCardNo"}, expire = 3000, acquireTimeout = 4000)
    public void unbindSettleAcct(BankInfoReqDTO bankInfoReqDTO) {
        CustBankInfoEntity custBankInfo = custBankInfoService.getBankInfoByBankCardNoAndLoanUserId(bankInfoReqDTO.getBankCardNo(), bankInfoReqDTO.getLoanUserId());
        log.info("删除绑定银行卡:{}", bankInfoReqDTO.getBankCardNo());
        LoanUserEntity loanUser = loanUserService.getById(bankInfoReqDTO.getLoanUserId());
        if(ObjectUtils.isNotEmpty(loanUser)){
            ReqDeleteSettleAcctParams deleteSettleAcctParams = new ReqDeleteSettleAcctParams();
            deleteSettleAcctParams.setBankAcctNo(bankInfoReqDTO.getBankCardNo());
            deleteSettleAcctParams.setCusId(loanUser.getCusId());
            deleteSettleAcctParams.setMchId(loanUser.getBusId());
            unionPayLoansApiService.deleteSettleAcct(deleteSettleAcctParams);
        }else{
            throw new TfException("贷款用户不存在");
        }
        //标记删除银行卡
        custBankInfo.setDeleted(true);
        custBankInfoService.updateCustBankInfo(custBankInfo);
    }

    /**
     * 绑定银行卡
     * @param bankInfoDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#bankInfoReqDTO.bankCardNo"}, expire = 3000, acquireTimeout = 4000)
    public boolean bindSettleAcct(BankInfoReqDTO bankInfoReqDTO) {
        CustBankInfoEntity custBankInfoEntity = new CustBankInfoEntity();
        BeanUtils.copyProperties(bankInfoReqDTO, custBankInfoEntity);
        //默认一个职业
        custBankInfoEntity.setCareer("10200");
        try {
            unionPayLoansApiService.bindAddSettleAcct(custBankInfoEntity);
        } catch (TfException ex) {
            throw new TfException(ex.getCode(), ex.getMessage());
        }
        return custBankInfoService.save(custBankInfoEntity);
    }
}
