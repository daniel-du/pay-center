package com.tfjt.pay.external.unionpay.service;


import com.tfjt.pay.external.unionpay.dto.req.*;
import com.tfjt.pay.external.unionpay.dto.resp.*;
import com.tfjt.tfcommon.dto.response.Result;

/**
 * @author Lzh
 * @version 1.0
 * @title UnionPayService
 * @description 商户资金自主管理相关接口
 * @Date 2023/8/12 17:24
 **/
public interface UnionPayService {
    /**
     * 合并消费担保下单
     * @param consumerPoliciesReqDTO
     * @return
     */
    public Result<ConsumerPoliciesRespDTO> mergeConsumerPolicies(ConsumerPoliciesReqDTO consumerPoliciesReqDTO);

    /**
     * 合并消费担保确认
     * @param consumerPoliciesReqDTO
     * @return
     */
    public Result<ConsumerPoliciesCheckRespDTO> mergeConsumerPoliciesCheck(ConsumerPoliciesCheckReqDTO consumerPoliciesReqDTO);

    /**
     * 提现创建
     * @param withdrawalCreateReqDTO
     * @return
     */
    public Result<WithdrawalCreateRespDTO> withdrawalCreation(WithdrawalCreateReqDTO withdrawalCreateReqDTO);

    /**
     * 电子账簿流水查询
     * @param electronicBookReqDTO
     * @return
     */
    public Result<ElectronicBookRespDTO> electronicBook(ElectronicBookReqDTO electronicBookReqDTO);

    /**
     * 本接口用于使用系统订单号查询合并消费担保下单订单状态
     * @param combinedGuaranteePaymentId 合并消费担保下单订单系统订单号
     * @return
     */
    public Result<ConsumerPoliciesRespDTO> querySystemOrderStatus(String  combinedGuaranteePaymentId);


    /**
     * 本接口用于使用平台订单号查询合并消费担保下单订单状态
     * @param combinedOutOrderNo 平台订单号
     * @return
     */
    public Result<ConsumerPoliciesRespDTO> queryPlatformOrderStatus(String combinedOutOrderNo);

    /**
     * 获取账户信息
     * @param balanceAcctId
     * @return
     */
    LoanAccountDTO getLoanAccount(String balanceAcctId);

    /**
     * 银联分账
     */
    void balanceDivide(UnionPayDivideReqDTO unionPayDivideReqDTO);
}
