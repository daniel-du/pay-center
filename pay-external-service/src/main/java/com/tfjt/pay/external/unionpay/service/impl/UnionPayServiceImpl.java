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
            log.info("进入合并消费担保下单");
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
            log.info("进入合并消费担保确认");
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
            log.info("进入提现创建");
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
            log.info("进入电子账簿流水查询");
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
            log.info("进入使用系统订单号查询合并消费担保下单订单状态");
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
            log.info("使用平台订单号查询合并消费担保下单订单状态");
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
        JSONObject param = new JSONObject();
        try {
            param.put("balanceAcctId",balanceAcctId);
            log.info("调用电子账簿查询(电子账簿ID){}",JSON.toJSONString(param));
            LoanAccountDTO loanAccountDTO = (LoanAccountDTO)unionPayBaseBuilderUtils.combination(
                    TransactionCodeEnum.LWZ511_RECEIPT_QUERY_REQ.getCode(),
                    JSON.toJSONString(param),
                    LoanAccountDTO.class);
            return loanAccountDTO;
        }catch (TfException e){
            log.error("调用电子账簿查询(电子账簿ID)返回 TfException{},{}", JSON.toJSON(param),e);
        }catch (Exception e){
            log.error("调用电子账簿查询(电子账簿ID)返回 Exception{},{}", JSON.toJSON(param),e);
        }
        return null;
    }

    @Override
    public Result<UnionPayDivideRespDTO> balanceDivide(UnionPayDivideReqDTO unionPayDivideReqDTO) {
        try{
            UnionPayDivideRespDTO unionPayDivideRespDTO = (UnionPayDivideRespDTO)unionPayBaseBuilderUtils.combination(TransactionCodeEnum.LWZ616_ALLOCATIONS.getCode(),
                    JSON.toJSONString(unionPayDivideReqDTO),UnionPayDivideRespDTO.class);
            return Result.ok(unionPayDivideRespDTO);
        } catch (TfException e){
            log.error("调用分账返回 TfException{},{}", JSON.toJSON(unionPayDivideReqDTO),e);
            return Result.failed(e.getMessage());
        }catch (Exception e){
            log.error("调用分账返回 Exception{},{}", JSON.toJSON(unionPayDivideReqDTO),e);
            return Result.failed(e.getMessage());
        }
    }

    @Override
    public Result<WithdrawalCreateRespDTO> getWithdrawal(String outOrderNo) {
        JSONObject param = new JSONObject();
        try{
            param.put("outOrderNo",outOrderNo);
            WithdrawalCreateRespDTO withdrawalCreateRespDTO = (WithdrawalCreateRespDTO)unionPayBaseBuilderUtils.combination(TransactionCodeEnum.LWZ66_WITHDRAWALS_BY_OUT_ORDER_NO.getCode(),
                    JSON.toJSONString(param),WithdrawalCreateRespDTO.class);
            return Result.ok(withdrawalCreateRespDTO);
        } catch (TfException e){
            log.error("调用提现查询返回 TfException{},{}", JSON.toJSON(param),e);
            return Result.failed(e.getMessage());
        }catch (Exception e){
            log.error("调用提现查询返回 Exception{},{}", JSON.toJSON(param),e);
            return Result.failed(e.getMessage());
        }
    }

    @Override
    public Result<String> downloadCheckBill(String format) {
        JSONObject param = new JSONObject();
        try {
            param.put("billDate",format);
            JSONObject combination = (JSONObject)unionPayBaseBuilderUtils.combination(TransactionCodeEnum.LWZ91_RECEIPT_QUERY_REQ.getCode(), param.toJSONString(), JSONObject.class);
            return Result.ok(combination.getString("downloadUrl"));
        }catch (TfException e){
            log.error("调用下载对账单查询返回 TfException{},{}", JSON.toJSON(param),e);
            return Result.failed(e.getMessage());
        }catch (Exception e){
            log.error("调用下载对账单查询返回 Exception{},{}", JSON.toJSON(param),e);
            return Result.failed(e.getMessage());
        }


    }


}
