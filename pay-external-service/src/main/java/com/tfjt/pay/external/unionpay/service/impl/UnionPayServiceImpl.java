package com.tfjt.pay.external.unionpay.service.impl;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.dto.req.*;
import com.tfjt.pay.external.unionpay.dto.resp.*;
import com.tfjt.pay.external.unionpay.enums.TransactionCodeEnum;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.pay.external.unionpay.utils.UnionPayBaseBuilderUtils;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 商户资金自主管理相关接口实现
 */
@Service
@Slf4j
public class UnionPayServiceImpl implements UnionPayService {


    @Resource
    private UnionPayBaseBuilderUtils unionPayBaseBuilderUtils;

    @Override
    public Result<ConsumerPoliciesRespDTO> mergeConsumerPolicies(ConsumerPoliciesReqDTO consumerPoliciesReqDTO) {
        try{
            ConsumerPoliciesRespDTO consumerPoliciesRespDTO = (ConsumerPoliciesRespDTO)unionPayBaseBuilderUtils.combination(
                    TransactionCodeEnum.LWZ634_COMBINED_GUARANTEE_PAYMENTS.getCode(),
                    JSON.toJSONString(consumerPoliciesReqDTO),
                    ConsumerPoliciesRespDTO.class);
            
            log.info("合并消费担保下单解析返回信息{}", consumerPoliciesRespDTO);
            return Result.ok(consumerPoliciesRespDTO);
        }catch (Exception e){
            log.error("合并消费担保下单报错{},{}", JSON.toJSON(consumerPoliciesReqDTO),e);
            return Result.failed(e.getMessage());
        }

    }

    @Override
    public Result<ConsumerPoliciesCheckRespDTO> mergeConsumerPoliciesCheck(ConsumerPoliciesCheckReqDTO consumerPoliciesReqDTO) {
        try{
            ConsumerPoliciesCheckRespDTO consumerPoliciesRespDTO = (ConsumerPoliciesCheckRespDTO)unionPayBaseBuilderUtils.combination(
                    TransactionCodeEnum.LWZ637_COMBINED_GUARANTEE_CONFIRMS.getCode(),
                    JSON.toJSONString(consumerPoliciesReqDTO),
                    ConsumerPoliciesCheckRespDTO.class);
            log.info("合并消费担保确认解析返回信息{}", consumerPoliciesRespDTO);
            return Result.ok(consumerPoliciesRespDTO);
        }catch (TfException e){
            log.error("合并消费担保确认报错{},{}", JSON.toJSON(consumerPoliciesReqDTO),e);
            return Result.failed(e.getMessage());
        }catch (Exception e){
            log.error("合并消费担保确认异常{},{}", JSON.toJSON(consumerPoliciesReqDTO),e);
            return Result.failed(e.getMessage());
        }

    }

    @Override
    public Result<WithdrawalCreateRespDTO> withdrawalCreation(WithdrawalCreateReqDTO withdrawalCreateReqDTO) {

        try{

            WithdrawalCreateRespDTO withdrawalCreateRespDTO = (WithdrawalCreateRespDTO)unionPayBaseBuilderUtils.combination(
                    TransactionCodeEnum.LWZ64_WITHDRAWALS_REQ.getCode(),
                    JSON.toJSONString(withdrawalCreateReqDTO),
                    WithdrawalCreateRespDTO.class);
            log.info("提现创建返回信息{}", withdrawalCreateRespDTO);
            return Result.ok(withdrawalCreateRespDTO);
        }catch (TfException e){
            log.error("提现创建报错{},{}", JSON.toJSON(withdrawalCreateReqDTO),e);
            return Result.failed(e.getMessage());
        }catch (Exception e){
            log.error("提现创建异常{},{}", JSON.toJSON(withdrawalCreateReqDTO),e);
            return Result.failed(e.getMessage());
        }
    }

    @Override
    public Result<ElectronicBookRespDTO> electronicBook(ElectronicBookReqDTO electronicBookReqDTO) {

        try{
            ElectronicBookRespDTO electronicBookRespDTO = (ElectronicBookRespDTO)unionPayBaseBuilderUtils.combination(
                    TransactionCodeEnum.LWZ623_BALANCE_TRANSACTIONS_REQ.getCode(),
                    JSON.toJSONString(electronicBookReqDTO),
                    ElectronicBookRespDTO.class);
            log.info("电子账簿流水查询返回信息{}", electronicBookRespDTO);
            return Result.ok(electronicBookRespDTO);
        }catch (TfException e){
            log.error("电子账簿流水查询报错{},{}", JSON.toJSON(electronicBookReqDTO),e);
            return Result.failed(e.getMessage());
        }catch (Exception e){
            log.error("电子账簿流水查询异常{},{}", JSON.toJSON(electronicBookReqDTO),e);
            return Result.failed(e.getMessage());
        }
    }

    @Override
    public Result<ConsumerPoliciesRespDTO> querySystemOrderStatus(String  combinedGuaranteePaymentId) {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("combinedGuaranteePaymentId",combinedGuaranteePaymentId);
            ConsumerPoliciesRespDTO consumerPoliciesRespDTO = (ConsumerPoliciesRespDTO)unionPayBaseBuilderUtils.combination(
                    null,
                    JSON.toJSONString(jsonObject),
                    ConsumerPoliciesRespDTO.class);
            log.info("使用系统订单号查询合并消费担保下单订单状态返回信息{}", consumerPoliciesRespDTO);
            return Result.ok(consumerPoliciesRespDTO);
        }catch (TfException e){
            log.error("使用系统订单号查询合并消费担保下单订单状态报错{},{}", JSON.toJSON(jsonObject),e);
            return Result.failed(e.getMessage());
        }catch (Exception e){
            log.error("使用系统订单号查询合并消费担保下单订单状态异常{},{}", JSON.toJSON(jsonObject),e);
            return Result.failed(e.getMessage());
        }
    }

    @Override
    public Result<ConsumerPoliciesRespDTO> queryPlatformOrderStatus(String combinedOutOrderNo) {

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("combinedOutOrderNo",combinedOutOrderNo);
            ConsumerPoliciesRespDTO consumerPoliciesRespDTO = (ConsumerPoliciesRespDTO)unionPayBaseBuilderUtils.combination(
                    TransactionCodeEnum.LWZ636_COMBINED_GUARANTEE_PAYMENTS_BY_OUT_ORDER_NO.getCode(),
                    JSON.toJSONString(jsonObject),
                    ConsumerPoliciesRespDTO.class);
            log.info("使用平台订单号查询合并消费担保下单订单状态返回信息{}", jsonObject);
            return Result.ok(consumerPoliciesRespDTO);
        }catch (TfException e){
            log.error("使用平台订单号查询合并消费担保下单订单状态报错{},{}", JSON.toJSON(jsonObject),e);
            return Result.failed(e.getMessage());
        }catch (Exception e){
            log.error("使用平台订单号查询合并消费担保下单订单状态异常{},{}", JSON.toJSON(jsonObject),e);
            return Result.failed(e.getMessage());
        }
    }

    @Override
    public LoanAccountDTO getLoanAccount(String balanceAcctId) {
        UnionPayBaseReq unionPayBaseReq = null;
        try {
            JSONObject param = new JSONObject();
            param.put("balanceAcctId",balanceAcctId);
            unionPayBaseReq = (UnionPayBaseReq)unionPayBaseBuilderUtils.baseBuilder(TransactionCodeEnum.LWZ511_RECEIPT_QUERY_REQ.getCode(), param.toJSONString());
            //调用银联接口
            ResponseEntity<UnionPayBaseResp> responseEntity = (ResponseEntity<UnionPayBaseResp>)unionPayBaseBuilderUtils.post(unionPayBaseReq);
            log.debug("调用电子账簿查询(电子账簿ID)返回信息<<<<<<<<<<<<<<<<<<{}", responseEntity);
            LoanAccountDTO loanAccountDTO = (LoanAccountDTO)unionPayBaseBuilderUtils.getBaseReturn(responseEntity,LoanAccountDTO.class);
            return loanAccountDTO;
        }catch (TfException e){
            log.error("调用电子账簿查询(电子账簿ID)返回 TfException{},{}", JSON.toJSON(unionPayBaseReq),e);
        }catch (Exception e){
            log.error("调用电子账簿查询(电子账簿ID)返回 Exception{},{}", JSON.toJSON(unionPayBaseReq),e);
        }
        return null;
    }


}
