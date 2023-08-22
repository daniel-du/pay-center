package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.req.ConsumerPoliciesReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.LoanOrderUnifiedorderReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.LoanTransferRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesRespDTO;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;
import com.tfjt.tfcommon.dto.response.Result;
import org.springframework.stereotype.Service;

/**
 * @author songx
 * @date 2023-08-21 18:49
 * @email 598482054@qq.com
 */
@Service
public interface LoanOrderBiz {
    /**
     * 保存转账参数
     * @param loanTransferRespDTO
     * @param tradeOrderNo
     */
    void transferSaveOrder(LoanTransferRespDTO loanTransferRespDTO, String tradeOrderNo);

    /**
     * 验证指定的单号是否存在
     * @param businessOrderNo 业务单号
     * @param appId  应用id
     * @return  true 存在  false 不存在
     */
    boolean checkExistBusinessOrderNo(String businessOrderNo, String appId);

    /**
     * 根据业务单号和业务应用 获取订单信息
     * @param businessOrderNo 业务单号
     * @param appId         业务appid
     * @return 订单信息
     */
    LoanOrderEntity getByBusinessAndAppId(String businessOrderNo, String appId);

    /**
     * 保存下单商品信息
     * 并生成调用银联下单接口参数
     *
     * @param loanOrderUnifiedorderReqDTO    商品订单信息
     * @return 调用银联参数
     */
     ConsumerPoliciesReqDTO unifiedorderSaveOrderAndBuildUnionPayParam(LoanOrderUnifiedorderReqDTO loanOrderUnifiedorderReqDTO);
    /**
     * 修改订单状态
     *
     * @param result 银联合并下单返回数据
     * @param appId appId
     */
    void saveMergeConsumerResult(Result<ConsumerPoliciesRespDTO> result, String appId);
}
