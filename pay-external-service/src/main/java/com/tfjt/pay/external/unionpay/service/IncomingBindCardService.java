package com.tfjt.pay.external.unionpay.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.tfjt.pay.external.unionpay.constants.PnSdkConstant;
import com.tfjt.pay.external.unionpay.dto.CheckCodeMessageDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingCheckCodeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSubmitMessageReqDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.tfjt.pay.external.unionpay.enums.ExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.enums.PnApiEnum;
import com.tfjt.pay.external.unionpay.utils.PnHeadUtils;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 20:45
 * @description 进件绑卡服务
 */
@Slf4j
public abstract class IncomingBindCardService {

    @Autowired
    TfIncomingInfoService tfIncomingInfoService;

    /**
     * 绑定银行卡、获取验证码
     * @return
     */
    abstract public boolean incomingSubmit(IncomingSubmitMessageDTO incomingSubmitMessageDTO);

    /**
     * 回填校验验证码、打款金额
     * @return
     */
    abstract public boolean checkCode(CheckCodeMessageDTO checkCodeMessageDTO);

    /**
     * 平安开通账户
     * @param json
     * @return
     */
    public String openAccount(TfIncomingInfoEntity tfIncomingInfoEntity, JSONObject json) {
        try {
            //调用平安6248-开户接口
            JSONObject resultJson = PnHeadUtils.send(json,
                    PnApiEnum.OPEN_ACCOUNT.getServiceCode(), PnApiEnum.OPEN_ACCOUNT.getServiceId());
            //平安api返回标识非成功
            if (!PnSdkConstant.API_SUCCESS_CODE.equals(resultJson.getString(PnSdkConstant.RESULT_CODE_FIELD))) {
                //记录错误原因
                JSONObject errorJson = PnHeadUtils.getError(resultJson);
                tfIncomingInfoEntity.setFailReason(errorJson.toJSONString());
                tfIncomingInfoEntity.setFailTime(LocalDateTime.now());
                tfIncomingInfoService.updateById(tfIncomingInfoEntity);
                throw new TfException(errorJson.getString(PnSdkConstant.RESULT_ERROR_MSG_FIELD));
            }
            JSONObject dataJson = resultJson.getJSONObject(PnSdkConstant.RESULT_DATA_FIELD);
            if (ObjectUtils.isEmpty(dataJson)) {
                throw new TfException("返回数据为空");
            }
            if (StringUtils.isBlank(dataJson.getString(PnSdkConstant.RESULT_SUB_ACCT_NO_FIELD))) {
                throw new TfException("返回子账户号为空");
            }
            return dataJson.getString(PnSdkConstant.RESULT_SUB_ACCT_NO_FIELD);
        } catch (TfException e) {
            log.error("IncomingBindCardService--openAccount exception", e);
            throw new TfException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("IncomingBindCardService--openAccount is error", e);
            throw new TfException(ExceptionCodeEnum.PN_API_ERROR);
        }
    }

    /**
     * 平安确认协议
     * @param checkCodeMessageDTO
     */
    public void confirmAgreement(CheckCodeMessageDTO checkCodeMessageDTO) {
        try {
            //调用平安6248-开户接口
            JSONObject resultJson = PnHeadUtils.send(covertConfirmAgreementJson(checkCodeMessageDTO),
                    PnApiEnum.REGISTER_BEHAVIOR.getServiceCode(), PnApiEnum.REGISTER_BEHAVIOR.getServiceId());
            //平安api返回标识非成功
            if (!PnSdkConstant.API_SUCCESS_CODE.equals(resultJson.getString(PnSdkConstant.RESULT_CODE_FIELD))) {
                //记录错误原因
                JSONObject errorJson = PnHeadUtils.getError(resultJson);
                TfIncomingInfoEntity tfIncomingInfoEntity = new TfIncomingInfoEntity();
                tfIncomingInfoEntity.setId(checkCodeMessageDTO.getId());
                tfIncomingInfoEntity.setFailReason(errorJson.toJSONString());
                tfIncomingInfoEntity.setFailTime(LocalDateTime.now());
                tfIncomingInfoService.updateById(tfIncomingInfoEntity);
                throw new TfException(errorJson.getString(PnSdkConstant.RESULT_ERROR_MSG_FIELD));
            }
        } catch (TfException e) {
            log.error("IncomingBindCardService--confirmAgreement exception", e);
            throw new TfException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("IncomingBindCardService--confirmAgreement is error", e);
            throw new TfException(ExceptionCodeEnum.PN_API_ERROR);
        }
    }

    /**
     * 确认协议
     * @param checkCodeMessageDTO
     * @return
     */
    private JSONObject covertConfirmAgreementJson(CheckCodeMessageDTO checkCodeMessageDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime data = LocalDateTime.now();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("FunctionFlag", "1");
//        jsonObject.put("FundSummaryAcctNo", "15000101144986");
        jsonObject.put("SubAcctNo", checkCodeMessageDTO.getAccountNo());
        jsonObject.put("TranNetMemberCode", checkCodeMessageDTO.getMemberId());
        //功能标志FunctionFlag=1时必输
        jsonObject.put("OpClickTime", data.format(formatter));
        jsonObject.put("IpAddress", checkCodeMessageDTO.getIpAddress());
        jsonObject.put("MacAddress", checkCodeMessageDTO.getMacAddress());
        //签约渠道:1-app 2-平台H5网页 3-公众号 4-小程序
        jsonObject.put("SignChannel", checkCodeMessageDTO.getSignChannel());
        return jsonObject;
    }

//    public static void main(String[] args) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//        LocalDateTime data = LocalDateTime.now();
//        System.out.println("time:" + data.format(formatter));
//    }

}
