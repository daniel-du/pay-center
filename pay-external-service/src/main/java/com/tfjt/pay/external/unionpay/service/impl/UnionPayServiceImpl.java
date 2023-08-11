package com.tfjt.pay.external.unionpay.service.impl;

import com.alibaba.fastjson.JSON;

import com.tfjt.pay.external.unionpay.dto.req.ConsumerPoliciesCheckReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.ConsumerPoliciesReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayBaseReq;
import com.tfjt.pay.external.unionpay.dto.req.WithdrawalCreateReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesCheckRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayBaseResp;
import com.tfjt.pay.external.unionpay.enums.TransactionCodeEnum;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.pay.external.unionpay.utils.UnionPayBaseBuilderUtils;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.http.ResponseEntity;

import javax.annotation.Resource;


/**
 * 商户资金自主管理相关接口实现
 */
@DubboService()
@Slf4j
public class UnionPayServiceImpl implements UnionPayService {


    @Resource
    private UnionPayBaseBuilderUtils unionPayBaseBuilderUtils;

    @Override
    public Result<ConsumerPoliciesRespDTO> mergeConsumerPolicies(ConsumerPoliciesReqDTO consumerPoliciesReqDTO) {
        UnionPayBaseReq unionPayBaseReq = null;
        try{
            unionPayBaseReq = (UnionPayBaseReq)unionPayBaseBuilderUtils.baseBuilder(TransactionCodeEnum.LWZ634_COMBINED_GUARANTEE_PAYMENTS.getCode(), JSON.toJSONString(consumerPoliciesReqDTO));
            log.info("合并消费担保下单入参{}", JSON.toJSON(unionPayBaseReq));
            //调用银联接口
            ResponseEntity<UnionPayBaseResp> responseEntity = (ResponseEntity<UnionPayBaseResp>)unionPayBaseBuilderUtils.post(unionPayBaseReq);
            log.info("合并消费担保下单返回值{}", responseEntity);
            ConsumerPoliciesRespDTO consumerPoliciesRespDTO = (ConsumerPoliciesRespDTO)unionPayBaseBuilderUtils.getBaseReturn(responseEntity,ConsumerPoliciesRespDTO.class);

            log.info("合并消费担保下单解析返回信息{}", consumerPoliciesRespDTO);
            return Result.ok(consumerPoliciesRespDTO);
        }catch (Exception e){
            log.error("合并消费担保下单报错{},{}", JSON.toJSON(unionPayBaseReq),e);
            return Result.failed(e.getMessage());
        }

    }

    @Override
    public Result<ConsumerPoliciesCheckRespDTO> mergeConsumerPoliciesCheck(ConsumerPoliciesCheckReqDTO consumerPoliciesReqDTO) {
        UnionPayBaseReq unionPayBaseReq = null;
        try{
            unionPayBaseReq = (UnionPayBaseReq)unionPayBaseBuilderUtils.baseBuilder(TransactionCodeEnum.LWZ637_COMBINED_GUARANTEE_CONFIRMS.getCode(), JSON.toJSONString(consumerPoliciesReqDTO));
            log.info("合并消费担保确认入参{}", JSON.toJSON(unionPayBaseReq));
            //调用银联接口
            ResponseEntity<UnionPayBaseResp> responseEntity = (ResponseEntity<UnionPayBaseResp>)unionPayBaseBuilderUtils.post(unionPayBaseReq);
            log.info("合并消费担保确认返回值{}", responseEntity);
            ConsumerPoliciesCheckRespDTO consumerPoliciesRespDTO = (ConsumerPoliciesCheckRespDTO)unionPayBaseBuilderUtils.getBaseReturn(responseEntity,ConsumerPoliciesRespDTO.class);

            log.info("合并消费担保确认解析返回信息{}", consumerPoliciesRespDTO);
            return Result.ok(consumerPoliciesRespDTO);
        }catch (TfException e){
            log.error("合并消费担保确认报错{},{}", JSON.toJSON(unionPayBaseReq),e);
            return Result.failed(e.getMessage());
        }catch (Exception e){
            log.error("合并消费担保确认异常{},{}", JSON.toJSON(unionPayBaseReq),e);
            return Result.failed(e.getMessage());
        }

    }

    @Override
    public ConsumerPoliciesRespDTO withdrawalCreation(WithdrawalCreateReqDTO withdrawalCreateReqDTO) {
        //LWZ64_WITHDRAWALS_REQ
        UnionPayBaseReq unionPayBaseReq = null;
        try{
            unionPayBaseReq = (UnionPayBaseReq)unionPayBaseBuilderUtils.baseBuilder(TransactionCodeEnum.LWZ64_WITHDRAWALS_REQ.getCode(), JSON.toJSONString(withdrawalCreateReqDTO));
            log.info("提现创建入参{}", JSON.toJSON(unionPayBaseReq));
            //调用银联接口
            ResponseEntity<UnionPayBaseResp> responseEntity = (ResponseEntity<UnionPayBaseResp>)unionPayBaseBuilderUtils.post(unionPayBaseReq);
            log.info("合并消费担保确认返回值{}", responseEntity);
            ConsumerPoliciesCheckRespDTO consumerPoliciesRespDTO = (ConsumerPoliciesCheckRespDTO)unionPayBaseBuilderUtils.getBaseReturn(responseEntity,ConsumerPoliciesRespDTO.class);

            log.info("合并消费担保确认解析返回信息{}", consumerPoliciesRespDTO);
            return null;//Result.ok(consumerPoliciesRespDTO);
        }catch (TfException e){
            log.error("合并消费担保确认报错{},{}", JSON.toJSON(unionPayBaseReq),e);
            return null;//Result.ok(consumerPoliciesRespDTO);
        }catch (Exception e){
            log.error("合并消费担保确认异常{},{}", JSON.toJSON(unionPayBaseReq),e);
            return null;//Result.ok(consumerPoliciesRespDTO);
        }
    }

    @Override
    public ConsumerPoliciesRespDTO electronicBook(ConsumerPoliciesReqDTO consumerPoliciesReqDTO) {
        return null;
    }

    @Override
    public ConsumerPoliciesRespDTO queryOrderStatus(ConsumerPoliciesReqDTO consumerPoliciesReqDTO) {
        return null;
    }

}
