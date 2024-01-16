package com.tfjt.pay.external.unionpay.strategy.incoming;

import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.PnSdkConstant;
import com.tfjt.pay.external.unionpay.dto.CheckCodeMessageDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.tfjt.pay.external.unionpay.enums.ExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.enums.IdTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessStatusEnum;
import com.tfjt.pay.external.unionpay.enums.PnApiEnum;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.tfjt.pay.external.unionpay.utils.PnHeadUtils;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 21:00
 * @description 平安普通进件对公结算服务
 */
@Slf4j
@Service("pingan_common_corporate")
public class IncomingPnCorporateService extends AbstractIncomingService {

    @Autowired
    TfIncomingInfoService tfIncomingInfoService;

    @Autowired
    IncomingPnCorporateService incomingPnCorporateService;

    @Autowired
    PnHeadUtils pnHeadUtils;


    /**
     * 开户、绑卡
     * @return
     */
    @Override
    public boolean incomingSubmit(IncomingSubmitMessageDTO incomingSubmitMessageDTO) {
        //判断开户状态
//        TfIncomingInfoEntity tfIncomingInfoEntity = tfIncomingInfoService.queryIncomingInfoById(incomingSubmitMessageReqDTO.getIncomingId());
        TfIncomingInfoEntity tfIncomingInfoEntity = new TfIncomingInfoEntity();
        tfIncomingInfoEntity.setId(incomingSubmitMessageDTO.getId());
        if (IncomingAccessStatusEnum.MESSAGE_FILL_IN.getCode().equals(incomingSubmitMessageDTO.getAccessStatus())) {
            //调用平安6248-开户接口
            String accountNo = openAccount(tfIncomingInfoEntity, covertOpenAccountJson(incomingSubmitMessageDTO));
            incomingSubmitMessageDTO.setAccountNo(accountNo);
            tfIncomingInfoEntity.setAccountNo(accountNo);
            tfIncomingInfoEntity.setAccessStatus(IncomingAccessStatusEnum.SIGN_SUCCESS.getCode());
            tfIncomingInfoService.updateById(tfIncomingInfoEntity);
        }
        //调用平安6240接口
        binkCard(incomingSubmitMessageDTO);
        //更新入网状态
        tfIncomingInfoEntity.setAccessStatus(IncomingAccessStatusEnum.BINK_CARD_SUCCESS.getCode());
        tfIncomingInfoService.updateById(tfIncomingInfoEntity);
        return false;
    }

    /**
     * 回填验证码、打款金额
     * @param checkCodeMessageDTO
     * @return
     */
    @Override
    public boolean checkCode(CheckCodeMessageDTO checkCodeMessageDTO) {
        //调用平安6241接口
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("SubAcctNo", checkCodeMessageDTO.getAccountNo());
        jsonObject.put("TranNetMemberCode", checkCodeMessageDTO.getMemberId());
        jsonObject.put("TakeCashAcctNo", checkCodeMessageDTO.getBankCardNo());
        jsonObject.put("AuthAmt", checkCodeMessageDTO.getAuthAmt());
        jsonObject.put("OrderNo", checkCodeMessageDTO.getMessageCheckCode());
        jsonObject.put("Ccy", PnSdkConstant.DEFAULT_CCY);
        try {
            if (!IncomingAccessStatusEnum.SMS_VERIFICATION_SUCCESS.getCode().equals(checkCodeMessageDTO.getAccessStatus())) {
                JSONObject resultJson = pnHeadUtils.send(jsonObject,
                        PnApiEnum.CHECK_CODE_CORPORATE.getServiceCode(), PnApiEnum.CHECK_CODE_CORPORATE.getServiceId());
                JSONObject dataJson = resultJson.getJSONObject(PnSdkConstant.RESULT_DATA_FIELD);
                log.info("IncomingBindCardPnCorporateServiceImpl--checkCode, dataJson:{}", JSONObject.toJSONString(dataJson));
                TfIncomingInfoEntity tfIncomingInfoEntity = new TfIncomingInfoEntity();
                tfIncomingInfoEntity.setId(checkCodeMessageDTO.getId());
                if (ObjectUtils.isEmpty(dataJson)) {
                    //记录错误原因
                    JSONObject errorJson = PnHeadUtils.getError(resultJson);
                    tfIncomingInfoEntity.setFailReason(errorJson.toJSONString());
                    tfIncomingInfoEntity.setFailTime(LocalDateTime.now());
                    tfIncomingInfoService.updateById(tfIncomingInfoEntity);
                    throw new TfException(errorJson.getString(PnSdkConstant.RESULT_ERROR_MSG_FIELD));
                }
                tfIncomingInfoEntity.setAccessStatus(IncomingAccessStatusEnum.SMS_VERIFICATION_SUCCESS.getCode());
                tfIncomingInfoService.updateById(tfIncomingInfoEntity);
            }
            //调用验证协议接口
            confirmAgreement(checkCodeMessageDTO);
        } catch (TfException e) {
            log.error("IncomingBindCardPnCorporateServiceImpl--checkCode exception", e);
            throw new TfException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("IncomingBindCardPnCorporateServiceImpl--checkCode is error", e);
            throw new TfException(ExceptionCodeEnum.PN_API_ERROR);
        }
        return true;
    }


    /**
     * 绑定银行卡
     * @param incomingSubmitMessageDTO
     */
    private void binkCard(IncomingSubmitMessageDTO incomingSubmitMessageDTO) {
        try {
            //调用平安6248-开户接口
            JSONObject resultJson = pnHeadUtils.send(covertBinkCardJson(incomingSubmitMessageDTO),
                    PnApiEnum.BIND_CARD_CORPORATE.getServiceCode(), PnApiEnum.BIND_CARD_CORPORATE.getServiceId());
            JSONObject dataJson = resultJson.getJSONObject(PnSdkConstant.RESULT_DATA_FIELD);
            log.info("IncomingBindCardPnCorporateServiceImpl--binkCard, dataJson:{}", JSONObject.toJSONString(dataJson));
            //平安api返回标识非成功  !PnSdkConstant.API_SUCCESS_CODE.equals(resultJson.getString(PnSdkConstant.RESULT_CODE_FIELD))
            if (ObjectUtils.isEmpty(dataJson)) {
                //记录错误原因
                JSONObject errorJson = PnHeadUtils.getError(resultJson);
                TfIncomingInfoEntity tfIncomingInfoEntity = new TfIncomingInfoEntity();
                tfIncomingInfoEntity.setId(incomingSubmitMessageDTO.getId());
                tfIncomingInfoEntity.setFailReason(errorJson.toJSONString());
                tfIncomingInfoEntity.setFailTime(LocalDateTime.now());
                tfIncomingInfoService.updateById(tfIncomingInfoEntity);
                throw new TfException(errorJson.getString(PnSdkConstant.RESULT_ERROR_MSG_FIELD));
            }
        } catch (TfException e) {
            log.error("IncomingBindCardPnCorporateServiceImpl--binkCard exception", e);
            throw new TfException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("IncomingBindCardPnCorporateServiceImpl--binkCard is error", e);
            throw new TfException(ExceptionCodeEnum.PN_API_ERROR);
        }
    }




    /**
     * 转换开户api入参
     * @param incomingSubmitMessageDTO
     * @return
     */
    private JSONObject covertOpenAccountJson(IncomingSubmitMessageDTO incomingSubmitMessageDTO) {
        JSONObject jsonObject = new JSONObject();
        //功能标识:1:开户 2:销户 3: 为存量见证子帐号申请智能收款子账号
        jsonObject.put("FunctionFlag", NumberConstant.ONE);
        //交易网会员代码:平台端的用户ID，需要保证唯一性，可数字字母混合，如HY_120，若需要开通智能收款要求后6位是数字，该6位数字为智能收款账号后6位，因账号前几位固定故后6位不能重复。
        jsonObject.put("TranNetMemberCode", incomingSubmitMessageDTO.getMemberId());
        //客户真实姓名
        jsonObject.put("MemberName", incomingSubmitMessageDTO.getBusinessName());
        //会员证件类型
        jsonObject.put("MemberGlobalType", IdTypeEnum.SOCIAL_CREDIT_CODE.getCode());
//        jsonObject.put("MemberGlobalType", "52");
        //会员证件号码
        jsonObject.put("MemberGlobalId", incomingSubmitMessageDTO.getBusinessLicenseNo());
        //会员属性: SH-商户子账户(默认) 00-普通子账户
        jsonObject.put("MemberProperty", "SH");
        //手机号码测试送11个1
        jsonObject.put("Mobile", incomingSubmitMessageDTO.getLegalMobile());
        //邮箱
//        jsonObject.put("Email", "duake524@163.com");

        //个体工商户标识
        jsonObject.put("IndivBusinessFlag", NumberConstant.ONE.equals(incomingSubmitMessageDTO.getAccessMainType()) ? NumberConstant.ONE : NumberConstant.TWO);
        //营业及店铺信息-个体工商户必填
        jsonObject.put("CompanyName", incomingSubmitMessageDTO.getBusinessName());
        jsonObject.put("CompanyGlobalType", incomingSubmitMessageDTO.getBusinessLicenseType());
//        jsonObject.put("CompanyGlobalType", "52");
        jsonObject.put("CompanyGlobalId", incomingSubmitMessageDTO.getBusinessLicenseNo());
        jsonObject.put("ShopId", incomingSubmitMessageDTO.getBusinessId());
        jsonObject.put("ShopName", incomingSubmitMessageDTO.getShopShortName());
        //法人信息-个体工商户必填
        jsonObject.put("RepFlag", NumberConstant.ONE);
        jsonObject.put("ReprName", incomingSubmitMessageDTO.getLegalName());
        jsonObject.put("ReprGlobalType", IdTypeEnum.ID_CARD.getCode());
        jsonObject.put("ReprGlobalId", incomingSubmitMessageDTO.getLegalIdNo());
        return jsonObject;
    }

    /**
     * 转换绑卡api入参
     * @param incomingSubmitMessageDTO
     * @return
     */
    private JSONObject covertBinkCardJson(IncomingSubmitMessageDTO incomingSubmitMessageDTO) {
        JSONObject jsonObject = new JSONObject();
        //子账户账号
        jsonObject.put("SubAcctNo", incomingSubmitMessageDTO.getAccountNo());
        //交易网会员代码
        jsonObject.put("TranNetMemberCode", incomingSubmitMessageDTO.getMemberId());
        //会员名称
        jsonObject.put("MemberName", incomingSubmitMessageDTO.getBusinessName());
        //会员证件类型
        jsonObject.put("MemberGlobalType", incomingSubmitMessageDTO.getBusinessLicenseType());
//        jsonObject.put("MemberGlobalType", "52");
        //会员证件号码
        jsonObject.put("MemberGlobalId", incomingSubmitMessageDTO.getBusinessLicenseNo());
        //会员账号:提现的银行卡
        jsonObject.put("MemberAcctNo", incomingSubmitMessageDTO.getBankCardNo());
        //银行类型:1：本行 2：他行
        jsonObject.put("BankType", PnSdkConstant.PN_BANK_CODE.equals(incomingSubmitMessageDTO.getBankCode()) ? NumberConstant.ONE : NumberConstant.TWO);
//        jsonObject.put("BankType", "1");
        //开户行名称
        jsonObject.put("AcctOpenBranchName", incomingSubmitMessageDTO.getBankName());
        //大小额行号：大小额行号和超级网银行号两者二选一必填。
        if (!PnSdkConstant.PN_BANK_CODE.equals(incomingSubmitMessageDTO.getBankCode())) {
            jsonObject.put("CnapsBranchId", incomingSubmitMessageDTO.getBankBranchCode());
        }
//        //超级网银行号
//        jsonObject.put("EiconBankBranchId", "102100099996");
        //手机号码
        jsonObject.put("Mobile", incomingSubmitMessageDTO.getBankCardMobile());
        //个体工商户标识
        jsonObject.put("IndivBusinessFlag", NumberConstant.ONE.equals(incomingSubmitMessageDTO.getAccessMainType()) ? NumberConstant.ONE : NumberConstant.TWO);
        //营业及店铺信息-个体工商户必填
        jsonObject.put("CompanyName", incomingSubmitMessageDTO.getBusinessName());
        jsonObject.put("CompanyGlobalType", incomingSubmitMessageDTO.getBusinessLicenseType());
//        jsonObject.put("CompanyGlobalType", "52");
        jsonObject.put("CompanyGlobalId", incomingSubmitMessageDTO.getBusinessLicenseNo());
        jsonObject.put("ShopId", incomingSubmitMessageDTO.getBusinessId());
        jsonObject.put("ShopName", incomingSubmitMessageDTO.getShopShortName());

        //会员名称是否是法人：1-是  2-否（个体工商户必输）
        jsonObject.put("RepFlag", "2");
        jsonObject.put("ReprName", incomingSubmitMessageDTO.getLegalName());
        jsonObject.put("ReprGlobalType", IdTypeEnum.ID_CARD.getCode());
        jsonObject.put("ReprGlobalId", incomingSubmitMessageDTO.getLegalIdNo());
        //进件主体为企业时，必须有经办人信息
        if (NumberConstant.TWO.equals(incomingSubmitMessageDTO.getAccessMainType())) {
            jsonObject.put("AgencyClientFlag", "1");
            jsonObject.put("AgencyClientName", incomingSubmitMessageDTO.getAgentName());
            jsonObject.put("AgencyClientGlobalType", IdTypeEnum.ID_CARD.getCode());
            jsonObject.put("AgencyClientGlobalId", incomingSubmitMessageDTO.getAgentIdNo());
            jsonObject.put("AgencyClientMobile", incomingSubmitMessageDTO.getAgentMobile());
        }
        return jsonObject;
    }
}
