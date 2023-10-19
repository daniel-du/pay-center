package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.io.Files;
import com.tfjt.pay.external.unionpay.api.dto.resp.UnionPayLoansSettleAcctDTO;
import com.tfjt.pay.external.unionpay.constants.RedisConstant;
import com.tfjt.pay.external.unionpay.dto.*;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.enums.BankTypeEnum;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.enums.UnionPayLoanBussCodeEnum;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.utils.MessageDigestUtils;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.pay.external.unionpay.utils.UnionPaySignUtil;
import com.tfjt.pay.external.unionpay.validator.group.VerifyBankInfo;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.validator.group.UpdateGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UnionPayLoansApiServiceImpl implements UnionPayLoansApiService {


    @Resource
    private RestTemplate restTemplate;

    @Value("${unionPayLoans.groupId}")
    private String groupId;

    @Value("${unionPayLoans.url}")
    private String url;

    @Value("${unionPayLoans.encodedPub}")
    private String encodedPub;


    @Autowired
    private CustIdcardInfoService custIdcardInfoService;

    @Lazy
    @Autowired
    private CustBankInfoService custBankInfoService;

    @Lazy
    @Autowired
    private LoanUserService loanUserService;

    @Autowired
    private LoanBalanceAcctService tfLoanBalanceAcctService;

    @Autowired
    private UnionPayLoanReqLogService unionPayLoanReqLogService;

    @Autowired
    private CustBusinessDetailService custBusinessDetailService;

    @Autowired
    private CustHoldingService tfCustHoldingService;

    @Autowired
    private LoanUserKeyInformationChangeRecordLogService keyInformationChangeRecordLogService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 进件接口
     *
     * @param tfLoanUserEntity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#tfLoanUserEntity.busId"}, expire = 3000, acquireTimeout = 4000)
    @Override
    public LoanUserEntity incoming(LoanUserEntity tfLoanUserEntity, String smsCode) {
        try {
            Date req = new Date();
            //业务参数复制
            UnionPayLoansIncomingDTO unionPayLoansIncomingDTO = buildYinLianLoansIncoming(tfLoanUserEntity, smsCode);

            UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ51_PERSON_APP.getCode(), JSONObject.toJSONString(unionPayLoansIncomingDTO));

            log.info("进件入参{}", JSON.toJSON(unionPayLoansBaseReq));
            //调用银联接口
            ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);
            log.info("进件返回值{}", responseEntity.getBody().toString());

            IncomingReturn incomingReturn = getBaseIncomingReturn(responseEntity, JSON.toJSON(unionPayLoansBaseReq), req, tfLoanUserEntity.getId());
            //修改货款商户
            updatTfLoanUserEntity(incomingReturn, tfLoanUserEntity);
            //添加电子账单
            addTfLoanBalanceAcct(incomingReturn, tfLoanUserEntity.getId());
        } catch (TfException e) {
            log.error("银联-进件失败：param={}", JSON.toJSONString(tfLoanUserEntity), e);
            throw new TfException(e.getMessage());
        }

        return tfLoanUserEntity;
    }

    /**
     * 二级进件
     *
     * @param tfLoanUserEntity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#tfLoanUserEntity.busId"}, expire = 3000, acquireTimeout = 4000)
    @Override
    public IncomingReturn twoIncoming(LoanUserEntity tfLoanUserEntity, String smsCode) {
        IncomingReturn incomingReturn = new IncomingReturn();
        try {
            Date req = new Date();
            //业务参数复制
            UnionPayLoansTwoIncomingDTO unionPayLoansTwoIncomingDTO = buildYinLianLoansTwoIncomingDTO(tfLoanUserEntity, smsCode);

            UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ56_MCH_APP.getCode(), JSONObject.toJSONString(unionPayLoansTwoIncomingDTO));

            log.info("进件入参{}", JSON.toJSON(unionPayLoansBaseReq));
            //调用银联接口
            ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);
            log.info("进件返回值{}", responseEntity.getBody().toString());

            incomingReturn = getBaseIncomingReturn(responseEntity, JSON.toJSON(unionPayLoansBaseReq), req, tfLoanUserEntity.getId());
            //修改货款商户
            updatTwoTfLoanUserEntity(incomingReturn, tfLoanUserEntity);
        } catch (TfException e) {
            log.error("银联-二级进件失败：param={}", JSON.toJSONString(tfLoanUserEntity), e);
            throw new TfException(e.getMessage());
        }
        return incomingReturn;
    }

    private void updatTwoTfLoanUserEntity(IncomingReturn incomingReturn, LoanUserEntity tfLoanUserEntity) {
        LoanUserEntity tfLoanUserEntityOld = tfLoanUserEntity;
        if (StringUtils.isNotBlank(incomingReturn.getOutRequestNo())) {
            tfLoanUserEntity.setOutRequestNo(incomingReturn.getOutRequestNo());
        }
        if (StringUtils.isNotBlank(incomingReturn.getMchApplicationId())) {
            tfLoanUserEntity.setMchApplicationId(incomingReturn.getMchApplicationId());
        }

        IncomingReturn incomingReturnSel = this.getTwoIncomingInfo(incomingReturn.getOutRequestNo());
        tfLoanUserEntity.setApplicationStatus(incomingReturnSel.getApplicationStatus());
        if (ObjectUtil.isNotEmpty(incomingReturn.getFailureMsgs())) {
            tfLoanUserEntity.setFailureMsgs(incomingReturn.getFailureMsgs());
        }
        Integer loanUserType = tfLoanUserEntity.getLoanUserType();
        if (loanUserType != 0) {
            CustBankInfoEntity custBankInfoEntity = verifyCustBankInfo(tfLoanUserEntity.getId());
            int settlementType = custBankInfoEntity.getSettlementType();
            if (2 == settlementType) {
                tfLoanUserEntity.setBankCallStatus(1);
            }
        }
        loanUserService.updateById(tfLoanUserEntity);
        keyInformationChangeRecordLogService.saveLog(tfLoanUserEntity.getId(),incomingReturn.getOutRequestNo(),
                incomingReturn.getMchApplicationId(),null,tfLoanUserEntityOld);
        //通知业务
        loanUserService.asynNotice(tfLoanUserEntity);
    }

    private UnionPayLoansTwoIncomingDTO buildYinLianLoansTwoIncomingDTO(LoanUserEntity tfLoanUserEntity, String smsCode) {
        if (StringUtils.isBlank(tfLoanUserEntity.getName())) {
            throw new TfException(PayExceptionCodeEnum.NOT_NULL_MERCHANT);
        }
        String nonce = UUID.randomUUID().toString().replace("-", "");
        CustBankInfoEntity custBankInfoEntity = verifyCustBankInfo(tfLoanUserEntity.getId());
        int settlementType = custBankInfoEntity.getSettlementType();
        smsCode = handleCode(smsCode, custBankInfoEntity);
        // 1营业执照
        BusinessLicenseInFoDTO businessLicenseInFoDTO = buildBusinessLicenseDTO(tfLoanUserEntity.getId());
        // 2身份证
        IdCardDTO legalPersonIdCard = buildIdCardDTO(tfLoanUserEntity.getId());
        legalPersonIdCard.setType("1");
        // 3银行卡


        SettleAcctDTO settleAcctDTO = buildSettleAcctDTO(custBankInfoEntity, custBankInfoEntity.getSettlementType());
        // 4企业信息
        UnionPayLoansHoldingCompany unionPayLoansHoldingCompany = buildYinLianLoansHoldingCompany(tfLoanUserEntity.getId());

        UnionPayLoansTwoIncomingDTO unionPayLoansTwoIncomingDTO = UnionPayLoansTwoIncomingDTO.builder()
                .businessLicense(businessLicenseInFoDTO.getBusinessLicenseDTO())
                .legalPersonIdCard(legalPersonIdCard)
                .contactIdCard(legalPersonIdCard)
                .settleAcct(settleAcctDTO)
                .holdingCompany(unionPayLoansHoldingCompany)
                .outRequestNo(nonce)
                .organizationType(String.valueOf(tfLoanUserEntity.getLoanUserType()))
                .shortName(tfLoanUserEntity.getName())
                .contactMobileNumber(UnionPaySignUtil.SM2(encodedPub, custBankInfoEntity.getPhone()))
                .legalPersonMobileNumber(UnionPaySignUtil.SM2(encodedPub, custBankInfoEntity.getPhone()))
                .contactEmail(UnionPaySignUtil.SM2(encodedPub, businessLicenseInFoDTO.getContactEmail()))
                .build();
        //此时需要验证码
        if (settlementType == 1) {
            unionPayLoansTwoIncomingDTO.setSmsCode(smsCode);
        }
        return unionPayLoansTwoIncomingDTO;
    }

    private UnionPayLoansHoldingCompany buildYinLianLoansHoldingCompany(Long id) {
        CustHoldingEntity tfCustHoldingEntity = tfCustHoldingService.getByLoanUserId(id);
        if (tfCustHoldingEntity == null) {
            throw new TfException(PayExceptionCodeEnum.NO_DATA);
        }
        UnionPayLoansHoldingCompany unionPayLoansHoldingCompany = new UnionPayLoansHoldingCompany();
        unionPayLoansHoldingCompany.setName(tfCustHoldingEntity.getHoldingName());
        unionPayLoansHoldingCompany.setLicenseNumber(tfCustHoldingEntity.getHoldingNum());
        unionPayLoansHoldingCompany.setLicenseValidTime(tfCustHoldingEntity.getEffectiveDate() + "," + tfCustHoldingEntity.getExpiryDate());
        unionPayLoansHoldingCompany.setLicenseType("1");
        return unionPayLoansHoldingCompany;
    }

    /**
     * 营业执照
     *
     * @param id
     * @return
     */
    private BusinessLicenseInFoDTO buildBusinessLicenseDTO(Long id) {
        CustBusinessDetailEntity custBusinessDetailEntity = custBusinessDetailService.getByLoanUserId(id);
        if (custBusinessDetailEntity == null) {
            throw new TfException(PayExceptionCodeEnum.NO_DATA);
        }
        BusinessLicenseInFoDTO businessLicenseInFoDTO = new BusinessLicenseInFoDTO();
        BusinessLicenseDTO businessLicenseDTO = new BusinessLicenseDTO();
        businessLicenseDTO.setNumber(custBusinessDetailEntity.getBusinessNum());
        businessLicenseDTO.setCompanyName(custBusinessDetailEntity.getBusinessName());
        businessLicenseDTO.setCompanyAddress(custBusinessDetailEntity.getBusinessAddress());
        businessLicenseDTO.setValidTime(custBusinessDetailEntity.getEffectiveDate() + "," + custBusinessDetailEntity.getExpiryDate());
        businessLicenseInFoDTO.setBusinessLicenseDTO(businessLicenseDTO);
        businessLicenseInFoDTO.setContactEmail(custBusinessDetailEntity.getEmail());
        businessLicenseDTO.setCopy(custBusinessDetailEntity.getBusinessImgMediaId());
        return businessLicenseInFoDTO;
    }

    private ResponseEntity<UnionPayLoansBaseReturn> post(UnionPayLoansBaseReq unionPayLoansBaseReq) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json; charset=utf-8");
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(JSON.toJSONString(unionPayLoansBaseReq), httpHeaders);
        //调用银联接口
        return restTemplate.postForEntity(url, stringHttpEntity, UnionPayLoansBaseReturn.class);
    }


    /**
     * 进件修改接口
     *
     * @param tfLoanUserEntity
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#tfLoanUserEntity.busId"}, expire = 3000, acquireTimeout = 4000)
    public IncomingReturn incomingEdit(LoanUserEntity tfLoanUserEntity, String smsCode) {
        LoanUserEntity tfLoanUserEntityOld = tfLoanUserEntity;
        IncomingReturn incomingReturn = new IncomingReturn();
        try {
            Date req = new Date();
            UnionPayLoansIncomingEditDTO unionPayLoansIncomingDTO = buildEditYinLianLoansIncoming(tfLoanUserEntity, smsCode);

            UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ54_CUS_APPLICATIONS_RENEW.getCode(), JSON.toJSONString(unionPayLoansIncomingDTO));

            ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);

            log.info("进件修改接口{}", responseEntity.getBody());
            incomingReturn = getBaseIncomingReturn(responseEntity, tfLoanUserEntity, req, tfLoanUserEntity.getId());
            tfLoanUserEntity.setOutRequestNo(incomingReturn.getOutRequestNo());
            tfLoanUserEntity.setCusId(incomingReturn.getCusId());
            loanUserService.updateById(tfLoanUserEntity);
            keyInformationChangeRecordLogService.saveLog(tfLoanUserEntity.getId(),tfLoanUserEntity.getOutRequestNo(),
                    null,null,tfLoanUserEntityOld);

        } catch (TfException e) {
            log.error("银联-进件-修改失败：param={}", JSON.toJSONString(tfLoanUserEntity), e);
            throw new TfException(e.getMessage());

        }
        return incomingReturn;
    }

    /**
     * 二级进件修改接口
     *
     * @param tfLoanUserEntity
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#tfLoanUserEntity.busId"}, expire = 3000, acquireTimeout = 4000)
    public IncomingReturn twoIncomingEdit(LoanUserEntity tfLoanUserEntity, String smsCode) {
        LoanUserEntity tfLoanUserEntityOld = tfLoanUserEntity;
        IncomingReturn incomingReturn = new IncomingReturn();
        try {
            Date req = new Date();
            UnionPayLoansTwoIncomingEditDTO unionPayLoansTwoIncomingEditDTO = buildYinLianLoansTwoIncomingEditDTO(tfLoanUserEntity, smsCode);

            UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ59_MCH_APPLICATIONS_RENEW.getCode(), JSON.toJSONString(unionPayLoansTwoIncomingEditDTO));


            ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);

            log.info("二级进件修改接口{}", responseEntity.getBody());
            incomingReturn = getBaseIncomingReturn(responseEntity, tfLoanUserEntity, req, tfLoanUserEntity.getId());
            IncomingReturn incomingReturnSel = this.getTwoIncomingInfo(incomingReturn.getOutRequestNo());
            tfLoanUserEntity.setApplicationStatus(incomingReturnSel.getApplicationStatus());
            if (ObjectUtil.isNotEmpty(incomingReturn.getFailureMsgs())) {
                tfLoanUserEntity.setFailureMsgs(incomingReturn.getFailureMsgs());
            }
            tfLoanUserEntity.setOutRequestNo(incomingReturn.getOutRequestNo());
            tfLoanUserEntity.setMchApplicationId(incomingReturn.getMchApplicationId());

            loanUserService.updateById(tfLoanUserEntity);
            keyInformationChangeRecordLogService.saveLog(tfLoanUserEntity.getId(),incomingReturn.getOutRequestNo(),
                    incomingReturn.getMchApplicationId(),null,tfLoanUserEntityOld);
        } catch (TfException e) {
            log.error("银联-二级进件-修改失败：param={}", JSON.toJSONString(tfLoanUserEntity), e);
            throw new TfException(e.getMessage());

        }
        return incomingReturn;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#loanUserId"}, expire = 3000, acquireTimeout = 4000)
    public UnionPayLoansSettleAcctDTO settleAcctsValidate(Long loanUserId, Integer payAmount) {
        String settleAcctId = getSettleAcctId(loanUserId);
        if (StringUtils.isBlank(settleAcctId)) {
            throw new TfException(PayExceptionCodeEnum.NO_SETTLE_ACCT);
        }
        //业务参数复制
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("settleAcctId", settleAcctId);
        reqParams.put("payAmount", payAmount);
        log.info("打款金额验证电子账号ID{}金额{}", settleAcctId, payAmount);
        UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ527_SETTLE_ACCTS_VALIDATE.getCode(), JSON.toJSONString(reqParams));
        //调用银联接口
        ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);
        log.info("打款金额验证{}", JSON.toJSONString(responseEntity));

        UnionPayLoansBaseReturn unionPayLoansBaseReturn = responseEntity.getBody();
        unionPayLoanReqLogService.asyncSaveLog(unionPayLoansBaseReturn, reqParams, null, null);

        getBaseIncomingReturnStr(responseEntity, null, null, null);

        //修改打款验证按钮显示
        LoanUserEntity tfLoanUserEntity = loanUserService.getById(loanUserId);
        tfLoanUserEntity.setBankCallStatus(0);
        loanUserService.updateById(tfLoanUserEntity);



        UnionPayLoansSettleAcctDTO unionPayLoansSettleAcct = JSON.parseObject(unionPayLoansBaseReturn.getLwzRespData(), UnionPayLoansSettleAcctDTO.class);

        return unionPayLoansSettleAcct;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public String getSettleAcctId(Long loanUserId) {
        LoanUserEntity tfLoanUserEntity = loanUserService.getById(loanUserId);
        LoanUserEntity tfLoanUserEntityOld = tfLoanUserEntity;
        if (StringUtils.isNotBlank(tfLoanUserEntity.getOutRequestNo())) {
            IncomingReturn incomingReturn = getTwoIncomingInfo(tfLoanUserEntity.getOutRequestNo());
            if (!StringUtils.isBlank(incomingReturn.getSettleAcctId())) {
                tfLoanUserEntity.setSettleAcctId(incomingReturn.getSettleAcctId());
                loanUserService.updateById(tfLoanUserEntity);
                keyInformationChangeRecordLogService.saveLog(tfLoanUserEntity.getId(),null,null,
                        incomingReturn.getSettleAcctId(),tfLoanUserEntityOld);
                return incomingReturn.getSettleAcctId();
            }else {
                return tfLoanUserEntity.getSettleAcctId();
            }

        } else {
            return "";
        }

    }


    /**
     * 构建二级进件修改参数
     *
     * @param tfLoanUserEntity
     * @param smsCode
     * @return
     */
    private UnionPayLoansTwoIncomingEditDTO buildYinLianLoansTwoIncomingEditDTO(LoanUserEntity tfLoanUserEntity, String smsCode) {
        try {
            ValidatorUtils.validateEntity(tfLoanUserEntity, UpdateGroup.class);
        } catch (Exception ex) {
            throw new TfException(ex.getMessage());
        }
        String mchId = tfLoanUserEntity.getMchId();
        if (StringUtil.isBlank(mchId)) {
            throw new TfException(PayExceptionCodeEnum.MCHID_NOT_NULL);
        }
        String nonce = UUID.randomUUID().toString().replace("-", "");
        // 1、银行卡
        CustBankInfoEntity custBankInfoEntity = verifyCustBankInfo(tfLoanUserEntity.getId());
        int settlementType = custBankInfoEntity.getSettlementType();
        smsCode = handleCode(smsCode, custBankInfoEntity);

        // 2、营业执照
        BusinessLicenseInFoDTO businessLicenseInFoDTO = buildBusinessLicenseDTO(tfLoanUserEntity.getId());
        // 3、身份证
        IdCardDTO legalPersonIdCard = buildIdCardDTO(tfLoanUserEntity.getId());
        legalPersonIdCard.setType("1");

        SettleAcctDTO settleAcctDTO = buildSettleAcctDTO(custBankInfoEntity, custBankInfoEntity.getSettlementType());
        // 4企业信息
        UnionPayLoansHoldingCompany unionPayLoansHoldingCompany = buildYinLianLoansHoldingCompany(tfLoanUserEntity.getId());

        UnionPayLoansTwoIncomingEditDTO unionPayLoansTwoIncomingEditDTO = UnionPayLoansTwoIncomingEditDTO.builder()
                .mchId(tfLoanUserEntity.getMchId())
                .businessLicense(businessLicenseInFoDTO.getBusinessLicenseDTO())
                .legalPersonIdCard(legalPersonIdCard)
                .contactIdCard(legalPersonIdCard)
                .settleAcct(settleAcctDTO)
                .holdingCompany(unionPayLoansHoldingCompany)
                .outRequestNo(nonce)
                .shortName(tfLoanUserEntity.getName())
                .contactMobileNumber(UnionPaySignUtil.SM2(encodedPub, custBankInfoEntity.getPhone()))
                .legalPersonMobileNumber(UnionPaySignUtil.SM2(encodedPub, custBankInfoEntity.getPhone()))
                .contactEmail(UnionPaySignUtil.SM2(encodedPub, businessLicenseInFoDTO.getContactEmail()))
                .build();
        //此时需要验证码信息
        if (settlementType == 1) {
            unionPayLoansTwoIncomingEditDTO.setSmsCode(smsCode);
        }
        return unionPayLoansTwoIncomingEditDTO;
    }


    /**
     * 进件公用返回值
     *
     * @param responseEntity
     * @return
     */
    private IncomingReturn getBaseIncomingReturn(ResponseEntity<UnionPayLoansBaseReturn> responseEntity, Object p, Date req, Long loanUserId) {
        UnionPayLoansBaseReturn unionPayLoansBaseReturn = responseEntity.getBody();
        //日志保存
        if (null != req) {
            unionPayLoanReqLogService.asyncSaveLog(unionPayLoansBaseReturn, p, req, loanUserId);
        }
        //银联贷款返回码
        if (!Objects.equals("LWZ99999", unionPayLoansBaseReturn.getRespCode())) {
            log.error("银联调用失败{}", responseEntity.getBody().toString());
            throw new TfException(unionPayLoansBaseReturn.getRespMsg());
        }

        //银行返回码
        if (Objects.equals("200", unionPayLoansBaseReturn.getRespLwzCode())) {
            IncomingReturn incomingReturn = JSON.parseObject(unionPayLoansBaseReturn.getLwzRespData().toString(), IncomingReturn.class);
            if (Objects.equals(incomingReturn.getApplicationStatus(), "failed")) {
                throw new TfException(getLwzRespData(incomingReturn.getFailureMsgs()));
            }
            return incomingReturn;
        } else {
            log.error("银联-银行调用失败{}", responseEntity.getBody().toString());
            throw new TfException(getRespLwzMsgReturnMsg(unionPayLoansBaseReturn.getRespLwzMsg()));
        }
    }

    private String getLwzRespData(String failureMsgs) {
        log.info("错误信息{}", failureMsgs);
        String msg = "";
        List<LwzRespReturn> lwzRespReturnList = JSONObject.parseArray(failureMsgs, LwzRespReturn.class);
        if (lwzRespReturnList != null && lwzRespReturnList.size() > 0) {
            String names = lwzRespReturnList.stream().map(LwzRespReturn::getReason).collect(Collectors.joining(";"));
            if (StringUtils.isNotBlank(names)) {
                return names;
            }
        }
        return msg;
    }

    public static void main(String[] args) {
        String m = "[{\n" +
                "\t\"reason\": \"身份证号、姓名、手机号、银行卡号四要素不匹配或验证通道异常。验证通道返回原因：鉴权失败, 银行卡号以五结尾\",\n" +
                "\t\"param\": \"/settle_acct\"\n" +
                "}]";
        List<LwzRespReturn> lwzRespReturnList = JSONObject.parseArray(m, LwzRespReturn.class);
        if (lwzRespReturnList != null && lwzRespReturnList.size() > 0) {
            String names = lwzRespReturnList.stream().map(LwzRespReturn::getReason).collect(Collectors.joining(";"));
            if (StringUtils.isNotBlank(names)) {
            }
        }
    }

    private String getRespLwzMsgReturnMsg(String respLwzMsg) {
        log.info("错误信息{}", respLwzMsg);
        String mgs = "";
        if (StringUtils.isNotBlank(respLwzMsg)) {
            RespLwzMsgReturn respLwzMsgReturn = JSON.parseObject(respLwzMsg, RespLwzMsgReturn.class);
            if (respLwzMsgReturn != null) {
                if (StringUtils.isNotBlank(respLwzMsgReturn.getIssue())) {
                    return respLwzMsgReturn.getIssue();
                }
                if (StringUtils.isNotBlank(respLwzMsgReturn.getMessage())) {
                    return respLwzMsgReturn.getMessage();
                }
            }
        }
        return mgs;
    }


    private void getBaseIncomingReturnStr(ResponseEntity<UnionPayLoansBaseReturn> responseEntity, Object p, Date req, Long loanUserId) {
        UnionPayLoansBaseReturn unionPayLoansBaseReturn = responseEntity.getBody();
        //日志保存
        if (null != req) {
            unionPayLoanReqLogService.asyncSaveLog(unionPayLoansBaseReturn, p, req, loanUserId);
        }
        if (!Objects.equals("LWZ99999", unionPayLoansBaseReturn.getRespCode())) {
            log.error("银联调用失败{}", responseEntity.getBody());
            throw new TfException(unionPayLoansBaseReturn.getRespMsg());
        }

        if (!Objects.equals("200", unionPayLoansBaseReturn.getRespLwzCode())) {
            log.error("银联-银行调用失败{}", responseEntity.getBody());
            throw new TfException(getRespLwzMsgReturnMsg(unionPayLoansBaseReturn.getRespLwzMsg()));
        }
    }


    private void addTfLoanBalanceAcct(IncomingReturn incomingReturn, Long id) {
        LoanBalanceAcctEntity tfLoanBalanceAcctEntity = new LoanBalanceAcctEntity();
        tfLoanBalanceAcctEntity.setLoanUserId(Integer.valueOf(String.valueOf(id)));
        if (StringUtils.isNotBlank(incomingReturn.getRelAcctNo())) {
            tfLoanBalanceAcctEntity.setRelAcctNo(incomingReturn.getRelAcctNo());
        }
        if (StringUtils.isNotBlank(incomingReturn.getBalanceAcctId())) {
            tfLoanBalanceAcctEntity.setBalanceAcctId(incomingReturn.getBalanceAcctId());
        }
        tfLoanBalanceAcctService.save(tfLoanBalanceAcctEntity);
    }

    private void updatTfLoanUserEntity(IncomingReturn incomingReturn, LoanUserEntity tfLoanUserEntity) {
        LoanUserEntity tfLoanUserEntityOld = tfLoanUserEntity;
        tfLoanUserEntity.setApplicationStatus(incomingReturn.getApplicationStatus());
        if (StringUtils.isNotBlank(incomingReturn.getCusId())) {
            tfLoanUserEntity.setCusId(incomingReturn.getCusId());
        }
        if (StringUtils.isNotBlank(incomingReturn.getOutRequestNo())) {
            tfLoanUserEntity.setOutRequestNo(incomingReturn.getOutRequestNo());
        }
        if (StringUtils.isNotBlank(incomingReturn.getSettleAcctId())) {
            tfLoanUserEntity.setSettleAcctId(incomingReturn.getSettleAcctId());
        }
        loanUserService.updateById(tfLoanUserEntity);
        keyInformationChangeRecordLogService.saveLog(tfLoanUserEntity.getId(),tfLoanUserEntity.getOutRequestNo(),null
                ,null,tfLoanUserEntityOld);
        //通知业务
        loanUserService.asynNotice(tfLoanUserEntity);
    }

    /**
     * 构建进件参数
     *
     * @param tfLoanUserEntity
     * @return
     */
    private UnionPayLoansIncomingDTO buildYinLianLoansIncoming(LoanUserEntity tfLoanUserEntity, String smsCode) {
        String nonce = UUID.randomUUID().toString().replace("-", "");
        //1 银行卡
        CustBankInfoEntity custBankInfoEntity = verifyCustBankInfo(tfLoanUserEntity.getId());
        int settlementType = custBankInfoEntity.getSettlementType();
        smsCode = handleCode(smsCode, custBankInfoEntity);
        //2 身份证
        IdCardDTO idCardDTO = buildIdCardDTO(tfLoanUserEntity.getId());

        SettleAcctDTO settleAcctDTO = buildSettleAcctDTO(custBankInfoEntity, null);

        UnionPayLoansIncomingDTO unionPayLoansIncomingDTO = UnionPayLoansIncomingDTO.builder()
                .idCard(idCardDTO)
                .settleAcct(settleAcctDTO)
                .outRequestNo(nonce)
                .mobileNumber(UnionPaySignUtil.SM2(encodedPub, custBankInfoEntity.getPhone()))
                .build();
        if (settlementType == 1) {
            unionPayLoansIncomingDTO.setSmsCode(smsCode);
        }


        return unionPayLoansIncomingDTO;
    }

    private String handleCode(String smsCode, CustBankInfoEntity custBankInfoEntity) {
        int settlementType = custBankInfoEntity.getSettlementType();
        if (settlementType == 1) {
            if (StringUtils.isEmpty(smsCode)) {
                //验证衍生码是否有效
                String phone = custBankInfoEntity.getPhone();
                Boolean mobileStatus = getMobileStatus(phone);
                if (!mobileStatus) {
                    validationMobileNumber(phone);
                    custBankInfoEntity.setSmsCode("");
                    custBankInfoService.updateById(custBankInfoEntity);
                    throw new TfException(PayExceptionCodeEnum.SMSCODE_ERROR);
                }
                String smsCodeOld = custBankInfoEntity.getSmsCode();
                if (StringUtils.isEmpty(smsCodeOld)) {
                    throw new TfException(PayExceptionCodeEnum.SMSCODE_ERROR);
                }
                smsCode = smsCodeOld;
            }
        }
        return smsCode;
    }

    private CustBankInfoEntity verifyCustBankInfo(Long loanUserId) {
        CustBankInfoEntity custBankInfoEntity = custBankInfoService.getOne(new LambdaQueryWrapper<CustBankInfoEntity>().eq(CustBankInfoEntity::getLoanUserId, loanUserId));
        if (null == custBankInfoEntity) {
            throw new TfException(PayExceptionCodeEnum.NO_DATA);
        }
        try {
            ValidatorUtils.validateEntity(custBankInfoEntity, VerifyBankInfo.class);
        } catch (Exception ex) {
            throw new TfException(ex.getMessage());
        }
        return custBankInfoEntity;
    }

    private SettleAcctDTO buildSettleAcctDTO(CustBankInfoEntity custBankInfoEntity, Integer settlementType) {
        SettleAcctDTO settleAcctDTO = new SettleAcctDTO();
        settleAcctDTO.setName(UnionPaySignUtil.SM2(encodedPub, custBankInfoEntity.getAccountName()));
        settleAcctDTO.setBankAcctNo(UnionPaySignUtil.SM2(encodedPub, custBankInfoEntity.getBankCardNo()));
        settleAcctDTO.setBankBranchCode(custBankInfoEntity.getBankBranchCode());
        settleAcctDTO.setProfession(custBankInfoEntity.getCareer());
        if (!Objects.isNull(settlementType)) {
            settleAcctDTO.setType(String.valueOf(settlementType));
        }
        return settleAcctDTO;
    }

    private IdCardDTO buildIdCardDTO(Long loanUserId) {
        IdCardDTO idCardDTO = new IdCardDTO();
        CustIdcardInfoEntity custIdcardInfoEntity = custIdcardInfoService.getOne(new LambdaQueryWrapper<CustIdcardInfoEntity>().eq(CustIdcardInfoEntity::getLoanUserId, loanUserId));
        if (null == custIdcardInfoEntity) {
            throw new TfException(PayExceptionCodeEnum.NO_DATA);
        }
        idCardDTO.setCopy(custIdcardInfoEntity.getFrontIdCardUrlMediaId());
        idCardDTO.setNational(custIdcardInfoEntity.getBackIdCardUrlMediaId());
        idCardDTO.setName(UnionPaySignUtil.SM2(encodedPub, custIdcardInfoEntity.getName()));
        idCardDTO.setNumber(UnionPaySignUtil.SM2(encodedPub, custIdcardInfoEntity.getIdNo()));
        idCardDTO.setValidTime(custIdcardInfoEntity.getEffectiveDate() + "," + custIdcardInfoEntity.getExpiryDate());
        return idCardDTO;

    }

    /**
     * 公共参数赋值
     *
     * @param lwzBussCode
     * @param lwzData
     * @return
     */
    private UnionPayLoansBaseReq baseBuilder(String lwzBussCode, String lwzData) {
        String srcReqDate = DateFormatUtils.format(new Date(), "yyyyMMdd");
        String srcReqTime = DateFormatUtils.format(new Date(), "hhmmss");
        String nonce = UUID.randomUUID().toString().replace("-", "");

        UnionPayLoansBaseReq unionPayLoansBaseReq = UnionPayLoansBaseReq.builder()
                .lwzBussCode(lwzBussCode)
                .transCode("203000")
                .verNo("100")
                .srcReqDate(srcReqDate)
                .srcReqTime(srcReqTime)
                .srcReqId(nonce)
                .channelId("043")
                .merNo(null)
                .groupId(groupId)
                .lwzData(lwzData)
                .channelId("043")
                .lwzChannelType("19")
                .build();
        unionPayLoansBaseReq.setSignature(UnionPaySignUtil.sign(unionPayLoansBaseReq));
        return unionPayLoansBaseReq;
    }


    /**
     * 查询进件接口
     *
     * @param outRequestNo
     * @return
     */
    @Override
    public IncomingReturn getIncomingInfo(String outRequestNo) {
        IncomingReturn incomingReturn = new IncomingReturn();
        try {
            Map<String, String> map = new HashMap<>();
            map.put("outRequestNo", outRequestNo);

            UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ53_PERSON_APP_REQ.getCode(), JSON.toJSONString(map));
            ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);
            log.info("查询进件接口{}", responseEntity.getBody());
            incomingReturn = getBaseIncomingReturn(responseEntity, outRequestNo, null, null);
            //保存到异常表

        } catch (TfException e) {
            log.error("银联-进件-查询失败：param={}", JSON.toJSONString(outRequestNo), e);
            throw new TfException(e.getMessage());
        }

        return incomingReturn;
    }

    /**
     * 查询二级进件接口
     *
     * @param outRequestNo
     * @return
     */
    @Override
    public IncomingReturn getTwoIncomingInfo(String outRequestNo) {
        IncomingReturn incomingReturn = new IncomingReturn();
        try {
            Map<String, String> map = new HashMap<>();
            map.put("outRequestNo", outRequestNo);
            UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ58_MCH_APP_REQ.getCode(), JSON.toJSONString(map));
            ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);
            log.info("查询二级进件接口{}", responseEntity.getBody());
            incomingReturn = getBaseIncomingReturnDTO(responseEntity, outRequestNo, null, null);
            //保存到异常表
        } catch (TfException e) {
            log.error("银联-二级进件-查询失败：param={}", JSON.toJSONString(outRequestNo), e);
            throw new TfException(e.getMessage());
        }

        return incomingReturn;
    }


    /**
     * 进件公用返回值
     *
     * @param responseEntity
     * @return
     */
    private IncomingReturn getBaseIncomingReturnDTO(ResponseEntity<UnionPayLoansBaseReturn> responseEntity, Object p, Date req, Long loanUserId) {
        UnionPayLoansBaseReturn unionPayLoansBaseReturn = responseEntity.getBody();
        //日志保存
        if (null != req) {
            unionPayLoanReqLogService.asyncSaveLog(unionPayLoansBaseReturn, p, req, loanUserId);
        }
        if (!Objects.equals("LWZ99999", unionPayLoansBaseReturn.getRespCode())) {
            log.error("银联调用失败{}", responseEntity.getBody().toString());
            throw new TfException(unionPayLoansBaseReturn.getRespMsg());
        }

        if (Objects.equals("200", unionPayLoansBaseReturn.getRespLwzCode())) {
            IncomingReturn incomingReturn = JSON.parseObject(unionPayLoansBaseReturn.getLwzRespData().toString(), IncomingReturn.class);
            if (Objects.equals(incomingReturn.getApplicationStatus(), "failed")) {
                incomingReturn.setFailureMsgs(getLwzRespData(incomingReturn.getFailureMsgs()));
            }
            return incomingReturn;
        } else {
            log.error("银联-银行调用失败{}", responseEntity.getBody().toString());
            throw new TfException(getRespLwzMsgReturnMsg(unionPayLoansBaseReturn.getRespLwzMsg()));
        }
    }

    /*    *//**
     * 保存进件查询的原因
     *
     * @param unionPayLoansBaseReturn
     *//*
    public void saveNcomingReason(YinLianLoansBaseReturn unionPayLoansBaseReturn) {
        //todo 审核原因
        String respLwzMsgStr = unionPayLoansBaseReturn.getRespLwzMsg().getMessage();
        RespLwzMsgVO respLwzMsgVO = JSON.parseObject(respLwzMsgStr, RespLwzMsgVO.class);
        NcomingReasonEntity ncomingReason = new NcomingReasonEntity();
        ncomingReason.setFeildName(NcomingMappingFeildConstant.mapping.get(respLwzMsgVO.getField()));
        ncomingReason.setMappingFeildName(respLwzMsgVO.getField());
        ncomingReason.setFaildReason(respLwzMsgVO.getIssue());
        ncomingReasonService.save(ncomingReason);

    }*/


    /**
     * 图片上传
     *
     * @param file
     * @return
     */
    @Override
    public String upload(File file) {
        try {
            UnionPayLoansMetaDTO metaDTO = new UnionPayLoansMetaDTO();
            metaDTO.setFilename(file.getName());
            metaDTO.setSha256(MessageDigestUtils.sha256(file));
            UnionPayLoansImagesDTO unionPayLoansIncomingDTO = new UnionPayLoansImagesDTO();
            unionPayLoansIncomingDTO.setFile(Base64.encodeBase64String(Files.toByteArray(file)));
            unionPayLoansIncomingDTO.setMeta(metaDTO);
            SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
            filter.getExcludes().add("file");
            log.info("图片上传入参{}", JSON.toJSONString(unionPayLoansIncomingDTO,filter));

            UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ526_IMAGES.getCode(), JSON.toJSONString(unionPayLoansIncomingDTO));

            ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);
            log.info("图片上传出差{}", responseEntity.getBody());
            IncomingReturn incomingReturn = getBaseIncomingReturn(responseEntity, file.getName(), null, null);
            if (StringUtils.isNotBlank(incomingReturn.getMediaId())) {
                return incomingReturn.getMediaId();
            }
            throw new TfException(PayExceptionCodeEnum.UPLOAD_FILE_ERROR);
        } catch (Exception e) {
            log.error("", e);
            throw new TfException(e.getMessage());
        }
    }

    private UnionPayLoansIncomingEditDTO buildEditYinLianLoansIncoming(LoanUserEntity tfLoanUserEntity, String smsCode) {
        try {
            ValidatorUtils.validateEntity(tfLoanUserEntity, UpdateGroup.class);
        } catch (Exception e) {
            throw new TfException(e.getMessage());
        }
        if (StringUtil.isBlank(tfLoanUserEntity.getCusId())) {
            throw new TfException(PayExceptionCodeEnum.CUSID_NOT_NULL);
        }
        String outRequestNo = UUID.randomUUID().toString().replace("-", "");
        CustBankInfoEntity custBankInfoEntity = verifyCustBankInfo(tfLoanUserEntity.getId());
        int settlementType = custBankInfoEntity.getSettlementType();
        smsCode = handleCode(smsCode, custBankInfoEntity);

        //1 身份证
        IdCardDTO idCardDTO = buildIdCardDTO(tfLoanUserEntity.getId());


        SettleAcctDTO settleAcctDTO = buildSettleAcctDTO(custBankInfoEntity, null);

        UnionPayLoansIncomingEditDTO unionPayLoansIncomingEditDTO = UnionPayLoansIncomingEditDTO.builder()
                .cusId(tfLoanUserEntity.getCusId())
                .idCard(idCardDTO)
                .settleAcct(settleAcctDTO)
                .outRequestNo(outRequestNo)
                .mobileNumber(UnionPaySignUtil.SM2(encodedPub, custBankInfoEntity.getPhone()))
                .build();
        if (settlementType == 1) {
            unionPayLoansIncomingEditDTO.setSmsCode(smsCode);
        }
        return unionPayLoansIncomingEditDTO;
    }

    /**
     * 新增绑定账户
     *
     * @return
     */
    @Override
    public UnionPayLoansSettleAcctDTO bindAddSettleAcct(CustBankInfoEntity custBankInfoEntity) {
        //业务参数复制
        ReqYinLianLoansSettleAcctDTO unionPayLoansSettleAcctDTO = buildYinLianLoansSettleAcct(custBankInfoEntity);

        //新增
        UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ517_SETTLE_ACCT_ADD.getCode(), JSON.toJSONString(unionPayLoansSettleAcctDTO));
        Date req = new Date();
        //调用银联接口
        ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);

        UnionPayLoansBaseReturn unionPayLoansBaseReturn = responseEntity.getBody();
        unionPayLoanReqLogService.asyncSaveLog(unionPayLoansBaseReturn, custBankInfoEntity, req, custBankInfoEntity.getLoanUserId());
        log.info("绑定账户返回值{}", JSON.toJSONString(unionPayLoansBaseReturn));
        UnionPayLoansSettleAcctDTO unionPayLoansSettleAcct = JSON.parseObject(unionPayLoansBaseReturn.getLwzRespData().toString(), UnionPayLoansSettleAcctDTO.class);
        //异常校验
        getBaseIncomingReturnStr(responseEntity, null, null, null);
        //二次异常校验
        getBaseIncomingReturnStrTwice(unionPayLoansSettleAcct);

        return unionPayLoansSettleAcct;

    }

    //针对新增绑定账户失败进行二次异常校验
    private void getBaseIncomingReturnStrTwice(UnionPayLoansSettleAcctDTO unionPayLoansSettleAcct ) {
        String verifyStatus = unionPayLoansSettleAcct.getVerifyStatus();
        if (StringUtils.isNotBlank(verifyStatus) && "failed".equals(verifyStatus)) {
            log.error("绑定银行卡信息失败{}", unionPayLoansSettleAcct.toString());
            String acctValidationFailureMsg = unionPayLoansSettleAcct.getAcctValidationFailureMsg();
            if (StringUtils.isBlank(acctValidationFailureMsg)) {
                acctValidationFailureMsg = PayExceptionCodeEnum.BIND_BANK_CARD_FAILED.getMsg();
            }
            throw new TfException(acctValidationFailureMsg);
        }
    }


    private void delSettleAcct(String cusId, String bankCardNo, Integer settlementType, String mchId) {
        ReqDeleteSettleAcctParams deleteSettleAcctParams = new ReqDeleteSettleAcctParams();
        if (String.valueOf(settlementType).equals(BankTypeEnum.PERSONAL.getCode())) {
            deleteSettleAcctParams.setCusId(cusId);
        }
        if (String.valueOf(settlementType).equals(BankTypeEnum.CORPORATE.getCode())) {
            deleteSettleAcctParams.setMchId(mchId);
        }
        deleteSettleAcctParams.setBankAcctNo(UnionPaySignUtil.SM2(encodedPub, bankCardNo));
        UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ522_SETTLE_ACCTS_DELETE.getCode(), JSON.toJSONString(deleteSettleAcctParams));
        Date req = new Date();
        ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);
        UnionPayLoansBaseReturn unionPayLoansBaseReturn = responseEntity.getBody();
        unionPayLoanReqLogService.asyncSaveLog(unionPayLoansBaseReturn, bankCardNo, req, 1L);
    }

    /**
     * 构建请求参数
     *
     * @param custBankInfoEntity
     * @return
     */
    private ReqYinLianLoansSettleAcctDTO buildYinLianLoansSettleAcct(CustBankInfoEntity custBankInfoEntity) {
        LoanUserEntity tfLoanUser = loanUserService.getById(custBankInfoEntity.getLoanUserId());
        ReqYinLianLoansSettleAcctDTO unionPayLoansSettleAcctDTO = new ReqYinLianLoansSettleAcctDTO();

        if (ObjectUtils.isNotEmpty(tfLoanUser)) {
            //平台订单号
            unionPayLoansSettleAcctDTO.setOutRequestNo(UUID.randomUUID().toString().replace("-", ""));
            //个人用户ID
            unionPayLoansSettleAcctDTO.setCusId(tfLoanUser.getCusId());
            //银行账户类型
            unionPayLoansSettleAcctDTO.setBankAcctType(String.valueOf(custBankInfoEntity.getSettlementType()));
            unionPayLoansSettleAcctDTO.setMchId(tfLoanUser.getMchId());
            //开户银行编码
            unionPayLoansSettleAcctDTO.setBankCode(custBankInfoEntity.getBankCode());
            //开户银行省市编码
            unionPayLoansSettleAcctDTO.setBankAddressCode(custBankInfoEntity.getCity());
            //开户银行联行号
            unionPayLoansSettleAcctDTO.setBankBranchCode(custBankInfoEntity.getBankBranchCode());
            //银行账号
            String encCardNo = UnionPaySignUtil.SM2(encodedPub, custBankInfoEntity.getBankCardNo());
            unionPayLoansSettleAcctDTO.setBankAcctNo(encCardNo);
            //profession
            unionPayLoansSettleAcctDTO.setProfession(custBankInfoEntity.getCareer());

            String encPhone = UnionPaySignUtil.SM2(encodedPub, custBankInfoEntity.getPhone());
            unionPayLoansSettleAcctDTO.setMobileNumber(encPhone);
            unionPayLoansSettleAcctDTO.setSmsCode(custBankInfoEntity.getSmsCode());
        } else {
            throw new TfException(PayExceptionCodeEnum.NO_DATA);
        }

        return unionPayLoansSettleAcctDTO;
    }


    /**
     * 更新用户ID查询绑定账号
     *
     * @param id
     * @return
     */
    @Override
    public SettleAcctsMxDTO querySettleAcct(Integer id) {


        LoanUserEntity tfLoanUserEntity = loanUserService.getById(id);
        //业务参数复制
        Map<String, String> reqParams = new HashMap<>();
        if (Objects.equals(tfLoanUserEntity.getLoanUserType(), 0)) {
            reqParams.put("cusId", tfLoanUserEntity.getCusId());
        }
        if (!Objects.equals(tfLoanUserEntity.getLoanUserType(), 0)) {
            reqParams.put("mchId", tfLoanUserEntity.getMchId());
        }

        ResponseEntity<UnionPayLoansBaseReturn> responseEntity = getUnionPayInfoByUserEntity(reqParams);
        SettleAcctsMxDTO settleAcctsMxDTO = new SettleAcctsMxDTO();
        UnionPayLoansBaseReturn unionPayLoansBaseReturn = responseEntity.getBody();
        unionPayLoanReqLogService.asyncSaveLog(unionPayLoansBaseReturn, reqParams, null, null);
        log.info("查询账户返回值{}", JSON.toJSONString(unionPayLoansBaseReturn));
        SettleAcctsDTO settleAcctDTO = JSON.parseObject(unionPayLoansBaseReturn.getLwzRespData().toString(), SettleAcctsDTO.class);
        if (settleAcctDTO != null && settleAcctDTO.getSettleAccts() != null && settleAcctDTO.getSettleAccts().size() > 0) {
            CustBankInfoEntity custBankInfoEntity = custBankInfoService.getByLoanUserId(tfLoanUserEntity.getId());
            if (custBankInfoEntity != null) {
                List<SettleAcctsMxDTO> list = settleAcctDTO.getSettleAccts().stream().filter(i -> Objects.equals(i.getBankAcctNo(), custBankInfoEntity.getBankCardNo())).collect(Collectors.toList());
                if (list.size() > 0) {
                    settleAcctsMxDTO = list.get(0);
                }
            }
        }
        getBaseIncomingReturnStr(responseEntity, null, null, null);
        return settleAcctsMxDTO;
    }

    private ResponseEntity<UnionPayLoansBaseReturn> getUnionPayInfoByUserEntity(Map<String, String> reqParams) {

        UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ520_SETTLE_ACCTS_QUERY.getCode(), JSON.toJSONString(reqParams));
        //调用银联接口
        ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);

        return responseEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UnionPayLoansSettleAcctDTO querySettleAcctByOutRequestNo(Long loanUserId, String outRequestNo) {

        CustBankInfoEntity custBankInfoEntity = custBankInfoService.getByLoanUserId(loanUserId);
        //业务参数复制
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("outRequestNo", outRequestNo);
        UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ519_SETTLE_ACCTS_QUERY.getCode(), JSON.toJSONString(reqParams));
        //调用银联接口
        ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);
        UnionPayLoansBaseReturn unionPayLoansBaseReturn = responseEntity.getBody();
        unionPayLoanReqLogService.asyncSaveLog(unionPayLoansBaseReturn, reqParams, null, null);
        log.info("查询账户返回值{}", JSON.toJSONString(unionPayLoansBaseReturn));
        UnionPayLoansSettleAcctDTO unionPayLoansSettleAcct = JSON.parseObject(unionPayLoansBaseReturn.getLwzRespData().toString(), UnionPayLoansSettleAcctDTO.class);
        getBaseIncomingReturnStr(responseEntity, null, null, null);

        if (Objects.equals(unionPayLoansSettleAcct.getVerifyStatus(), "succeeded")) {
            custBankInfoEntity.setVerifyStatus("succeeded");
            custBankInfoService.updateById(custBankInfoEntity);
        }
        return unionPayLoansSettleAcct;
    }


    /**
     * 删除结算账户
     *
     * @param deleteSettleAcctParams
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSettleAcct(ReqDeleteSettleAcctParams deleteSettleAcctParams) {
        UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ522_SETTLE_ACCTS_DELETE.getCode(), JSON.toJSONString(deleteSettleAcctParams));
        Date req = new Date();
        ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);
        UnionPayLoansBaseReturn unionPayLoansBaseReturn = responseEntity.getBody();

        log.info("删除账户返回值{}", JSON.toJSONString(unionPayLoansBaseReturn));
        IncomingReturn incomingReturn = getBaseIncomingReturn(responseEntity, deleteSettleAcctParams, req, 1L);
        log.info("删除结果{}", incomingReturn);
/*        if(incomingReturn.getIsDeleted()==null){
            return false;
        }
        return incomingReturn.getIsDeleted();*/
    }

    @Override
    public String validationMobileNumber(String mobileNumber) {
        IncomingReturn incomingReturn = new IncomingReturn();
        try {
            Map<String, String> map = new HashMap<>();
            map.put("mobileNumber", mobileNumber);

            UnionPayLoansBaseReq unionPayLoansBaseReq = baseBuilder(UnionPayLoanBussCodeEnum.LWZ55_PERSONAL_VALIDATION_SMS_CODES.getCode(), JSON.toJSONString(map));
            ResponseEntity<UnionPayLoansBaseReturn> responseEntity = post(unionPayLoansBaseReq);
            log.info("查询进件接口{}", responseEntity.getBody().toString());
            incomingReturn = getBaseIncomingReturn(responseEntity, mobileNumber, null, null);
            this.saveRedisSmsCode(mobileNumber);
            return incomingReturn.getMobileNumber();
        } catch (TfException e) {
            log.error("银联-进件-个人手机号验证失败：param={}", mobileNumber, e);
            throw new TfException(e.getMessage());
        }
    }

    private void saveRedisSmsCode(String mobileNumber) {
        String key = RedisConstant.MOBILE_VERIFICATION_CODE + mobileNumber;
        String value = "used";
        redisCache.setCacheObject(key, value, 5, TimeUnit.MINUTES);
    }

    @Override
    public Boolean getMobileStatus(String mobile) {
        Boolean flag = false;
        String key = RedisConstant.MOBILE_VERIFICATION_CODE + mobile;
        Object cacheObject = redisCache.getCacheObject(key);
        if (null != cacheObject) {
            flag = true;
        }
        return flag;
    }

    @Override
    public UnionPayLoansSettleAcctDTO delAndBindAddSettleAcct(CustBankInfoEntity custBankInfo, String oldBankCardNo) {
        log.info("更新结算信息{}", custBankInfo.toString());
        //先删除
        LoanUserInfoDTO loanUerInfo = loanUserService.getLoanUerInfo(custBankInfo.getLoanUserId());

        //修改
        //先绑定
        Long loanUserId = custBankInfo.getLoanUserId();
        LoanUserEntity byId = loanUserService.getById(loanUserId);
        LoanUserEntity loanUserEntityOld = byId;
        UnionPayLoansSettleAcctDTO unionPayLoansSettleAcctDTO = bindAddSettleAcct(custBankInfo);
        String settleAcctId = unionPayLoansSettleAcctDTO.getSettleAcctId();
        String verifyStatus = unionPayLoansSettleAcctDTO.getVerifyStatus();
        if ("PROCESSING".equals(verifyStatus.toUpperCase())) {
            byId.setBankCallStatus(1);
        }
        byId.setSettleAcctId(settleAcctId);
        loanUserService.updateById(byId);
        keyInformationChangeRecordLogService.saveLog(byId.getId(),null,null,settleAcctId,loanUserEntityOld);
        //再删除
        this.delSettleAcct(loanUerInfo.getCusId(), oldBankCardNo, custBankInfo.getSettlementType(), byId.getMchId());
        return unionPayLoansSettleAcctDTO;
    }


}
