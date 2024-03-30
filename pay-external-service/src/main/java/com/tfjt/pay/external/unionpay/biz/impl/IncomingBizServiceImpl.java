package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.tfjt.api.TfSupplierApiService;
import com.tfjt.constant.MessageStatusEnum;
import com.tfjt.dto.TfSupplierDTO;
import com.tfjt.entity.AsyncMessageEntity;
import com.tfjt.pay.external.unionpay.api.dto.req.AllIncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.TtqfContractReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.TtqfSignReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.*;
import com.tfjt.pay.external.unionpay.biz.IncomingBizService;
import com.tfjt.pay.external.unionpay.constants.*;
import com.tfjt.pay.external.unionpay.dto.CheckCodeMessageDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingDataIdDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.dto.message.IncomingFinishDTO;
import com.tfjt.pay.external.unionpay.dto.req.*;
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
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;
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

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private IncomingCacheService incomingCacheService;

    @DubboReference(retries = 0, timeout = 2000, check = false)
    private TfSupplierApiService tfSupplierApiService;

    @Value("${rocketmq.topic.incomingFinish}")
    private String incomingFinishTopic;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String MQ_FROM_SERVER = "tf-cloud-pay-center";

    private static final String MQ_TO_SERVER = "tf-cloud-shop";

    private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd");

    /**
     * 18位身份证号验证格式
     */
    private final Pattern ID_NEW_REGEXP = Pattern.compile(RegularConstants.ID_CARD_NEW);
    /**
     * 15位身份证号验证格式
     */
    private final Pattern ID_OLD_REGEXP = Pattern.compile(RegularConstants.ID_CARD_OLD);

    private static final Set<Integer> PN_OPEN_ACCOUNT_STATUS_SET = new HashSet<>();

    static {
        PN_OPEN_ACCOUNT_STATUS_SET.add(IncomingAccessStatusEnum.SIGN_SUCCESS.getCode());
        PN_OPEN_ACCOUNT_STATUS_SET.add(IncomingAccessStatusEnum.BINK_CARD_SUCCESS.getCode());
        PN_OPEN_ACCOUNT_STATUS_SET.add(IncomingAccessStatusEnum.ACCESS_SUCCESS.getCode());
        PN_OPEN_ACCOUNT_STATUS_SET.add(IncomingAccessStatusEnum.SMS_VERIFICATION_SUCCESS.getCode());
    }

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
        //写入缓存
        writeIncomingCache(incomingSubmitMessageReqDTO.getIncomingId());
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
        //写入缓存
        writeIncomingCache(inComingCheckCodeReqDTO.getIncomingId());
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
            if (PN_OPEN_ACCOUNT_STATUS_SET.contains(incomingMessageRespDTO.getAccessStatus())) {
                return Result.ok(incomingMessageRespDTO);
            }
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
        redisCache.setCacheString(cacheKey, JSONObject.toJSONString(incomingMessageRespDTO));
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
        Map<String, IncomingMessageRespDTO> incomingMessageMap = new HashMap<>();
        Set<String> incomingCacheKeys = new HashSet<>();
        Map<String, IncomingMessageReqDTO> noCacheMap = new HashMap<>();
        //遍历入参获取缓存key集合
        incomingMessageReqs.forEach(incomingMessageReqDTO -> {
            incomingCacheKeys.add(RedisConstant.INCOMING_MSG_KEY_PREFIX + incomingMessageReqDTO.getAccessChannelType() + ":" +
                    incomingMessageReqDTO.getBusinessType() + ":" + incomingMessageReqDTO.getBusinessId());
            noCacheMap.put(incomingMessageReqDTO.getAccessChannelType() + "-" +
                    incomingMessageReqDTO.getBusinessType() + "-" + incomingMessageReqDTO.getBusinessId(), incomingMessageReqDTO);
        });
        //批量查询缓存
        List<JSONObject> cacheJSONS =  redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String key : incomingCacheKeys) {
                connection.get(redisTemplate.getKeySerializer().serialize(key));
            }
            return null;
        });
        log.info("IncomingBizServiceImpl--queryIncomingMessages, cacheJSONS:{}", JSONObject.toJSONString(cacheJSONS));
        //遍历缓存中查询集合
        for (JSONObject cacheJSON : cacheJSONS) {
            if (ObjectUtils.isEmpty(cacheJSON)) {
                continue;
            }
            IncomingMessageRespDTO cacheResp = JSONObject.toJavaObject(cacheJSON, IncomingMessageRespDTO.class);
            String key = cacheResp.getAccessChannelType() + "-" + cacheResp.getBusinessType() +"-"+ cacheResp.getBusinessId();
            //移除数据不为空的key，最终剩余参数查询数据库
            noCacheMap.remove(key);
            if (PN_OPEN_ACCOUNT_STATUS_SET.contains(cacheResp.getAccessStatus())) {
                incomingMessageMap.put(key, cacheResp);
            }
        }
        //缓存中查到入参全部数据，返回结果
        if (CollectionUtils.isEmpty(noCacheMap)) {
            return Result.ok(incomingMessageMap);
        }
        List<IncomingMessageReqDTO> queryDBReqs = new ArrayList<>();
        //遍历缓存中未查询到的key集合，组合数据库查询参数
        for (Map.Entry<String, IncomingMessageReqDTO> entry : noCacheMap.entrySet()) {
            queryDBReqs.add(entry.getValue());
        }
        List<IncomingMessageRespDTO> incomingMessageRespDTOS = tfIncomingInfoService.queryIncomingMessagesByMerchantList(queryDBReqs);
        log.info("IncomingBizServiceImpl--queryIncomingMessages, incomingMessageRespDTOS:{}", JSONObject.toJSONString(incomingMessageRespDTOS));
        if (CollectionUtils.isEmpty(incomingMessageRespDTOS)) {
            return Result.ok(incomingMessageMap);
        }
        //将查询到的数据集合，以“入网渠道”-“商户类型”-“商户id”为key放入map
        incomingMessageRespDTOS.forEach(incomingMessage -> {
            String key = incomingMessage.getAccessChannelType() + "-" + incomingMessage.getBusinessType() +"-"+ incomingMessage.getBusinessId();
            incomingMessageMap.put(key, incomingMessage);
        });
        //异步写入进件缓存
        incomingCacheService.batchWriteIncomingCache(queryDBReqs);
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
        log.info("IncomingBizServiceImpl--queryIncomingStatus, incomingStatusReqDTO:{}",JSONObject.toJSONString(incomingStatusReqDTO));
        ValidatorUtils.validateEntity(incomingStatusReqDTO);
        Map<String, IncomingStatusRespDTO> incomingStatusMap = new HashMap<>();
        if (IncomingMemberBusinessTypeEnum.YUNSHANG.getCode().equals(incomingStatusReqDTO.getBusinessType())) {
            batchQuerySupplierIncomingStatus(incomingStatusReqDTO, incomingStatusMap);
        } else {
            batchQueryShopIncomingStatus(incomingStatusReqDTO, incomingStatusMap);
        }
        return Result.ok(incomingStatusMap);
    }

    /**
     * 根据商户id、商户类型查询所有渠道入网信息
     * @param reqDTO
     * @return
     */
    @Override
    public Result<List<AllIncomingMessageRespDTO>> queryAllIncomingMessage(AllIncomingMessageReqDTO reqDTO) {
        List<AllIncomingMessageRespDTO> messageRespList = new ArrayList<>();
        //查询incoming表入网信息
        List<TfIncomingInfoEntity> incomingInfoEntities = tfIncomingInfoService.queryListByBusinessIdAndType(reqDTO.getBusinessId(), reqDTO.getBusinessType());
        Map<Integer, AllIncomingMessageRespDTO> incomingMessageMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(incomingInfoEntities)) {
            incomingInfoEntities.forEach(incomingInfo -> {
                AllIncomingMessageRespDTO allIncomingMessageRespDTO = new AllIncomingMessageRespDTO();
                allIncomingMessageRespDTO.setChannelName(IncomingAccessChannelTypeEnum.getDescFromCode(incomingInfo.getAccessChannelType().intValue()));
                if (IncomingAccessStatusEnum.IMPORTS_CLOSURE.getCode().equals(incomingInfo.getAccessStatus())) {
                    allIncomingMessageRespDTO.setAccessStatusName(IncomingConstant.NO_ACCESS_STATUS_NAME);
                } else if (PN_OPEN_ACCOUNT_STATUS_SET.contains(incomingInfo.getAccessStatus())) {
                    allIncomingMessageRespDTO.setAccessStatusName(IncomingConstant.HAS_ACCESS_STATUS_NAME);
                } else {
                    allIncomingMessageRespDTO.setAccessStatusName(IncomingConstant.ACCESSING_STATUS_NAME);
                }
                allIncomingMessageRespDTO.setAccountNo(incomingInfo.getAccountNo());
                incomingMessageMap.put(incomingInfo.getAccessChannelType().intValue(), allIncomingMessageRespDTO);
            });
        }
        //查询self_signing表入网信息
        com.tfjt.tfcommon.utils.Result<TfSupplierDTO> result =  tfSupplierApiService.getSupplierInfoById(reqDTO.getBusinessId());
        if (ObjectUtils.isNotEmpty(result) && ObjectUtils.isNotEmpty(result.getData())) {
            TfSupplierDTO tfSupplier = result.getData();
            SelfSignEntity selfSignEntity = selfSignService.querySelfSignByAccessAcct(tfSupplier.getSupplierId());
            AllIncomingMessageRespDTO allIncomingMessageRespDTO = new AllIncomingMessageRespDTO();
            allIncomingMessageRespDTO.setChannelName(IncomingAccessChannelTypeEnum.UNIONPAY.getDesc());
            if (ObjectUtils.isEmpty(selfSignEntity)) {
                allIncomingMessageRespDTO.setAccessStatusName(IncomingConstant.NO_ACCESS_STATUS_NAME);
            } else {
                //返回银联枚举值
                allIncomingMessageRespDTO.setAccessStatusName(UnionPayStatusEnum.getDesc(selfSignEntity.getSigningStatus()));
                allIncomingMessageRespDTO.setAccountNo(selfSignEntity.getMid());
                allIncomingMessageRespDTO.setAccountBusinessNo(selfSignEntity.getBusinessNo());
            }
            incomingMessageMap.put(IncomingAccessChannelTypeEnum.UNIONPAY.getCode(), allIncomingMessageRespDTO);
        }
        for (IncomingAccessChannelTypeEnum accessChannelTypeEnum : IncomingAccessChannelTypeEnum.values()) {
            if (incomingMessageMap.containsKey(accessChannelTypeEnum.getCode())) {
                messageRespList.add(incomingMessageMap.get(accessChannelTypeEnum.getCode()));
            } else {
                AllIncomingMessageRespDTO allIncomingMessageRespDTO = new AllIncomingMessageRespDTO();
                allIncomingMessageRespDTO.setChannelName(accessChannelTypeEnum.getDesc());
                allIncomingMessageRespDTO.setAccessStatusName(IncomingConstant.NO_ACCESS_STATUS_NAME);
                messageRespList.add(allIncomingMessageRespDTO);
            }
        }
        return Result.ok(messageRespList);
    }

    /**
     * 天天企赋-商户签约
     * @param ttqfSignReqDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = {TfException.class, Exception.class})
    public Result<TtqfSignRespDTO> ttqfSign(TtqfSignReqDTO ttqfSignReqDTO) {
        log.info("IncomingBizServiceImpl>>ttqfSign, ttqfSignReqDTO:{}", JSONObject.toJSONString(ttqfSignReqDTO));
        ValidatorUtils.validateEntity(ttqfSignReqDTO);
        if (!ID_NEW_REGEXP.matcher(ttqfSignReqDTO.getIdCardNo()).matches() && !ID_OLD_REGEXP.matcher(ttqfSignReqDTO.getIdCardNo()).matches()) {
            throw new TfException(ExceptionCodeEnum.ID_CARD_NO_FORMAT_ERROR);
        }
        //判断该会员是否存在认证信息
        QueryTtqfSignMsgRespDTO signMsgRespDTO = tfIncomingInfoService.queryTtqfSignMsg(ttqfSignReqDTO.getBusinessId());
        if (ObjectUtils.isNotEmpty(signMsgRespDTO)) {
            return Result.failed(ExceptionCodeEnum.MERCHANT_IS_AUTH);
        }
        //判断该身份证号是否存在认证信息
        List<QueryTtqfSignMsgRespDTO> signMsgRespByIdCard = tfIncomingInfoService.queryTtqfSignMsgByIdCardNo(ttqfSignReqDTO.getIdCardNo());
        if (!CollectionUtils.isEmpty(signMsgRespByIdCard)) {
            return Result.failed(ExceptionCodeEnum.ID_CARD_NO_ALREADY_EXIST);
        }
        //保存进件主表信息
        TfIncomingInfoEntity tfIncomingInfoEntity = new TfIncomingInfoEntity();
        tfIncomingInfoEntity.setBusinessId(ttqfSignReqDTO.getBusinessId());
        String memberId = generateMemberId(ttqfSignReqDTO.getBusinessType().intValue());
        tfIncomingInfoEntity.setAccessChannelType(IncomingAccessChannelTypeEnum.TTQF.getCode().byteValue());
        tfIncomingInfoEntity.setAccessType(IncomingAccessTypeEnum.COMMON.getCode().byteValue());
        tfIncomingInfoEntity.setAccessMainType(IncomingAccessMainTypeEnum.SMALL.getCode().byteValue());
        tfIncomingInfoEntity.setBusinessType(ttqfSignReqDTO.getBusinessType().byteValue());
        tfIncomingInfoEntity.setMemberId(memberId);
        tfIncomingInfoEntity.setAccessStatus(IncomingAccessStatusEnum.MESSAGE_FILL_IN.getCode());
        if (!tfIncomingInfoService.save(tfIncomingInfoEntity)) {
            log.error("IncomingBizServiceImpl---ttqfSign, 保存进件主表信息失败:{}", JSONObject.toJSONString(tfIncomingInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        //保存身份信息
        saveTtqfMerchantInfo(ttqfSignReqDTO, tfIncomingInfoEntity);
        //保存结算信息
        saveTtqfSettleInfo(ttqfSignReqDTO, tfIncomingInfoEntity);
        //调用签约策略
        AbstractIncomingService abstractIncomingService = abstractIncomingServiceMap.get("ttqf_common_personal");
        IncomingSubmitMessageDTO incomingSubmitMessageDTO =  IncomingSubmitMessageDTO.builder()
                .incomingId(tfIncomingInfoEntity.getId())
                .legalName(ttqfSignReqDTO.getUserName())
                .legalMobile(ttqfSignReqDTO.getMobile())
                .legalIdNo(ttqfSignReqDTO.getIdCardNo())
                .bankCardNo(ttqfSignReqDTO.getBankCardNo())
                .legalIdExpiryStart(ttqfSignReqDTO.getExpiryStart())
                .legalIdExpiryEnd(ttqfSignReqDTO.getExpiryEnd())
                .legalIdFrontUrl(ttqfSignReqDTO.getIdCardPicAFileId())
                .legalIdBackUrl(ttqfSignReqDTO.getIdCardPicBFileId()).build();
        abstractIncomingService.incomingSubmit(incomingSubmitMessageDTO);
        return Result.ok();
    }

    /**
     * 天天企赋-手签H5唤起
     * @param ttqfContractReqDTO
     * @return
     */
    @Override
    public Result<TtqfContractRespDTO> ttqfContract(TtqfContractReqDTO ttqfContractReqDTO) {

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
        message.setMsgTag("*");
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
            //写入缓存
            writeIncomingCache(tfIncomingInfoEntity.getId());
        } catch (Exception e) {
            log.error("IncomingBizServiceImpl--incomingMessageSubmit，银联数据开户失败, incomingId:{}", tfIncomingInfoEntity.getId());
            throw e;
        }
    }

    /**
     * 批量查询供应商是入网状态
     * @param incomingStatusReqDTO
     * @param incomingStatusMap
     */
    private void batchQuerySupplierIncomingStatus(IncomingStatusReqDTO incomingStatusReqDTO, Map<String, IncomingStatusRespDTO> incomingStatusMap) {
        List<Integer> ids = new ArrayList<>();
        incomingStatusReqDTO.getBusinessIds().forEach(id -> {
            ids.add(id.intValue());
        });
        List<String> accessAccts = new ArrayList<>();
        //查询供应商信息，获取supplierId
        List<TfSupplierDTO> tfSuppliers = tfSupplierApiService.getTfSupplierList(ids);
        log.info("IncomingBizServiceImpl--queryIncomingStatus, tfSuppliers:{}",JSONObject.toJSONString(tfSuppliers));
        if (CollectionUtils.isEmpty(tfSuppliers)) {
            throw new TfException(ExceptionCodeEnum.IS_NULL);
        }
        tfSuppliers.forEach(tfSupplier -> {
            accessAccts.add(tfSupplier.getSupplierId());
        });
        log.info("IncomingBizServiceImpl--queryIncomingStatus, accessAccts:{}",accessAccts.toString());
        //查询银联入网数据
        List<SelfSignEntity> selfSignEntities = selfSignService.querySelfSignsByAccessAccts(accessAccts);
        log.info("IncomingBizServiceImpl--queryIncomingStatus, selfSignEntities:{}",JSONObject.toJSONString(selfSignEntities));
        //查询平安入网数据
        List<TfIncomingInfoEntity> incomingInfoEntities = tfIncomingInfoService.queryListByBusinessIdsAndType(incomingStatusReqDTO.getBusinessIds(), incomingStatusReqDTO.getBusinessType());
        log.info("IncomingBizServiceImpl--queryIncomingStatus, incomingInfoEntities:{}",JSONObject.toJSONString(incomingInfoEntities));
        Map<String, SelfSignEntity> selfMap = selfSignEntities.stream().collect(Collectors.toMap(SelfSignEntity::getAccesserAcct, Function.identity()));
        Map<Long, TfIncomingInfoEntity> incomingMap = incomingInfoEntities.stream().collect(Collectors.toMap(TfIncomingInfoEntity::getBusinessId, Function.identity()));
        tfSuppliers.forEach(tfSupplier -> {
            IncomingStatusRespDTO incomingStatus = new IncomingStatusRespDTO();
            incomingStatus.setBusinessType(incomingStatusReqDTO.getBusinessType());
            incomingStatus.setBusinessId(tfSupplier.getId().longValue());
            SelfSignEntity selfSignEntity = selfMap.get(tfSupplier.getSupplierId());
            TfIncomingInfoEntity tfIncomingInfoEntity = incomingMap.get(tfSupplier.getId().longValue());
            incomingStatusMap.put(incomingStatusReqDTO.getBusinessType() + "-" + tfSupplier.getId(), incomingStatus);
            //设置银联入网状态
            if (ObjectUtils.isNotEmpty(selfSignEntity)) {
                incomingStatus.setIncomingStatus(selfSignEntity.getSigningStatus());
            }
            //如果银联状态为“入网完成”，则跳出循环
            if (PabcUnionNetworkStatusMappingEnum.NETWORK_SUCCESS.getUnionSigningStatus().equals(incomingStatus.getIncomingStatus())) {
                return;
            }
            //设置平安入网状态
            if (ObjectUtils.isNotEmpty(tfIncomingInfoEntity)) {
                incomingStatus.setIncomingStatus(PabcUnionNetworkStatusMappingEnum.getMsg(tfIncomingInfoEntity.getAccessStatus()));
            }
        });
    }

    /**
     * 批量查询云店入网状态
     * @param incomingStatusReqDTO
     * @param incomingStatusMap
     */
    private void batchQueryShopIncomingStatus(IncomingStatusReqDTO incomingStatusReqDTO, Map<String, IncomingStatusRespDTO> incomingStatusMap) {
        List<String> accessAccts = new ArrayList<>();
        incomingStatusReqDTO.getBusinessIds().forEach(id -> {
            accessAccts.add(id.toString());
        });
        //查询银联入网数据
        List<SelfSignEntity> selfSignEntities = selfSignService.querySelfSignsByAccessAccts(accessAccts);
        log.info("IncomingBizServiceImpl--queryIncomingStatus, selfSignEntities:{}",JSONObject.toJSONString(selfSignEntities));
        //查询平安入网数据
        List<TfIncomingInfoEntity> incomingInfoEntities = tfIncomingInfoService.queryListByBusinessIdsAndType(incomingStatusReqDTO.getBusinessIds(), incomingStatusReqDTO.getBusinessType());
        log.info("IncomingBizServiceImpl--queryIncomingStatus, incomingInfoEntities:{}",JSONObject.toJSONString(incomingInfoEntities));
        Map<String, SelfSignEntity> selfMap = selfSignEntities.stream().collect(Collectors.toMap(SelfSignEntity::getAccesserAcct, Function.identity()));
        Map<Long, TfIncomingInfoEntity> incomingMap = incomingInfoEntities.stream().collect(Collectors.toMap(TfIncomingInfoEntity::getBusinessId, Function.identity()));
        incomingStatusReqDTO.getBusinessIds().forEach(id -> {
            IncomingStatusRespDTO incomingStatus = new IncomingStatusRespDTO();
            incomingStatus.setBusinessType(incomingStatusReqDTO.getBusinessType());
            incomingStatus.setBusinessId(id);
            SelfSignEntity selfSignEntity = selfMap.get(id);
            TfIncomingInfoEntity tfIncomingInfoEntity = incomingMap.get(id);
            incomingStatusMap.put(incomingStatusReqDTO.getBusinessType() + "-" + id, incomingStatus);
            //设置银联入网状态
            if (ObjectUtils.isNotEmpty(selfSignEntity)) {
                incomingStatus.setIncomingStatus(selfSignEntity.getSigningStatus());
            }
            //如果银联状态为“入网完成”，则跳出循环
            if (PabcUnionNetworkStatusMappingEnum.NETWORK_SUCCESS.getUnionSigningStatus().equals(incomingStatus.getIncomingStatus())) {
                return;
            }
            //设置平安入网状态
            if (ObjectUtils.isNotEmpty(tfIncomingInfoEntity)) {
                incomingStatus.setIncomingStatus(PabcUnionNetworkStatusMappingEnum.getMsg(tfIncomingInfoEntity.getAccessStatus()));
            }
        });
    }

    private String generateMemberId(Integer businessType) {
        String prefix = IncomingMemberBusinessTypeEnum.fromCode(businessType).getMemberPrefix() + FORMAT.format(new Date());
        Long incr = redisCache.incr(prefix);
        redisCache.expire(prefix, 24, TimeUnit.HOURS);
        String str = String.format("%05d", incr);
        return prefix + str;
    }

    /**
     * 写入缓存
     * @param incomingId
     */
    private void writeIncomingCache(Long incomingId) {
        IncomingMessageRespDTO incomingMessage = tfIncomingInfoService.queryIncomingMessageRespById(incomingId);
        log.info("IncomingBizServiceImpl--writeIncomingCache, incomingMessage:{}", JSONObject.toJSONString(incomingMessage));
        if (ObjectUtils.isEmpty(incomingMessage)) {
            return;
        }
        String key = RedisConstant.INCOMING_MSG_KEY_PREFIX +  incomingMessage.getAccessChannelType() + ":"
                + incomingMessage.getBusinessType() + ":" + incomingMessage.getBusinessId();
        log.info("IncomingBizServiceImpl--writeIncomingCache, key:{}", key);
        redisCache.setCacheString(key, JSONObject.toJSONString(incomingMessage));
    }

    /**
     * 天天企赋签约-保存身份信息
     */
    private void saveTtqfMerchantInfo(TtqfSignReqDTO ttqfSignReqDTO, TfIncomingInfoEntity tfIncomingInfoEntity) {
        TfIdcardInfoEntity legalIdcardInfoEntity = TfIdcardInfoEntity.builder().
                idType(IdTypeEnum.ID_CARD.getCode()).
                idNo(ttqfSignReqDTO.getIdCardNo()).name(ttqfSignReqDTO.getUserName()).
                frontIdCardUrl(ttqfSignReqDTO.getIdCardPicAFileId()).
                backIdCardUrl(ttqfSignReqDTO.getIdCardPicBFileId()).
                idEffectiveDate(ttqfSignReqDTO.getExpiryStart()).
                idExpiryDate(ttqfSignReqDTO.getExpiryEnd()).build();
        if (!tfIdcardInfoService.saveOrUpdate(legalIdcardInfoEntity)) {
            log.error("IncomingBizServiceImpl--saveTtqfMerchantInfo，保存法人身份信息失败:{}", JSONObject.toJSONString(legalIdcardInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        TfIncomingMerchantInfoEntity tfIncomingMerchantInfoEntity = new TfIncomingMerchantInfoEntity();
        tfIncomingMerchantInfoEntity.setIncomingId(tfIncomingInfoEntity.getId());
        tfIncomingMerchantInfoEntity.setLegalMobile(ttqfSignReqDTO.getMobile());
        tfIncomingMerchantInfoEntity.setLegalIdCard(legalIdcardInfoEntity.getId());
        if (!tfIncomingMerchantInfoService.save(tfIncomingMerchantInfoEntity)) {
            log.error("IncomingBizServiceImpl--saveTtqfMerchantInfo，保存商户身份信息失败:{}", JSONObject.toJSONString(tfIncomingMerchantInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }

    /**
     * 天天企赋签约-保存结算信息
     */
    private void saveTtqfSettleInfo(TtqfSignReqDTO ttqfSignReqDTO, TfIncomingInfoEntity tfIncomingInfoEntity) {
        TfBankCardInfoEntity tfBankCardInfoEntity = new TfBankCardInfoEntity();
        tfBankCardInfoEntity.setBankCardNo(ttqfSignReqDTO.getBankCardNo());
        //保存银行卡表信息
        if (!tfBankCardInfoService.save(tfBankCardInfoEntity)) {
            log.error("IncomingBizServiceImpl--saveTtqfSettleInfo，保存结算银行卡信息失败:{}", JSONObject.toJSONString(tfBankCardInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        TfIncomingSettleInfoEntity tfIncomingSettleInfoEntity = TfIncomingSettleInfoEntity.builder().
                incomingId(tfIncomingInfoEntity.getId()).
                settlementAccountType(IncomingSettleTypeEnum.PERSONAL.getCode().byteValue()).
                bankCardId(tfBankCardInfoEntity.getId()).
                build();
        //保存结算表信息
        if (!tfIncomingSettleInfoService.save(tfIncomingSettleInfoEntity)) {
            log.error("IncomingBizServiceImpl--saveTtqfSettleInfo，保存结算信息失败:{}", JSONObject.toJSONString(tfIncomingSettleInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }


}
