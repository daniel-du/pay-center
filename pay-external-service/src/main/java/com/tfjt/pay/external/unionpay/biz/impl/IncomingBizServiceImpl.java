package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.tfjt.api.TfSupplierApiService;
import com.tfjt.constant.MessageStatusEnum;
import com.tfjt.dto.TfSupplierDTO;
import com.tfjt.entity.AsyncMessageEntity;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingStatusRespDTO;
import com.tfjt.pay.external.unionpay.biz.IncomingBizService;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.RedisConstant;
import com.tfjt.pay.external.unionpay.constants.RetryMessageConstant;
import com.tfjt.pay.external.unionpay.dto.CheckCodeMessageDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingDataIdDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.dto.message.IncomingFinishDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingChangeAccessMainTypeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingCheckCodeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingInfoReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSubmitMessageReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingSubmitMessageRespDTO;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.enums.*;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.strategy.incoming.AbstractIncomingService;
import com.tfjt.pay.external.unionpay.utils.NetworkTypeCacheUtil;
import com.tfjt.producter.ProducerMessageApi;
import com.tfjt.producter.service.AsyncMessageService;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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
    private ITfIncomingImportService tfIncomingImportService;

    @Autowired
    private IdentifierGenerator identifierGenerator;

    @Autowired
    private NetworkTypeCacheUtil networkTypeCacheUtil;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IncomingBizServiceImpl incomingBizService;

    @Autowired
    private SelfSignService selfSignService;

    @DubboReference
    private TfSupplierApiService tfSupplierApiService;

    @Value("${rocketmq.topic.incomingFinish}")
    private String incomingFinishTopic;

    @Value("${tf-pay.appId}")
    private String appId;

    @Value("${tf-pay.appSecret}")
    private String appSecret;

    private static final String MQ_FROM_SERVER = "tf-cloud-pay-center";

    private static final String MQ_TO_SERVER = "tf-cloud-shop";

    private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd");

    @Override
    public Result incomingSave(IncomingInfoReqDTO incomingInfoReqDTO) {
        try {
            log.info("IncomingBizServiceImpl---incomingSave, incomingInfoReqDTO:{}", JSONObject.toJSONString(incomingInfoReqDTO));
            ValidatorUtils.validateEntity(incomingInfoReqDTO, AddGroup.class);
            Long incomingCount = tfIncomingInfoService.queryIncomingInfoCountByMerchant(incomingInfoReqDTO.getBusinessId(),
                    incomingInfoReqDTO.getBusinessType(), incomingInfoReqDTO.getAccessChannelType());
            if (incomingCount > 0) {
                throw new TfException(ExceptionCodeEnum.INCOMING_DATA_ALREADY_EXIST);
            }
            //保存进件主表信息
            TfIncomingInfoEntity tfIncomingInfoEntity = new TfIncomingInfoEntity();
            BeanUtils.copyProperties(incomingInfoReqDTO, tfIncomingInfoEntity);
            String memberId = generateMemberId(incomingInfoReqDTO.getBusinessType().intValue());
            tfIncomingInfoEntity.setMemberId(memberId);
            tfIncomingInfoEntity.setAccessStatus(IncomingAccessStatusEnum.MESSAGE_FILL_IN.getCode());
            if (!tfIncomingInfoService.save(tfIncomingInfoEntity)) {
                log.error("IncomingBizServiceImpl---incomingSave, 保存进件主表信息失败:{}", JSONObject.toJSONString(tfIncomingInfoEntity));
                throw new TfException(ExceptionCodeEnum.FAIL);
            }
            return Result.ok();
        } catch (TfException e) {
            log.error("平安进件-保存进件主表信息 发生 TfException:", e);
            throw e;
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
    public Result<IncomingSubmitMessageRespDTO> incomingSubmit(IncomingSubmitMessageReqDTO incomingSubmitMessageReqDTO) {
        log.info("IncomingBizServiceImpl--incomingSubmit, incomingSubmitMessageReqDTO:{}", JSONObject.toJSONString(incomingSubmitMessageReqDTO));
        String codeValidityKey = RedisConstant.INCOMING_BINK_CARD_KEY_PREFIX + incomingSubmitMessageReqDTO.getIncomingId();
        //获取缓存，如果两分钟内存在操作记录，直接返回错误提示
        if (redisCache.getCacheString(codeValidityKey) != null) {
            return Result.failed(ExceptionCodeEnum.INCOMING_FREQUENT_OPERATION);
        }
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
        IncomingSubmitMessageRespDTO respDTO = abstractIncomingService.incomingSubmit(incomingSubmitMessageDTO);
        return Result.ok(respDTO);
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
        IncomingMessageRespDTO incomingMessageRespDTO;
        String cacheKey = RedisConstant.INCOMING_MSG_KEY_PREFIX + incomingMessageReqDTO.getAccessChannelType() + ":" +
                incomingMessageReqDTO.getBusinessType() + ":" + incomingMessageReqDTO.getBusinessId();
        //获取缓存
        String incomingMsgStr = redisCache.getCacheString(cacheKey);
        log.info("IncomingBizServiceImpl--queryIncomingMessage, incomingMsgStr:{}", incomingMsgStr);
        if (StringUtils.isNotBlank(incomingMsgStr)) {
            incomingMessageRespDTO = JSONObject.parseObject(incomingMsgStr, IncomingMessageRespDTO.class);
            return Result.ok(incomingMessageRespDTO);
        }
        incomingMessageRespDTO = tfIncomingInfoService.queryIncomingMessageByMerchant(incomingMessageReqDTO);
        log.info("IncomingBizServiceImpl--queryIncomingMessage, incomingMessageRespDTO:{}", JSONObject.toJSONString(incomingMessageRespDTO));
        if (ObjectUtils.isEmpty(incomingMessageRespDTO)) {
            return Result.ok();
        }
        //如果结算类型为对公，会员名称返回“营业名称”，否则返回“法人姓名”
        if (IncomingSettleTypeEnum.CORPORATE.getCode().equals(incomingMessageRespDTO.getSettlementAccountType())) {
            incomingMessageRespDTO.setMemberName(incomingMessageRespDTO.getBusinessName());
        } else {
            incomingMessageRespDTO.setMemberName(incomingMessageRespDTO.getLegalName());
        }
        //设置缓存，10分钟
        redisCache.setCacheString(cacheKey, JSONObject.toJSONString(incomingMessageRespDTO), NumberConstant.TEN, TimeUnit.MINUTES);
        return Result.ok(incomingMessageRespDTO);
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
            log.error("IncomingBizServiceImpl--changeAccessMainType, isError incomingId:{}", changeAccessMainTypeReqDTO.getId());
            return Result.failed(ExceptionCodeEnum.FAIL);
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
        return Result.ok();
    }

    /**
     * 银联老数据批量入网平安
     * @return
     */
    @Override
    public Result bacthIncoming() {
        log.info("IncomingBizServiceImpl--bacthIncoming, start");
        TfIncomingInfoEntity tfIncomingInfostart = tfIncomingInfoService.queryNotSubmitMinIdData();
        log.info("IncomingBizServiceImpl--bacthIncoming, tfIncomingInfoStart:{}", JSONObject.toJSONString(tfIncomingInfostart));
        if (ObjectUtils.isEmpty(tfIncomingInfostart)) {
            log.warn("IncomingBizServiceImpl--bacthIncoming, tfIncomingImportStart isEmpty");
            return Result.ok();
        }
        boolean extractFlag = true;
        long startId = tfIncomingInfostart.getId();
        while(extractFlag) {
            List<TfIncomingInfoEntity> incomingList = tfIncomingInfoService.queryListByStartId(startId);
            incomingList.forEach(incomingEntity -> {
                try {
                    incomingBizService.incomingMessageSubmit(incomingEntity);
                } catch (Exception e) {

                }

            });

            startId = incomingList.get(incomingList.size() - 1).getId();
            if (incomingList.size() < 100) {
                extractFlag = false;
            }
        }
        log.info("IncomingBizServiceImpl--bacthIncoming, end");
        return Result.ok();
    }

    /**
     * 根据多个商户信息批量查询入网状态（一个渠道入网成功即算入网成功），key为“商户类型”-“商户id”
     * @param incomingStatusReqDTO
     * @return
     */
    @Override
    public Result<Map<String, IncomingStatusRespDTO>> queryIncomingStatus(IncomingStatusReqDTO incomingStatusReqDTO) {
        ValidatorUtils.validateEntity(incomingStatusReqDTO);
        List<Integer> ids = new ArrayList<>();
        incomingStatusReqDTO.getBusinessIds().forEach(id -> {
            ids.add(id.intValue());
        });
        List<String> accessAccts = new ArrayList<>();
        Map<String, IncomingStatusRespDTO> incomingStatusMap = new HashMap<>();
        if (IncomingMemberBusinessTypeEnum.YUNSHANG.getCode().equals(incomingStatusReqDTO.getBusinessType())) {
            List<TfSupplierDTO> tfSuppliers = tfSupplierApiService.getTfSupplierList(ids);
            if (CollectionUtils.isEmpty(tfSuppliers)) {
                return Result.failed(ExceptionCodeEnum.IS_NULL);
            }
            List<SelfSignEntity> selfSignEntities = selfSignService.querySelfSignsByAccessAccts(accessAccts);
            List<TfIncomingInfoEntity> incomingInfoEntities = tfIncomingInfoService.queryListByBusinessIdAndType(incomingStatusReqDTO.getBusinessIds(), incomingStatusReqDTO.getBusinessType());
            Map<String, SelfSignEntity> selfMap = selfSignEntities.stream().collect(Collectors.toMap(SelfSignEntity::getAccesserAcct, Function.identity()));
            Map<Long, TfIncomingInfoEntity> incomingMap = incomingInfoEntities.stream().collect(Collectors.toMap(TfIncomingInfoEntity::getBusinessId, Function.identity()));
            tfSuppliers.forEach(tfSupplier -> {
                IncomingStatusRespDTO incomingStatus = new IncomingStatusRespDTO();
                incomingStatus.setBusinessType(incomingStatusReqDTO.getBusinessType());
                incomingStatus.setBusinessId(tfSupplier.getId().longValue());
                SelfSignEntity selfSignEntity = selfMap.get(tfSupplier.getSupplierId());
                TfIncomingInfoEntity tfIncomingInfoEntity = incomingMap.get(tfSupplier.getId());
                incomingStatusMap.put(incomingStatusReqDTO.getBusinessType() + "-" + tfSupplier.getId(), incomingStatus);
                if (ObjectUtils.isNotEmpty(selfSignEntity)) {
                    incomingStatus.setIncomingStatus(selfSignEntity.getSigningStatus());
                }
                if (PabcUnionNetworkStatusMappingEnum.NETWORK_SUCCESS.getUnionSigningStatus().equals(incomingStatus.getIncomingStatus())) {
                    return;
                }
                if (ObjectUtils.isNotEmpty(tfIncomingInfoEntity)) {
                    incomingStatus.setIncomingStatus(PabcUnionNetworkStatusMappingEnum.getMsg(tfIncomingInfoEntity.getAccessStatus()));
                }
            });
        } else {

        }
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
    public void MQProcess(IncomingSubmitMessageDTO incomingMessage){
        log.info("IncomingBizServiceImpl--MQProcess, start incomingMessage:{}", JSONObject.toJSONString(incomingMessage));
        IncomingFinishDTO incomingFinishDTO = IncomingFinishDTO.builder()
                .id(incomingMessage.getId())
                .accessChannelType(incomingMessage.getAccessChannelType())
                .accessMainType(incomingMessage.getAccessMainType())
                .accountNo(incomingMessage.getAccountNo())
                .businessType(incomingMessage.getBusinessType())
                .businessId(incomingMessage.getBusinessId())
                .build();
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
        return networkTypeCacheUtil.getNetworkTypeCacheList(areaCode);
    }

    /**
     * 写入进件信息
     */
    @Transactional(rollbackFor = {TfException.class, Exception.class})
    public void incomingMessageWrite(TfIncomingImportEntity tfIncomingImportEntity) {
        Long importId = tfIncomingImportEntity.getId();
        tfIncomingImportEntity.setId(null);
        TfIncomingInfoEntity incomingInfoEntity = new TfIncomingInfoEntity();
        BeanUtils.copyProperties(tfIncomingImportEntity, incomingInfoEntity);
        incomingInfoEntity.setAccessStatus(IncomingAccessStatusEnum.IMPORTS_CLOSURE.getCode());
        String memberId = generateMemberId(tfIncomingImportEntity.getBusinessType().intValue());
        incomingInfoEntity.setId(null);
        incomingInfoEntity.setMemberId(memberId);
        incomingInfoEntity.setSignChannel(NumberConstant.ONE.byteValue());
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

        //更新提交状态
        tfIncomingImportEntity.setId(importId);
        tfIncomingImportEntity.setSubmitStatus(NumberConstant.ONE.byteValue());
        if (!tfIncomingImportService.updateById(tfIncomingImportEntity)) {
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
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
        tfBusinessLicenseInfoEntity.setBusinessLicenseType(IdTypeEnum.SOCIAL_CREDIT_CODE.getCode());
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

    @Transactional(rollbackFor = {TfException.class, Exception.class})
    public void incomingMessageSubmit(TfIncomingInfoEntity tfIncomingInfoEntity) {
        try {
            //查询提交进件申请所需信息
            IncomingSubmitMessageDTO incomingSubmitMessageDTO =
                    tfIncomingInfoService.queryIncomingMessage(tfIncomingInfoEntity.getId());
            //根据参数类型获取实现类
            String bindServiceName = getServiceName(incomingSubmitMessageDTO);
            AbstractIncomingService abstractIncomingService = abstractIncomingServiceMap.get(bindServiceName);
            //实现类为空时，直接返回
            if (ObjectUtils.isEmpty(abstractIncomingService)) {
                log.error("IncomingBizServiceImpl--incomingMessageSubmit, abstractIncomingService isEmpty, bindServiceName:{}", bindServiceName);
                throw new TfException(ExceptionCodeEnum.INCOMING_STRATEGY_SERVICE_IS_NULL);
            }
            abstractIncomingService.openAccount(incomingSubmitMessageDTO);
        } catch (Exception e) {
            log.error("IncomingBizServiceImpl--incomingMessageSubmit，银联数据开户失败, incomingId:{}", tfIncomingInfoEntity.getId());
            throw e;
        }
    }

    private String generateMemberId(Integer businessType) {
        String prefix = IncomingMemberBusinessTypeEnum.fromCode(businessType).getMemberPrefix() + FORMAT.format(new Date());
        Long incr = redisCache.incr(prefix);
        redisCache.expire(prefix, 24, TimeUnit.HOURS);
        String str = String.format("%05d", incr);
        return prefix + str;
    }


}
