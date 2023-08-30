package com.tfjt.pay.external.unionpay.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.api.dto.req.*;
import com.tfjt.pay.external.unionpay.api.dto.resp.*;
import com.tfjt.pay.external.unionpay.api.service.UnionPayApiService;
import com.tfjt.pay.external.unionpay.biz.LoanOrderBiz;
import com.tfjt.pay.external.unionpay.biz.PayBalanceDivideBiz;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansBizService;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.*;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.LoanAccountDTO;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.LoanUnionpayCheckBillService;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.*;

/**
 * 银联接口服务实现类
 *
 * @author songx
 * @date 2023-08-10 17:02
 * @email 598482054@qq.com
 */
@Slf4j
@DubboService
public class UnionPayApiServiceImpl implements UnionPayApiService {
    @Resource
    private TfAccountConfig accountConfig;
    @Resource
    private UnionPayService unionPayService;
    @Resource
    private LoanUnionpayCheckBillService loanUnionpayCheckBillService;

    @Resource
    private LoanOrderBiz loanOrderBiz;

    @Resource
    private PayBalanceDivideBiz payBalanceDivideBiz;

    @Autowired
    private LoanUserService loanUserService;




    @Resource
    private UnionPayLoansBizService unionPayLoansBizService;

    @Override
    public Result<String> transfer(UnionPayTransferRespDTO payTransferDTO) {
        log.info("转账接收参数:{}", JSONObject.toJSONString(payTransferDTO));
        try {
            return loanOrderBiz.transfer(payTransferDTO);
        } catch (TfException e) {
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }
    }

    @Override
    public Result<Integer> currentBalance() {
        BalanceAcctRespDTO balanceAcctDTOByAccountId = getBalanceAcctDTOByAccountId(accountConfig.getBalanceAcctId());
        if (Objects.isNull(balanceAcctDTOByAccountId)) {
            throw new TfException(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
        }
        log.debug("查询母账户交易余额返回:{}", balanceAcctDTOByAccountId.getSettledAmount());
        return Result.ok(balanceAcctDTOByAccountId.getSettledAmount());
    }

    @Override
    public Result<BalanceAcctRespDTO> getBalanceByAccountId(String balanceAcctId) {
        log.debug("查询电子账簿id:{}", balanceAcctId);
        if (StringUtil.isBlank(balanceAcctId)) {
            return Result.failed(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
        }
        BalanceAcctRespDTO balanceAcctDTO = getBalanceAcctDTOByAccountId(balanceAcctId);
        if (Objects.isNull(balanceAcctDTO)) {
            String message = String.format("[%s]电子账簿信息不存在", balanceAcctId);
            log.error(String.format(message));
            return Result.failed(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
        }
        return Result.ok(balanceAcctDTO);
    }

    @Override
    public Result<Map<String, BalanceAcctRespDTO>> listBalanceByAccountIds(List<String> balanceAcctIds) {
        log.info("批量查询电子账户参数信息:{}", JSONObject.toJSONString(balanceAcctIds));
        if (CollectionUtil.isEmpty(balanceAcctIds)) {
            return Result.failed(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
        }
        Map<String, BalanceAcctRespDTO> result = new HashMap<>(balanceAcctIds.size());
        for (String balanceAcctId : balanceAcctIds) {
            BalanceAcctRespDTO balanceAcctDTOByAccountId = getBalanceAcctDTOByAccountId(balanceAcctId);
            result.put(balanceAcctId, balanceAcctDTOByAccountId);
        }
        log.info("批量查询电子账户返回信息:{}", JSONObject.toJSONString(result));
        return Result.ok(result);
    }

    @Override
    public Result<Map<String, SubBalanceDivideRespDTO>> balanceDivide(UnionPayBalanceDivideReqDTO balanceDivideReq) {
        log.info("请求分账参数<<<<<<<<<<<<<<<<:{}", JSONObject.toJSONString(balanceDivideReq));
        try {
            return payBalanceDivideBiz.balanceDivide(balanceDivideReq);
        } catch (TfException e) {
            //交易失败的状态
            return Result.failed(e.getMessage());
        } catch (Exception e) {
            log.error("调用银联异常分账接口异常:{}", e.getMessage());
        }
        return Result.failed(PayExceptionCodeEnum.UNIONPAY_RESPONSE_ERROR);
    }


    /**
     * 提现
     * @param withdrawalReqDTO 提现参数
     * @return 提现结果
     */
    @Override
    public Result<WithdrawalRespDTO> withdrawalCreation(WithdrawalReqDTO withdrawalReqDTO) {
        return this.unionPayLoansBizService.withdrawalCreation(withdrawalReqDTO);
    }

    @Override
    public Result<LoanQueryOrderRespDTO> orderQuery(String businessOrderNo, String appId) {
        log.info("查询交易结果信息:{}", businessOrderNo);
        try {
            LoanOrderEntity one = this.loanOrderBiz.getByBusinessAndAppId(businessOrderNo, appId);
            LoanQueryOrderRespDTO loanQueryOrderRespDTO = new LoanQueryOrderRespDTO();
            if (one == null) {
                loanQueryOrderRespDTO.setResult_code(TradeResultConstant.PAY_FAILED);
                return Result.ok(loanQueryOrderRespDTO);
            }
            loanQueryOrderRespDTO.setBusiness_type(one.getBusinessType());
            loanQueryOrderRespDTO.setOut_trade_no(businessOrderNo);
            loanQueryOrderRespDTO.setTransaction_id(one.getCombinedGuaranteePaymentId());
            loanQueryOrderRespDTO.setPay_balanceAcct_id(one.getPayBalanceAcctId());
            loanQueryOrderRespDTO.setMetadata(one.getMetadata());
            loanQueryOrderRespDTO.setPay_balance_acct_name(one.getPayBalanceAcctName());
            loanQueryOrderRespDTO.setTotal_fee(one.getAmount());
            if (TradeResultConstant.UNIONPAY_UNKNOWN.equals(one.getStatus())) {
                Result<ConsumerPoliciesRespDTO> consumerPoliciesRespDTOResult = unionPayService.queryPlatformOrderStatus(one.getTradeOrderNo());
                int code = consumerPoliciesRespDTOResult.getCode();
                if (code == NumberConstant.ONE) {
                    ConsumerPoliciesRespDTO data = consumerPoliciesRespDTOResult.getData();
                    loanQueryOrderRespDTO.setResult_code(TradeResultConstant.UNIONPAY_SUCCEEDED.equals(data.getStatus()) ? TradeResultConstant.PAY_SUCCESS : TradeResultConstant.PAY_FAILED);

                } else {
                    return Result.failed(consumerPoliciesRespDTOResult.getMsg());
                }
            } else {
                loanQueryOrderRespDTO.setResult_code(TradeResultConstant.UNIONPAY_SUCCEEDED.equals(one.getStatus()) ? TradeResultConstant.PAY_SUCCESS : TradeResultConstant.PAY_FAILED);
            }
            List<LoanOrderDetailsRespDTO>  details_dto_list = this.loanOrderBiz.listLoanOrderDetailsRespDTO(one.getId());
            loanQueryOrderRespDTO.setDetails_dto_list(details_dto_list);
            loanQueryOrderRespDTO.setTread_type(PayTypeConstants.PAY_TYPE_LOAN);
            return Result.ok(loanQueryOrderRespDTO);
        } catch (TfException e) {
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }
    }


    @Override
    public Result<MergeConsumerRepDTO> unifiedorder(UnionPayLoanOrderUnifiedorderReqDTO loanOrderUnifiedorderDTO) {
        return this.loanOrderBiz.unifiedorder(loanOrderUnifiedorderDTO);
    }

    @Override
    public Result<String> downloadCheckBill(UnionPayCheckBillReqDTO date) {
        LoanUnionpayCheckBillEntity byDateAndAccountId = loanUnionpayCheckBillService.getByDateAndAccountId(date.getDate(), accountConfig.getBalanceAcctId());
        if (Objects.isNull(byDateAndAccountId)) {
            return Result.failed(PayExceptionCodeEnum.UNIONPAY_CHECK_BILL_NOT_FOUND);
        }
        if (Objects.equals(NumberConstant.ONE, byDateAndAccountId.getStatus())) {
            return Result.ok(byDateAndAccountId.getUrl());
        }
        return Result.failed(byDateAndAccountId.getReason());
    }




    /**
     * 获取指定电子账簿的账户信息
     *
     * @param balanceAcctId 账户账户id
     * @return 电子账户信息
     */
    private BalanceAcctRespDTO getBalanceAcctDTOByAccountId(String balanceAcctId) {

        LoanAccountDTO loanAccountDTO = null;
        try {
            loanAccountDTO = unionPayService.getLoanAccount(balanceAcctId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Objects.isNull(loanAccountDTO)) {
            return null;
        }
        BalanceAcctRespDTO balanceAcctDTO = new BalanceAcctRespDTO();
        BeanUtil.copyProperties(loanAccountDTO, balanceAcctDTO);
        if (!balanceAcctId.equals(accountConfig.getBalanceAcctId())) {
            LoanUserEntity user = loanUserService.getByBalanceAcctId(balanceAcctId);
            if (!Objects.isNull(user)) {
                balanceAcctDTO.setBalanceAcctName(user.getName());
                balanceAcctDTO.setType(user.getType());
            }
        }
        return balanceAcctDTO;
    }




}
