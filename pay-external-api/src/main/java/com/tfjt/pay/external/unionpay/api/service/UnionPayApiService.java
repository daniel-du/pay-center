package com.tfjt.pay.external.unionpay.api.service;

import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayBalanceDivideReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayCheckBillReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayLoanOrderUnifiedorderReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.WithdrawalReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.*;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.List;
import java.util.Map;

/**
 * 银联dubbo接口层
 * @author songx
 * @date 2023-08-10 17:02
 * @email 598482054@qq.com
 */
public interface UnionPayApiService {

    /**
     * 转账接口
     * @param payTransferDTO
     */
    Result<String> transfer(UnionPayTransferRespDTO payTransferDTO);

    /**
     * 获取同福母账户当前账户余额
     * @return
     */
    Result<Integer> currentBalance();

    /**
     * 获取同福母账户信息
     * @return
     */
    Result<ParentBalanceRespDTO> currentBalanceInfo();


    /**
     * 获取指定电子账簿id的账户信息
     * @param balanceAcctId 电子账簿id
     * @return
     */
    Result<BalanceAcctRespDTO> getBalanceByAccountId(String balanceAcctId);

    /**
     * 批量获取指定电子账簿id的账户信息
     * @return
     */
    Result<Map<String, BalanceAcctRespDTO>> listBalanceByAccountIds(List<String> balanceAcctIds);

    /**
     * 资金分账操作
     * @param balanceDivideReq
     * @return
     */
    Result<Map<String, SubBalanceDivideRespDTO>> balanceDivide(UnionPayBalanceDivideReqDTO balanceDivideReq);

    /**
     *  下单接口
     * @param loanOrderUnifiedorderDTO
     * @return
     */
    Result<MergeConsumerRepDTO> unifiedorder(UnionPayLoanOrderUnifiedorderReqDTO loanOrderUnifiedorderDTO);


    /**
     * 下载对账单
     * @param date
     * @return
     */
    Result<String> downloadCheckBill(UnionPayCheckBillReqDTO date);

    /**
     * 提现
     * @param withdrawalReqDTO
     * @return
     */
    Result<WithdrawalRespDTO> withdrawalCreation(WithdrawalReqDTO withdrawalReqDTO);

    /**
     * 查询
     * @return
     */
    Result<LoanQueryOrderRespDTO> orderQuery(String businessOrderNo,String appId);


}
