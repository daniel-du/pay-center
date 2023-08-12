package com.tfjt.pay.external.unionpay.service;


import com.tfjt.pay.external.unionpay.dto.req.ConsumerPoliciesCheckReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.ConsumerPoliciesReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.WithdrawalCreateReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesCheckRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesRespDTO;
import com.tfjt.tfcommon.dto.response.Result;

/**
 * 商户资金自主管理相关接口
 */
public interface UnionPayService {
    /**
     * 合并消费担保下单
     */
    public Result<ConsumerPoliciesRespDTO> mergeConsumerPolicies(ConsumerPoliciesReqDTO consumerPoliciesReqDTO);

    /**
     * 合并消费担保确认
     */
    public Result<ConsumerPoliciesCheckRespDTO> mergeConsumerPoliciesCheck(ConsumerPoliciesCheckReqDTO consumerPoliciesReqDTO);

    /**
     * 提现创建
     */
    public ConsumerPoliciesRespDTO withdrawalCreation(WithdrawalCreateReqDTO withdrawalCreateReqDTO);

    /**
     * 电子账簿流水查询
     */
    public ConsumerPoliciesRespDTO electronicBook(ConsumerPoliciesReqDTO consumerPoliciesReqDTO);

    /**
     * 查询订单状态
     */
    public ConsumerPoliciesRespDTO queryOrderStatus(ConsumerPoliciesReqDTO consumerPoliciesReqDTO);
}
