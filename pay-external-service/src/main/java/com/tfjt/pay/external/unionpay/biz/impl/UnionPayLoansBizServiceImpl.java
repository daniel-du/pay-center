package com.tfjt.pay.external.unionpay.biz.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.tfjt.pay.external.unionpay.api.dto.req.WithdrawalReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.BankInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.CustBankInfoRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.UnionPayLoansSettleAcctDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.WithdrawalRespDTO;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansBizService;
import com.tfjt.pay.external.unionpay.constants.CommonConstants;
import com.tfjt.pay.external.unionpay.constants.UnionPayTradeResultCodeConstant;
import com.tfjt.pay.external.unionpay.dto.ReqDeleteSettleAcctParams;
import com.tfjt.pay.external.unionpay.dto.req.WithdrawalCreateReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.WithdrawalCreateRespDTO;
import com.tfjt.pay.external.unionpay.entity.CustBankInfoEntity;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceAcctEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.entity.LoanWithdrawalOrderEntity;
import com.tfjt.pay.external.unionpay.enums.*;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.pay.external.unionpay.utils.MD5Util;
import com.tfjt.pay.external.unionpay.utils.UnionPaySignUtil;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.util.InstructIdUtil;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Resource
    RedisCache redisCache;

    @Resource
    LoanWithdrawalOrderService withdrawalOrderService;

    @Resource
    LoanBalanceAcctService loanBalanceAcctService;

    @Resource
    private UnionPayService unionPayService;

    @Value("${unionPay.loan.notifyUrl}")
    private String notifyUrl;
    private final static String WITHDRAWAL_IDEMPOTENT_KEY = "idempotent:withdrawal:";


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
        LoanUserEntity loanUser = loanUserService.getLoanUserByBusIdAndType(bankInfoReqDTO.getBusId(), bankInfoReqDTO.getType());
        if (loanUser == null) {
            throw new TfException(PayExceptionCodeEnum.NO_LOAN_USER);
        }
        log.info("解绑银行卡参数：{}", bankInfoReqDTO);
        List<CustBankInfoEntity> custBankInfos = custBankInfoService.getBankInfoByLoanUserId(loanUser.getId());
        if (custBankInfos.size() == 1) {
            throw new TfException(PayExceptionCodeEnum.LAST_ONE_BANK_CARD);
        } else {
            CustBankInfoEntity custBankInfo = custBankInfoService.getBankInfoByBankCardNoAndLoanUserId(bankInfoReqDTO.getBankCardNo(), loanUser.getId());
            if (ObjectUtils.isNotEmpty(custBankInfo)) {
                log.info("删除绑定银行卡:{}", bankInfoReqDTO.getBankCardNo());
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
                    throw new TfException(PayExceptionCodeEnum.NO_LOAN_USER);
                }
                //标记删除银行卡
                custBankInfo.setDeleted(true);
                custBankInfoService.updateCustBankInfo(custBankInfo);
            } else {
                throw new TfException(PayExceptionCodeEnum.ABSENT_BANK_CARD);
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
    public String bindSettleAcct(BankInfoReqDTO bankInfoReqDTO) {
        LoanUserEntity loanUser = loanUserService.getLoanUserByBusIdAndType(bankInfoReqDTO.getBusId(), bankInfoReqDTO.getType());
        if (loanUser == null) {
            throw new TfException(PayExceptionCodeEnum.NO_LOAN_USER);
        }
        log.info("绑定银行卡参数：{}", bankInfoReqDTO);
        CustBankInfoEntity bankInfoByBankCardNoAndLoanUserId = custBankInfoService.getBankInfoByBankCardNoAndLoanUserId(bankInfoReqDTO.getBankCardNo(), loanUser.getId());
        if (bankInfoByBankCardNoAndLoanUserId != null) {
            throw new TfException(PayExceptionCodeEnum.EXISTED_BANK_CARD);
        }
        List<CustBankInfoEntity> bankInfo = custBankInfoService.getBankInfoByLoanUserId(loanUser.getId());
        CustBankInfoEntity custBankInfoEntity = new CustBankInfoEntity();
        BeanUtils.copyProperties(bankInfoReqDTO, custBankInfoEntity);
        if (CollUtil.isNotEmpty(bankInfo)) {
            String career = bankInfo.get(0).getCareer();
            custBankInfoEntity.setCareer(career);
            custBankInfoEntity.setLoanUserId(loanUser.getId());
            custBankInfoEntity.setSettlementType(Integer.parseInt(bankInfoReqDTO.getSettlementType()));
            custBankInfoEntity.setAccountName(bankInfoReqDTO.getAccountName());
        }
        UnionPayLoansSettleAcctDTO unionPayLoansSettleAcctDTO ;
        try {
            unionPayLoansSettleAcctDTO = unionPayLoansApiService.bindAddSettleAcct(custBankInfoEntity);
            //银行账号类型
            custBankInfoEntity.setSettlementType(Integer.parseInt(unionPayLoansSettleAcctDTO.getBankAcctType()));
            custBankInfoEntity.setSettleAcctId(unionPayLoansSettleAcctDTO.getSettleAcctId());
        } catch (TfException ex) {
            throw new TfException(ex.getCode(), ex.getMessage());
        }
        custBankInfoService.save(custBankInfoEntity);
        return unionPayLoansSettleAcctDTO.getSettleAcctId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<WithdrawalRespDTO> withdrawalCreation(WithdrawalReqDTO withdrawalReqDTO) {
        LoanUserEntity loanUser = loanUserService.getLoanUserByBusIdAndType(withdrawalReqDTO.getBusId(), withdrawalReqDTO.getType());
        if (loanUser == null) {
            WithdrawalRespDTO withdrawalRespDTO = new WithdrawalRespDTO();
            withdrawalRespDTO.setStatus(String.valueOf(PayExceptionCodeEnum.NO_LOAN_USER.getCode()));
            withdrawalRespDTO.setReason(PayExceptionCodeEnum.NO_LOAN_USER.getMsg());
            return Result.ok(withdrawalRespDTO);
        }
        String outOrderNo;
        String isIdempotent = redisCache.getCacheString(WITHDRAWAL_IDEMPOTENT_KEY + loanUser.getId());

        if (ObjectUtils.isEmpty(withdrawalReqDTO.getOrderNo())) {
            outOrderNo = InstructIdUtil.getInstructId(CommonConstants.LOAN_REQ_NO_PREFIX, new Date(), UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_30, redisCache);
            String md5Str = withdrawalReqDTO.getBusId() + ":" + withdrawalReqDTO.getType() + ":" + withdrawalReqDTO.getAmount();
            log.info("防重复提交加密前的M5d值为：{}", md5Str);
            String idempotentMd5 = MD5Util.getMD5String(md5Str);
            log.info("防重复提交加密后的M5d值为：{}", idempotentMd5);
            if (StringUtils.isEmpty(isIdempotent)) {
                redisCache.setCacheString(WITHDRAWAL_IDEMPOTENT_KEY + loanUser.getId(), idempotentMd5, 60, TimeUnit.MINUTES);
            } else if (idempotentMd5.equals(isIdempotent)) {
                log.info("重复提现了！！！");
                WithdrawalRespDTO withdrawalRespDTO = new WithdrawalRespDTO();
                withdrawalRespDTO.setStatus(String.valueOf(PayExceptionCodeEnum.REPEAT_OPERATION.getCode()));
                withdrawalRespDTO.setReason(PayExceptionCodeEnum.REPEAT_OPERATION.getMsg());
                return Result.failed(withdrawalRespDTO);
            }
        } else {
            outOrderNo = withdrawalReqDTO.getOrderNo();
            log.info("防重复值：{}", outOrderNo);
            if (StringUtils.isEmpty(isIdempotent)) {
                redisCache.setCacheString(WITHDRAWAL_IDEMPOTENT_KEY + loanUser.getId(), outOrderNo, 60, TimeUnit.MINUTES);
            } else if (outOrderNo.equals(isIdempotent)) {
                log.info("重复提现了！！！");
                WithdrawalRespDTO withdrawalRespDTO = new WithdrawalRespDTO();
                withdrawalRespDTO.setStatus(String.valueOf(PayExceptionCodeEnum.REPEAT_OPERATION.getCode()));
                withdrawalRespDTO.setReason(PayExceptionCodeEnum.REPEAT_OPERATION.getMsg());
                return Result.failed(withdrawalRespDTO);
            }
        }

        LoanBalanceAcctEntity accountBook = loanBalanceAcctService.getAccountBookByLoanUserId(loanUser.getId());
        CustBankInfoEntity bankInfo = custBankInfoService.getById(withdrawalReqDTO.getBankInfoId());
        WithdrawalCreateReqDTO withdrawalCreateReqDTO = new WithdrawalCreateReqDTO();
        withdrawalCreateReqDTO.setOutOrderNo(outOrderNo);
        Date now = new Date();
        withdrawalCreateReqDTO.setSentAt(DateUtil.getByRFC3339(now));
        withdrawalCreateReqDTO.setAmount(withdrawalReqDTO.getAmount());
        withdrawalCreateReqDTO.setServiceFee(null);
        withdrawalCreateReqDTO.setBalanceAcctId(accountBook.getBalanceAcctId());//电子账簿ID
        withdrawalCreateReqDTO.setBusinessType(UnionPayBusinessTypeEnum.WITHDRAWAL.getCode());
        withdrawalCreateReqDTO.setBankAcctNo(UnionPaySignUtil.SM2(encodedPub, bankInfo.getBankCardNo()));//提现目标银行账号 提现目标银行账号需要加密处理  6228480639353401873
        withdrawalCreateReqDTO.setMobileNumber(UnionPaySignUtil.SM2(encodedPub, bankInfo.getPhone())); //手机号 需要加密处理
        withdrawalCreateReqDTO.setRemark("");
        Map<String, Object> map = new HashMap<>();
        map.put("notifyUrl", notifyUrl);
        withdrawalCreateReqDTO.setExtra(map);
        //插入业务表
        LoanWithdrawalOrderEntity loanWithdrawalOrderEntity = BeanUtil.copyProperties(withdrawalCreateReqDTO, LoanWithdrawalOrderEntity.class);
        loanWithdrawalOrderEntity.setBankAcctNo(bankInfo.getBankCardNo());
        loanWithdrawalOrderEntity.setMobileNumber(bankInfo.getPhone());
        loanWithdrawalOrderEntity.setAppId(withdrawalReqDTO.getAppId());
        loanWithdrawalOrderEntity.setSendAt(now);
        loanWithdrawalOrderEntity.setWithdrawalOrderNo(withdrawalCreateReqDTO.getOutOrderNo());
        log.info("银联提现参数插入业务表:{}", JSON.toJSONString(loanWithdrawalOrderEntity));
        withdrawalOrderService.save(loanWithdrawalOrderEntity);
        log.info("银联提现参数:{}", JSON.toJSONString(withdrawalCreateReqDTO));
        Result<WithdrawalCreateRespDTO> withdrawalCreateResp = null;
        try {
            withdrawalCreateResp = unionPayService.withdrawalCreation(withdrawalCreateReqDTO);
            WithdrawalRespDTO withdrawalRespDTO = new WithdrawalRespDTO();
            withdrawalRespDTO.setWithdrawalOrderNo(outOrderNo);
            withdrawalRespDTO.setWithdrawalId(withdrawalCreateResp.getData().getWithdrawalId());
            withdrawalRespDTO.setStatus(withdrawalCreateResp.getData().getStatus());
            //更新状态
            loanWithdrawalOrderEntity.setStatus(withdrawalRespDTO.getStatus());
            withdrawalOrderService.updateById(loanWithdrawalOrderEntity);
            return Result.ok(withdrawalRespDTO);
        } catch (TfException e) {
            log.info("提现失败,删除redis:{}", WITHDRAWAL_IDEMPOTENT_KEY + loanUser.getId());
            redisCache.deleteObject(WITHDRAWAL_IDEMPOTENT_KEY + loanUser.getId());
            log.info("提现失败,错误码:{},错误信息:{}", e.getCode(), e.getMessage());
            return Result.failed(e.getCode(), e.getMessage());
        }

    }

    @Override
    public List<CustBankInfoRespDTO> getBankInfoByLoanUserId(Long loanUserId) {
        List<CustBankInfoEntity> list = custBankInfoService.getBankInfoByLoanUserId(loanUserId);
        List<CustBankInfoRespDTO> custBankInfoRespDTOList = new ArrayList<>();
        list.forEach(custBankInfoEntity -> {
            //只获取打款验证成功的银行卡
            if(ValidateStatusEnum.YES.getCode().equals(custBankInfoEntity.getValidateStatus())){
                CustBankInfoRespDTO custBankInfoRespDTO = new CustBankInfoRespDTO();
                BeanUtil.copyProperties(custBankInfoEntity,custBankInfoRespDTO);
                custBankInfoRespDTOList.add(custBankInfoRespDTO);
            }
        });
        return custBankInfoRespDTOList;
    }
}
