package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.tfjt.constant.MessageStatusEnum;
import com.tfjt.entity.AsyncMessageEntity;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.biz.IncomingBizService;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.RetryMessageConstant;
import com.tfjt.pay.external.unionpay.dto.CheckCodeMessageDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingDataIdDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.dto.message.IncomingFinishDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingChangeAccessMainTypeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingCheckCodeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingInfoReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSubmitMessageReqDTO;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.enums.*;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.strategy.incoming.AbstractIncomingService;
import com.tfjt.pay.external.unionpay.utils.NetworkTypeCacheUtil;
import com.tfjt.producter.ProducerMessageApi;
import com.tfjt.producter.service.AsyncMessageService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 20:07
 * @description
 */
@Slf4j
@Service
public class IncomingBizServiceImpl implements IncomingBizService {

    @Autowired
    private Map<String, AbstractIncomingService> abstractIncomingServiceMap;

    @Autowired
    private TfIncomingInfoService tfIncomingInfoService;

    @Autowired
    private ProducerMessageApi producerMessageApi;

    @Autowired
    private AsyncMessageService asyncMessageService;

    @Autowired
    private TfIncomingMerchantInfoService tfIncomingMerchantInfoService;

    @Autowired
    private TfIdcardInfoService tfIdcardInfoService;

    @Autowired
    private TfIncomingBusinessInfoService tfIncomingBusinessInfoService;

    @Autowired
    private TfBusinessLicenseInfoService tfBusinessLicenseInfoService;

    @Autowired
    private TfIncomingSettleInfoService tfIncomingSettleInfoService;

    @Autowired
    private TfBankCardInfoService tfBankCardInfoService;

    @Autowired
    private SalesAreaIncomingChannelService salesAreaIncomingChannelService;

    @Autowired
    private ITfIncomingImportService tfIncomingImportService;

    @Autowired
    private IdentifierGenerator identifierGenerator;

    @Autowired
    private NetworkTypeCacheUtil networkTypeCacheUtil;


    @Value("${rocketmq.topic.incomingFinish}")
    private String incomingFinishTopic;

    private static final String MQ_FROM_SERVER = "tf-cloud-pay-center";

    private static final String MQ_TO_SERVER = "tf-cloud-shop";

    @Override
    public Result incomingSave(IncomingInfoReqDTO incomingInfoReqDTO) {
        try {
            log.info("IncomingBizServiceImpl---incomingSave, incomingInfoReqDTO:{}", JSONObject.toJSONString(incomingInfoReqDTO));
            ValidatorUtils.validateEntity(incomingInfoReqDTO, AddGroup.class);
            //保存进件主表信息
            TfIncomingInfoEntity tfIncomingInfoEntity = new TfIncomingInfoEntity();
            BeanUtils.copyProperties(incomingInfoReqDTO, tfIncomingInfoEntity);
            String memberId = IncomingMemberBusinessTypeEnum.fromCode(incomingInfoReqDTO.getBusinessType().intValue()).getMemberPrefix()
                    + incomingInfoReqDTO.getBusinessId();
            tfIncomingInfoEntity.setMemberId(memberId);
            tfIncomingInfoEntity.setAccessStatus(IncomingAccessStatusEnum.MESSAGE_FILL_IN.getCode());
            if (!tfIncomingInfoService.save(tfIncomingInfoEntity)) {
                log.error("IncomingBizServiceImpl---incomingSave, 保存进件主表信息失败:{}", JSONObject.toJSONString(tfIncomingInfoEntity));
                throw new TfException(ExceptionCodeEnum.FAIL);
            }
            return Result.ok();
        } catch (TfException e) {
            log.error("平安进件-保存进件主表信息 发生 RenException:", e);
            throw new TfException(e.getMessage());
        } catch (Exception e) {
            log.error("平安进件-保存进件主表信息 发生 Exception:", e);
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }

    /**
     * 提交基本信息、获取验证码
     * @return
     */
    @Override
    public Result incomingSubmit(IncomingSubmitMessageReqDTO incomingSubmitMessageReqDTO) {
        log.info("IncomingBizServiceImpl--incomingSubmit, incomingSubmitMessageReqDTO:{}", JSONObject.toJSONString(incomingSubmitMessageReqDTO));
        //查询提交进件申请所需信息
        IncomingSubmitMessageDTO incomingSubmitMessageDTO =
                tfIncomingInfoService.queryIncomingMessage(incomingSubmitMessageReqDTO.getIncomingId());
        //根据参数类型获取实现类
        String bindServiceName = getServiceName(incomingSubmitMessageDTO);
        AbstractIncomingService abstractIncomingService = abstractIncomingServiceMap.get(bindServiceName);
        //实现类为空时，直接返回
        if (ObjectUtils.isEmpty(abstractIncomingService)) {
            log.error("IncomingBizServiceImpl--incomingSubmit, abstractIncomingService isEmpty, bindServiceName:{}", bindServiceName);
            return Result.failed(ExceptionCodeEnum.INCOMING_STRATEGY_SERVICE_IS_NULL);
        }
        //调用实现类方法
        abstractIncomingService.incomingSubmit(incomingSubmitMessageDTO);
        //更新进件信息

        return Result.ok();
    }

    /**
     * 回填校验验证码、打款金额，验证协议
     * @return
     */
    @Override
    public Result checkCode(IncomingCheckCodeReqDTO inComingCheckCodeReqDTO) {
        log.info("IncomingBizServiceImpl--checkCode, incomingSubmitMessageReqDTO:{}", JSONObject.toJSONString(inComingCheckCodeReqDTO));
        IncomingSubmitMessageDTO incomingSubmitMessageDTO =
                tfIncomingInfoService.queryIncomingMessage(inComingCheckCodeReqDTO.getIncomingId());
        //根据进件信息类型数据获取对应实现
        String bindServiceName = getServiceName(incomingSubmitMessageDTO);
        AbstractIncomingService abstractIncomingService = abstractIncomingServiceMap.get(bindServiceName);
        //实现类为空时，直接返回
        if (ObjectUtils.isEmpty(abstractIncomingService)) {
            log.error("IncomingBizServiceImpl--incomingSubmit, abstractIncomingService isEmpty, bindServiceName:{}", bindServiceName);
            return Result.failed(ExceptionCodeEnum.INCOMING_STRATEGY_SERVICE_IS_NULL);
        }
        CheckCodeMessageDTO checkCodeMessageDTO = CheckCodeMessageDTO.builder()
                .id(incomingSubmitMessageDTO.getId())
                .memberId(incomingSubmitMessageDTO.getMemberId())
                .accountNo(incomingSubmitMessageDTO.getAccountNo())
                .bankCardNo(incomingSubmitMessageDTO.getBankCardNo())
                .authAmt(inComingCheckCodeReqDTO.getAuthAmt())
                .messageCheckCode(inComingCheckCodeReqDTO.getMessageCheckCode())
                .ipAddress(inComingCheckCodeReqDTO.getIpAddress())
                .macAddress(inComingCheckCodeReqDTO.getMacAddress())
                .signChannel(incomingSubmitMessageDTO.getSignChannel())
                .accessStatus(incomingSubmitMessageDTO.getAccessStatus()).build();
        //调用实现类方法
        abstractIncomingService.checkCode(checkCodeMessageDTO);
        //异步发送mq-进件完成事件
        MQProcess(incomingSubmitMessageDTO);
        //更新进件信息
        return Result.ok();
    }

    /**
     * 根据商户信息查询进件信息
     * @param incomingMessageReqDTO
     * @return
     */
    @Override
    public Result<IncomingMessageRespDTO> queryIncomingMessage(IncomingMessageReqDTO incomingMessageReqDTO) {
        log.info("IncomingBizServiceImpl--queryIncomingMessage, incomingMessageReqDTO:{}", JSONObject.toJSONString(incomingMessageReqDTO));
        ValidatorUtils.validateEntity(incomingMessageReqDTO);
        //入参中“渠道类型”与“区域code”不能同时为空
        if (ObjectUtils.isEmpty(incomingMessageReqDTO.getAccessChannelType()) && StringUtils.isBlank(incomingMessageReqDTO.getAreaCode())) {
            return Result.failed(ExceptionCodeEnum.QUERY_INCOMING_MSG_ILLEGAL_ARGUMENT);
        }
        //入参“渠道类型”为空时，根据区域code获取
        if (ObjectUtils.isEmpty(incomingMessageReqDTO.getAccessChannelType())) {
            incomingMessageReqDTO.setAccessChannelType(getAccessChannelType(incomingMessageReqDTO.getAreaCode()));
        }
        IncomingMessageRespDTO incomingMessageRespDTO = tfIncomingInfoService.queryIncomingMessageByMerchant(incomingMessageReqDTO);
        if (ObjectUtils.isEmpty(incomingMessageRespDTO)) {
            return Result.ok();
        }
        //如果结算类型为对公，会员名称返回“营业名称”，否则返回“法人姓名”
        if (IncomingSettleTypeEnum.CORPORATE.getCode().equals(incomingMessageRespDTO.getSettlementAccountType())) {
            incomingMessageRespDTO.setMemberName(incomingMessageRespDTO.getBusinessName());
        } else {
            incomingMessageRespDTO.setMemberName(incomingMessageRespDTO.getLegalName());
        }
        return Result.ok(tfIncomingInfoService.queryIncomingMessageByMerchant(incomingMessageReqDTO));
    }

    /**
     * 根据多个商户信息批量查询进件信息
     * @param incomingMessageReqs
     * @return
     */
    @Override
    public Result<Map<String, IncomingMessageRespDTO>> queryIncomingMessages(List<IncomingMessageReqDTO> incomingMessageReqs) {
        log.info("IncomingBizServiceImpl--queryIncomingMessages, incomingMessageReqDTO:{}", JSONObject.toJSONString(incomingMessageReqs));
        if (incomingMessageReqs.isEmpty()) {
            return Result.failed(ExceptionCodeEnum.ILLEGAL_ARGUMENT);
        }
        List<IncomingMessageRespDTO>  incomingMessageRespDTOS = tfIncomingInfoService.queryIncomingMessagesByMerchantList(incomingMessageReqs);
        log.info("IncomingBizServiceImpl--queryIncomingMessages, incomingMessageRespDTOS:{}", JSONObject.toJSONString(incomingMessageRespDTOS));
        if (CollectionUtils.isEmpty(incomingMessageRespDTOS)) {
            return Result.failed(ExceptionCodeEnum.IS_NULL);
        }
        Map<String, IncomingMessageRespDTO> incomingMessageMap = new HashMap<>();
        //将查询到的数据集合，以“入网渠道”-“商户类型”-“商户id”为key放入map
        incomingMessageRespDTOS.forEach(incomingMessage -> {
            String key = incomingMessage.getAccessChannelType() + "-" + incomingMessage.getBusinessType() + incomingMessage.getBusinessId();
            incomingMessageMap.put(key, incomingMessage);
        });
        return Result.ok(incomingMessageMap);
    }

    /**
     * 变更进件主体类型
     * @param changeAccessMainTypeReqDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = {TfException.class, Exception.class})
    public Result changeAccessMainType(IncomingChangeAccessMainTypeReqDTO changeAccessMainTypeReqDTO) {
        log.info("IncomingBizServiceImpl--changeAccessMainType, changeAccessMainTypeReqDTO:{}", JSONObject.toJSONString(changeAccessMainTypeReqDTO));
        ValidatorUtils.validateEntity(changeAccessMainTypeReqDTO);
        //判断入参中主体类型是否与枚举中类型匹配
        if (ObjectUtils.isEmpty(IncomingAccessMainTypeEnum.fromCode(changeAccessMainTypeReqDTO.getAccessMainType()))) {
            return Result.failed(ExceptionCodeEnum.INCOMING_CHANGE_MAIN_TYPE_CODE_ERROR);
        }
        //根据进件id查询相关表id
        IncomingDataIdDTO incomingDataIdDTO = tfIncomingInfoService.queryIncomingDataId(changeAccessMainTypeReqDTO.getId());
        log.info("IncomingBizServiceImpl--changeAccessMainType, incomingDataIdDTO:{}", JSONObject.toJSONString(incomingDataIdDTO));
        if (ObjectUtils.isEmpty(incomingDataIdDTO)) {
            return Result.failed(ExceptionCodeEnum.IS_NULL);
        }
        TfIncomingInfoEntity tfIncomingInfoEntity = new TfIncomingInfoEntity();
        tfIncomingInfoEntity.setId(changeAccessMainTypeReqDTO.getId());
        tfIncomingInfoEntity.setAccessMainType(changeAccessMainTypeReqDTO.getAccessMainType().byteValue());
        //变更主表进件主体类型
        if (!tfIncomingInfoService.updateById(tfIncomingInfoEntity)) {
            log.error("IncomingBizServiceImpl--changeAccessMainType, incomingId:{}", changeAccessMainTypeReqDTO.getId());
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        //清除身份信息
        clearMerchantInfo(incomingDataIdDTO);
        //清除营业信息
        clearBusinessInfo(incomingDataIdDTO);
        //清除结算信息
        clearSettleInfo(incomingDataIdDTO);
        return Result.ok();
    }

    /**
     * 银联入网数据抽取
     * @return
     */
    @Override
    public Result unionpayDataExtract() {
        log.info("IncomingBizServiceImpl--unionpayDataExtract, start");
        TfIncomingImportEntity tfIncomingImportStart = tfIncomingImportService.queryNotSubmitMinIdData();
        log.info("IncomingBizServiceImpl--unionpayDataExtract, tfIncomingImportStart:{}", JSONObject.toJSONString(tfIncomingImportStart));
        if (ObjectUtils.isEmpty(tfIncomingImportStart)) {
            log.warn("IncomingBizServiceImpl--unionpayDataExtract, tfIncomingImportStart isEmpty");
            return Result.ok();
        }
        boolean extractFlag = true;
        long startId = tfIncomingImportStart.getId();
        while(extractFlag) {
            List<TfIncomingImportEntity> importEntityList = tfIncomingImportService.queryListByStartId(startId);
            importEntityList.forEach(importEntity -> {
                incomingMessageWrite(importEntity);
            });

            startId = importEntityList.get(importEntityList.size() - 1).getId();
            if (importEntityList.size() < 100) {
                extractFlag = false;
            }
        }
        log.info("IncomingBizServiceImpl--unionpayDataExtract, end");
        return null;
    }

    /**
     * 根据进行信息获取实现类name
     * @param incomingSubmitMessageDTO
     * @return
     */
    private String getServiceName(IncomingSubmitMessageDTO incomingSubmitMessageDTO) {
        //根据进件信息类型数据获取对应实现
        String bindServiceName = IncomingAccessChannelTypeEnum.getNameFromCode(incomingSubmitMessageDTO.getAccessChannelType()) +
                "_" + IncomingAccessTypeEnum.getNameFromCode(incomingSubmitMessageDTO.getAccessType()) +
                "_" + IncomingSettleTypeEnum.getNameFromCode(incomingSubmitMessageDTO.getSettlementAccountType());
        return bindServiceName;
    }

    private AsyncMessageEntity createMessage(String messageType,String messageBody,String uniqueNo){
        AsyncMessageEntity message = new AsyncMessageEntity();
        // 生产者application name
        message.setFromServerName(MQ_FROM_SERVER);
        // 消费者application name
        message.setToServerName(MQ_TO_SERVER);
        // 消息队列的topic
        message.setTopic(incomingFinishTopic);
        // 消息队列的tag
        message.setMsgTag("");
        // 定义的业务消息类型
        message.setMsgType(messageType);
        // 消息内容
        message.setMsgBody(messageBody);
        // 业务的唯一序列号
        message.setUniqueNo(uniqueNo);
        return message;
    }

    /**
     * 异步发送消息
     * @param incomingMessage
     */
    @Async
    public void MQProcess(IncomingSubmitMessageDTO incomingMessage){
        log.info("IncomingBizServiceImpl--MQProcess, start incomingMessage:{}", JSONObject.toJSONString(incomingMessage));
        IncomingFinishDTO incomingFinishDTO = IncomingFinishDTO.builder()
                .id(incomingMessage.getId())
                .accessChannelType(incomingMessage.getAccessChannelType())
                .accessMainType(incomingMessage.getAccessMainType())
                .accountNo(incomingMessage.getAccountNo()).build();
        // 创建消息
        AsyncMessageEntity messageEntity = createMessage(RetryMessageConstant.INCOMING_FINISH,
                JSONObject.toJSONString(incomingFinishDTO), identifierGenerator.nextId(AsyncMessageEntity.class).toString());
        // 调用jar包中保存消息到数据库的方法
        asyncMessageService.saveMessage(messageEntity);
        // rocketMQ发送消息自行实现
        boolean result = producerMessageApi.sendMessage(messageEntity.getTopic(), JSONUtil.toJsonStr(messageEntity),messageEntity.getUniqueNo(),
                messageEntity.getMsgTag());
        //更新状态为成功
        messageEntity.setStatus(result ? MessageStatusEnum.SUCCESS.getCode() : MessageStatusEnum.FAILED.getCode());
        asyncMessageService.updateMessageStatus(messageEntity);
        log.info("IncomingBizServiceImpl--MQProcess, end");
    }

    /**
     * 清除身份信息
     * @param incomingDataIdDTO
     */
    private void clearMerchantInfo(IncomingDataIdDTO incomingDataIdDTO) {
        if (ObjectUtils.isEmpty(incomingDataIdDTO.getMerchantInfoId())) {
            return;
        }
        TfIncomingMerchantInfoEntity incomingMerchantInfoEntity = new TfIncomingMerchantInfoEntity();
        incomingMerchantInfoEntity.setId(incomingDataIdDTO.getMerchantInfoId());
        incomingMerchantInfoEntity.setIsDeleted(NumberConstant.ONE.byteValue());
        //清除商户身份信息表数据
        if (!tfIncomingMerchantInfoService.updateById(incomingMerchantInfoEntity)) {
            log.error("IncomingBizServiceImpl--changeAccessMainType, merchantInfoId:{}", incomingDataIdDTO.getMerchantInfoId());
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        TfIdcardInfoEntity legalIdEntity = new TfIdcardInfoEntity();
        legalIdEntity.setId(incomingDataIdDTO.getLegalId());
        legalIdEntity.setIsDeleted(NumberConstant.ONE.byteValue());
        //清除法人证件信息表数据
        if (!tfIdcardInfoService.updateById(legalIdEntity)) {
            log.error("IncomingBizServiceImpl--changeAccessMainType, legalId:{}", incomingDataIdDTO.getLegalId());
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        if (ObjectUtils.isEmpty(incomingDataIdDTO.getAgentId())) {
            return;
        }
        TfIdcardInfoEntity agentIdEntity = new TfIdcardInfoEntity();
        agentIdEntity.setId(incomingDataIdDTO.getAgentId());
        agentIdEntity.setIsDeleted(NumberConstant.ONE.byteValue());
        //清除经办人证件信息表数据
        if (!tfIdcardInfoService.updateById(agentIdEntity)) {
            log.error("IncomingBizServiceImpl--changeAccessMainType, agentId:{}", incomingDataIdDTO.getAgentId());
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }

    /**
     * 清除营业信息
     * @param incomingDataIdDTO
     */
    private void clearBusinessInfo(IncomingDataIdDTO incomingDataIdDTO) {
        if (ObjectUtils.isEmpty(incomingDataIdDTO.getBusinessInfoId())) {
            return;
        }
        TfIncomingBusinessInfoEntity tfIncomingBusinessInfoEntity = TfIncomingBusinessInfoEntity.builder()
                .id(incomingDataIdDTO.getBusinessInfoId())
                .isDeleted(NumberConstant.ONE.byteValue()).build();
        //清除营业信息表数据
        if (!tfIncomingBusinessInfoService.updateById(tfIncomingBusinessInfoEntity)) {
            log.error("IncomingBizServiceImpl--changeAccessMainType, businessInfoId:{}", incomingDataIdDTO.getBusinessInfoId());
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        TfBusinessLicenseInfoEntity tfBusinessLicenseInfoEntity = new TfBusinessLicenseInfoEntity();
        tfBusinessLicenseInfoEntity.setId(incomingDataIdDTO.getBusinessLicenseId());
        tfBusinessLicenseInfoEntity.setIsDeleted(NumberConstant.ONE.byteValue());
        //清除营业执照信息表数据
        if (!tfBusinessLicenseInfoService.updateById(tfBusinessLicenseInfoEntity)) {
            log.error("IncomingBizServiceImpl--changeAccessMainType, businessLicenseId:{}", incomingDataIdDTO.getBusinessLicenseId());
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }

    /**
     * 清除结算信息
     * @param incomingDataIdDTO
     */
    private void clearSettleInfo(IncomingDataIdDTO incomingDataIdDTO) {
        if (ObjectUtils.isEmpty(incomingDataIdDTO.getSettleInfoId())) {
            return;
        }
        TfIncomingSettleInfoEntity tfIncomingSettleInfoEntity = new TfIncomingSettleInfoEntity();
        tfIncomingSettleInfoEntity.setId(incomingDataIdDTO.getSettleInfoId());
        tfIncomingSettleInfoEntity.setIsDeleted(NumberConstant.ONE.byteValue());
        //清除结算信息表数据
        if (!tfIncomingSettleInfoService.updateById(tfIncomingSettleInfoEntity)) {
            log.error("IncomingBizServiceImpl--changeAccessMainType, settleInfoId:{}", incomingDataIdDTO.getSettleInfoId());
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        TfBankCardInfoEntity tfBankCardInfoEntity = new TfBankCardInfoEntity();
        tfBankCardInfoEntity.setId(incomingDataIdDTO.getBankCardId());
        tfBankCardInfoEntity.setIsDeleted(NumberConstant.ONE.byteValue());
        //清除结算银行卡信息表数据
        if (!tfBankCardInfoService.updateById(tfBankCardInfoEntity)) {
            log.error("IncomingBizServiceImpl--changeAccessMainType, bankCardId:{}", incomingDataIdDTO.getBankCardId());
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }

    /**
     * 根据商户所属区域获取进件渠道
     * @param areaCode
     * @return
     */
    private Integer getAccessChannelType(String areaCode) {
        List<String> cacheList = networkTypeCacheUtil.getNetworkTypeCacheList();
        if (cacheList.contains(areaCode)) {
            //新城
            return IncomingAccessChannelTypeEnum.PINGAN.getCode();
        } else {
            //老城
            return IncomingAccessChannelTypeEnum.UNIONPAY.getCode();
        }
    }

    /**
     * 写入进件信息
     */
    @Transactional(rollbackFor = {TfException.class, Exception.class})
    public void incomingMessageWrite(TfIncomingImportEntity tfIncomingImportEntity) {
        TfIncomingInfoEntity incomingInfoEntity = new TfIncomingInfoEntity();
        BeanUtils.copyProperties(tfIncomingImportEntity, incomingInfoEntity);
        incomingInfoEntity.setAccessStatus(IncomingAccessStatusEnum.IMPORTS_CLOSURE.getCode());
        String memberId = IncomingMemberBusinessTypeEnum.fromCode(tfIncomingImportEntity.getBusinessType().intValue()).getMemberPrefix()
                + tfIncomingImportEntity.getBusinessId();
        incomingInfoEntity.setMemberId(memberId);
        if (!tfIncomingInfoService.save(incomingInfoEntity)) {
            log.error("IncomingBizServiceImpl--saveMerchantInfo，保存进件主表信息失败:{}", JSONObject.toJSONString(incomingInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        //保存商户身份信息
        saveMerchantInfo(tfIncomingImportEntity, incomingInfoEntity.getId());
        //保存营业信息
        saveBusinessInfo(tfIncomingImportEntity, incomingInfoEntity.getId());
        //保存结算信息
        saveSettleInfo(tfIncomingImportEntity, incomingInfoEntity.getId());
    }

    /**
     * 保存身份信息
     * @param tfIncomingImportEntity
     * @param incomingId
     */
    private void saveMerchantInfo(TfIncomingImportEntity tfIncomingImportEntity, Long incomingId) {
        TfIncomingMerchantInfoEntity tfIncomingMerchantInfoEntity = new TfIncomingMerchantInfoEntity();
        BeanUtils.copyProperties(tfIncomingImportEntity, tfIncomingMerchantInfoEntity);
        tfIncomingMerchantInfoEntity.setIncomingId(incomingId);
        //保存商户身份-法人信息
        TfIdcardInfoEntity legalIdcardInfoEntity = saveLegal(tfIncomingImportEntity);
        tfIncomingMerchantInfoEntity.setLegalIdCard(legalIdcardInfoEntity.getId());
        //进件主体类型非企业时，无需处理经办人信息
        if (IncomingAccessMainTypeEnum.COMPANY.getCode().equals(tfIncomingImportEntity.getAccessMainType())) {
            TfIdcardInfoEntity agentIdcardInfoEntity = saveAgent(tfIncomingImportEntity);
            tfIncomingMerchantInfoEntity.setAgentIdCard(agentIdcardInfoEntity.getId());
        }
        if (!tfIncomingMerchantInfoService.save(tfIncomingMerchantInfoEntity)) {
            log.error("IncomingBizServiceImpl--saveMerchantInfo，保存商户身份信息失败:{}", JSONObject.toJSONString(tfIncomingMerchantInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }

    /**
     * 保存法人身份信息
     * @param tfIncomingImportEntity
     * @return
     */
    private TfIdcardInfoEntity saveLegal(TfIncomingImportEntity tfIncomingImportEntity) {
        TfIdcardInfoEntity legalIdcardInfoEntity = TfIdcardInfoEntity.builder().
                idType(IdTypeEnum.ID_CARD.getCode()).
                idNo(tfIncomingImportEntity.getLegalIdNo()).name(tfIncomingImportEntity.getLegalName()).
                frontIdCardUrl(tfIncomingImportEntity.getLegalFrontIdCardUrl()).
                backIdCardUrl(tfIncomingImportEntity.getLegalBackIdCardUrl()).
                idEffectiveDate(tfIncomingImportEntity.getLegalIdEffectiveDate()).
                idExpiryDate(tfIncomingImportEntity.getLegalIdExpiryDate()).
                isLongTerm(tfIncomingImportEntity.getLegalIdIsLongTerm()).build();
        if (!tfIdcardInfoService.saveOrUpdate(legalIdcardInfoEntity)) {
            log.error("IncomingBizServiceImpl--saveLegal，保存法人身份信息失败:{}", JSONObject.toJSONString(legalIdcardInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        return legalIdcardInfoEntity;
    }

    /**
     * 保存经办人身份信息
     * @param tfIncomingImportEntity
     * @return
     */
    private TfIdcardInfoEntity saveAgent(TfIncomingImportEntity tfIncomingImportEntity) {
        TfIdcardInfoEntity agentIdcardInfoEntity = TfIdcardInfoEntity.builder().idType(IdTypeEnum.ID_CARD.getCode()).
                idNo(tfIncomingImportEntity.getAgentIdNo()).name(tfIncomingImportEntity.getLegalName()).
                idEffectiveDate(tfIncomingImportEntity.getLegalIdEffectiveDate()).
                idExpiryDate(tfIncomingImportEntity.getLegalIdExpiryDate()).
                isLongTerm(tfIncomingImportEntity.getLegalIdIsLongTerm()).build();
        if (!tfIdcardInfoService.save(agentIdcardInfoEntity)) {
            log.error("IncomingBizServiceImpl--saveAgent，保存经办人身份信息失败:{}", JSONObject.toJSONString(agentIdcardInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        return agentIdcardInfoEntity;
    }

    /**
     * 保存营业信息
     * @param tfIncomingImportEntity
     * @param incomingId
     */
    private void saveBusinessInfo(TfIncomingImportEntity tfIncomingImportEntity, Long incomingId) {
        TfBusinessLicenseInfoEntity tfBusinessLicenseInfoEntity = new TfBusinessLicenseInfoEntity();
        BeanUtils.copyProperties(tfIncomingImportEntity, tfBusinessLicenseInfoEntity);
        //保存营业执照信息表
        if (!tfBusinessLicenseInfoService.save(tfBusinessLicenseInfoEntity)) {
            log.error("IncomingBizServiceImpl--saveBusinessInfo，保存营业执照信息失败:{}", JSONObject.toJSONString(tfBusinessLicenseInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        TfIncomingBusinessInfoEntity tfIncomingBusinessInfoEntity = TfIncomingBusinessInfoEntity.builder().
                incomingId(incomingId).
                businessLicenseId(tfBusinessLicenseInfoEntity.getId()).
                build();
        //保存营业信息表
        if (!tfIncomingBusinessInfoService.save(tfIncomingBusinessInfoEntity)) {
            log.error("IncomingBizServiceImpl--saveBusinessInfo，保存营业信息失败:{}", JSONObject.toJSONString(tfIncomingBusinessInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }

    /**
     * 保存结算信息
     * @param tfIncomingImportEntity
     * @param incomingId
     */
    private void saveSettleInfo(TfIncomingImportEntity tfIncomingImportEntity, Long incomingId) {
        TfBankCardInfoEntity tfBankCardInfoEntity = new TfBankCardInfoEntity();
        BeanUtils.copyProperties(tfIncomingImportEntity, tfBankCardInfoEntity);
        //保存银行卡表信息
        if (!tfBankCardInfoService.save(tfBankCardInfoEntity)) {
            log.error("IncomingBizServiceImpl--saveSettleInfo，保存结算银行卡信息失败:{}", JSONObject.toJSONString(tfBankCardInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        TfIncomingSettleInfoEntity tfIncomingSettleInfoEntity = TfIncomingSettleInfoEntity.builder().
                incomingId(incomingId).
                settlementAccountType(tfIncomingImportEntity.getSettleAccountType()).
                bankCardId(tfBankCardInfoEntity.getId()).
                build();
        //保存结算表信息
        if (!tfIncomingSettleInfoService.save(tfIncomingSettleInfoEntity)) {
            log.error("IncomingBizServiceImpl--saveSettleInfo，保存结算信息失败:{}", JSONObject.toJSONString(tfIncomingSettleInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }
}
