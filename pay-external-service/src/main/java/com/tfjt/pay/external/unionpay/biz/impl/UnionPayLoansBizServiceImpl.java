package com.tfjt.pay.external.unionpay.biz.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansBizService;
import com.tfjt.pay.external.unionpay.dto.ReqDeleteSettleAcctParams;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansSettleAcctDTO;
import com.tfjt.pay.external.unionpay.dto.req.BankInfoReqDTO;
import com.tfjt.pay.external.unionpay.entity.CustBankInfoEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.enums.LoanUserTypeEnum;
import com.tfjt.pay.external.unionpay.service.CustBankInfoService;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import com.tfjt.pay.external.unionpay.service.UnionPayLoansApiService;
import com.tfjt.pay.external.unionpay.utils.UnionPaySignUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
    @Value("${unionPayLoans.encodedPub}")
    private String encodedPub;


    /**
     * 解绑银行卡
     *
     * @param bankInfoReqDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#bankInfoReqDTO.bankCardNo"}, expire = 3000, acquireTimeout = 4000)
    public void unbindSettleAcct(BankInfoReqDTO bankInfoReqDTO) {
        log.info("解绑银行卡参数：{}", bankInfoReqDTO);
        List<CustBankInfoEntity> custBankInfos = custBankInfoService.getBankInfoByLoanUserId(bankInfoReqDTO.getLoanUserId());
        if (custBankInfos.size() == 1) {
            throw new TfException("解绑银行卡失败，至少保留一张银行卡");
        } else {
            CustBankInfoEntity custBankInfo = custBankInfoService.getBankInfoByBankCardNoAndLoanUserId(bankInfoReqDTO.getBankCardNo(), bankInfoReqDTO.getLoanUserId());
            if (ObjectUtils.isNotEmpty(custBankInfo)) {
                log.info("删除绑定银行卡:{}", bankInfoReqDTO.getBankCardNo());
                LoanUserEntity loanUser = loanUserService.getById(bankInfoReqDTO.getLoanUserId());
                if (ObjectUtils.isNotEmpty(loanUser)) {
                    ReqDeleteSettleAcctParams deleteSettleAcctParams = new ReqDeleteSettleAcctParams();
                    deleteSettleAcctParams.setBankAcctNo(UnionPaySignUtil.SM2(encodedPub, bankInfoReqDTO.getBankCardNo()));
                    if (LoanUserTypeEnum.PERSONAL.getCode().equals(loanUser.getLoanUserType())) {
                        deleteSettleAcctParams.setCusId(loanUser.getCusId());
                    } else {
                        deleteSettleAcctParams.setMchId(loanUser.getMchId());
                    }
                    unionPayLoansApiService.deleteSettleAcct(deleteSettleAcctParams);
                } else {
                    throw new TfException("贷款用户不存在");
                }
                //标记删除银行卡
                custBankInfo.setDeleted(true);
                custBankInfoService.updateCustBankInfo(custBankInfo);
            } else {
                throw new TfException("解绑银行卡失败，银行卡不存在");
            }

        }

    }

    /**
     * 绑定银行卡
     *
     * @param bankInfoReqDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#bankInfoReqDTO.bankCardNo"}, expire = 3000, acquireTimeout = 4000)
    public boolean bindSettleAcct(BankInfoReqDTO bankInfoReqDTO) {
        log.info("绑定银行卡参数：{}", bankInfoReqDTO);
        CustBankInfoEntity bankInfoByBankCardNoAndLoanUserId = custBankInfoService.getBankInfoByBankCardNoAndLoanUserId(bankInfoReqDTO.getBankCardNo(), bankInfoReqDTO.getLoanUserId());
        if (bankInfoByBankCardNoAndLoanUserId != null) {
            throw new TfException("银行卡已存在");
        }
        List<CustBankInfoEntity> bankInfo = custBankInfoService.getBankInfoByLoanUserId(bankInfoReqDTO.getLoanUserId());
        CustBankInfoEntity custBankInfoEntity = new CustBankInfoEntity();
        BeanUtils.copyProperties(bankInfoReqDTO, custBankInfoEntity);
        if (CollUtil.isNotEmpty(bankInfo)) {
            String career = bankInfo.get(0).getCareer();
            custBankInfoEntity.setCareer(career);
        }
        try {
            UnionPayLoansSettleAcctDTO unionPayLoansSettleAcctDTO = unionPayLoansApiService.bindAddSettleAcct(custBankInfoEntity);
            //银行账号类型
            custBankInfoEntity.setSettlementType(Integer.parseInt(unionPayLoansSettleAcctDTO.getBankAcctType()));
        } catch (TfException ex) {
            throw new TfException(ex.getCode(), ex.getMessage());
        }
        return custBankInfoService.save(custBankInfoEntity);
    }
}
