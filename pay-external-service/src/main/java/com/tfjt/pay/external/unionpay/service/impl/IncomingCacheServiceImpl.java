package com.tfjt.pay.external.unionpay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tfjt.api.TfSupplierApiService;
import com.tfjt.dto.TfSupplierDTO;
import com.tfjt.pay.external.query.api.dto.req.QueryIncomingStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.config.DevConfig;
import com.tfjt.pay.external.unionpay.constants.RedisConstant;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.entity.SelfSignEntity;
import com.tfjt.pay.external.unionpay.enums.ExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessChannelTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingMemberBusinessTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingSettleTypeEnum;
import com.tfjt.pay.external.unionpay.service.IncomingCacheService;
import com.tfjt.pay.external.unionpay.service.SelfSignService;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.tfjt.supply.merchant.common.dto.supplierdto.resp.SupplierBasicInfoRespDTO;
import com.tfjt.supply.merchant.supplier.api.service.ISupplierInfoApiService;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/25 9:58
 * @description
 */
@Slf4j
@Service
public class IncomingCacheServiceImpl implements IncomingCacheService {

    @DubboReference(retries = 0, timeout = 2000, check = false)
    private TfSupplierApiService tfSupplierApi;

    @DubboReference(retries = 0, timeout = 2000, check = false)
    private ISupplierInfoApiService supplierInfoApiService;

    @Autowired
    private TfIncomingInfoService tfIncomingInfoService;

    @Autowired
    private SelfSignService selfSignService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private DevConfig devConfig;

    @Value("${unionPay.appId.yunshang}")
    private String yunshangAppId;

    @Value("${unionPay.appId.yundian}")
    private String yundianAppId;

    /**
     * 写入进件缓存
     * @param incomingSubmitMessageDTO
     */
    @Async
    @Override
    public void writeIncomingCache(IncomingSubmitMessageDTO incomingSubmitMessageDTO) {
        IncomingMessageReqDTO incomingMessageReqDTO = new IncomingMessageReqDTO();
        incomingMessageReqDTO.setBusinessId(incomingSubmitMessageDTO.getBusinessId());
        incomingMessageReqDTO.setBusinessType(incomingSubmitMessageDTO.getBusinessType());
        incomingMessageReqDTO.setAccessChannelType(incomingSubmitMessageDTO.getAccessChannelType());
        IncomingMessageRespDTO incomingMessageRespDTO = tfIncomingInfoService.queryIncomingMessageByMerchant(incomingMessageReqDTO);
        //如果结算类型为对公，会员名称返回“营业名称”，否则返回“法人姓名”
        if (IncomingSettleTypeEnum.CORPORATE.getCode().equals(incomingMessageRespDTO.getSettlementAccountType())) {
            incomingMessageRespDTO.setMemberName(incomingMessageRespDTO.getBusinessName());
        } else {
            incomingMessageRespDTO.setMemberName(incomingMessageRespDTO.getLegalName());
        }
        String cacheKey = RedisConstant.INCOMING_MSG_KEY_PREFIX + incomingMessageReqDTO.getAccessChannelType() + ":" +
                incomingMessageReqDTO.getBusinessType() + ":" + incomingMessageReqDTO.getBusinessId();
        //设置缓存
        redisCache.setCacheString(cacheKey, JSONObject.toJSONString(incomingMessageRespDTO));
    }

    /**
     * 批量写入进件缓存
     * @param accessChannel
     * @param incomingReqMap
     */
    @Async
    @Override
    public void batchWriteIncomingCache(Integer accessChannel, Map<String, QueryIncomingStatusReqDTO> incomingReqMap) {
        log.info("IncomingCacheServiceImpl--batchWriteIncomingCache, incomingReqMap:{}", JSONObject.toJSONString(incomingReqMap));
        if (ObjectUtils.isEmpty(accessChannel) || CollectionUtils.isEmpty(incomingReqMap)) {
            return;
        }
        Map<String, IncomingMessageRespDTO> incomingMessageMap;
        if (IncomingAccessChannelTypeEnum.PINGAN.getCode().equals(accessChannel)) {
            incomingMessageMap = queryPnIncomingMessageMap(incomingReqMap);
        } else {
            incomingMessageMap = queryUnIncomingMessageMap(incomingReqMap);
        }
        log.info("IncomingCacheServiceImpl--batchWriteIncomingCache, incomingMessageMap:{}", JSONObject.toJSONString(incomingMessageMap));
        Map<String, String> incomingMessageStrMap = new HashMap<>();
        for (Map.Entry<String, IncomingMessageRespDTO> entry : incomingMessageMap.entrySet()) {
            incomingMessageStrMap.put(entry.getKey(), JSONObject.toJSONString(entry.getValue()));
            redisCache.setCacheString(entry.getKey(), JSONObject.toJSONString(entry.getValue()));
        }
        log.info("IncomingCacheServiceImpl--batchWriteIncomingCache, incomingMessageStrMap:{}", JSONObject.toJSONString(incomingMessageStrMap));
//        redisTemplate.opsForValue().multiSet(incomingMessageStrMap);
    }

    /**
     * 批量写入进件缓存
     * @param queryDBReqs
     */
    @Override
    public void batchWriteIncomingCache(List<IncomingMessageReqDTO> queryDBReqs) {
        Map<String, IncomingMessageRespDTO> incomingMessageMap = new HashMap<>();
        //查询数据库
        List<IncomingMessageRespDTO> incomingMessages = tfIncomingInfoService.queryIncomingMessagesByMerchantList(queryDBReqs);
        queryDBReqs.forEach(queryDBReq -> {
            String key = RedisConstant.INCOMING_MSG_KEY_PREFIX +  queryDBReq.getAccessChannelType() + ":"
                    + queryDBReq.getBusinessType() + ":" + queryDBReq.getBusinessId();
            //往返回map中放入默认状态“未入网”
            IncomingMessageRespDTO incomingMessageResp = new IncomingMessageRespDTO();
            incomingMessageResp.setBusinessId(queryDBReq.getBusinessId());
            incomingMessageResp.setBusinessType(queryDBReq.getBusinessType());
            incomingMessageResp.setAccessChannelType(queryDBReq.getAccessChannelType());
            incomingMessageResp.setAccessStatus(0);
            incomingMessageMap.put(key, incomingMessageResp);
        });
        //数据库中查询结果为空，直接将未入网状态写入缓存
        if (CollectionUtils.isEmpty(incomingMessages)) {
            for (Map.Entry<String, IncomingMessageRespDTO> entry : incomingMessageMap.entrySet()) {
                redisCache.setCacheString(entry.getKey(), JSONObject.toJSONString(entry.getValue()));
            }
            return;
        }
        incomingMessages.forEach(incomingMessage -> {
            String key = RedisConstant.INCOMING_MSG_KEY_PREFIX +  incomingMessage.getAccessChannelType() + ":"
                    + incomingMessage.getBusinessType() + ":" + incomingMessage.getBusinessId();
            incomingMessageMap.put(key, incomingMessage);
        });
        for (Map.Entry<String, IncomingMessageRespDTO> entry : incomingMessageMap.entrySet()) {
            redisCache.setCacheString(entry.getKey(), JSONObject.toJSONString(entry.getValue()));
        }
    }

    /**
     * 根据银联进件信息写入缓存
     * @param selfSignEntity
     */
    @Override
    public void writeIncomingCacheBySelfSign(SelfSignEntity selfSignEntity) {
        log.info("IncomingCacheServiceImpl--writeIncomingCacheBySelfSign, selfSignEntity:{}", JSONObject.toJSONString(selfSignEntity));
        if (ObjectUtils.isEmpty(selfSignEntity) || StringUtils.isBlank(selfSignEntity.getAccesserAcct())) {
            return;
        }
        Long businessId;
        //appId为云商时，查询supplier信息，或者云商id
        if (yunshangAppId.equals(selfSignEntity.getAppId())) {
            com.tfjt.tfcommon.dto.response.Result<SupplierBasicInfoRespDTO> supplierResult =  supplierInfoApiService.getBySupplierUuid(selfSignEntity.getAccesserAcct());
            if (ObjectUtils.isEmpty(supplierResult) || ObjectUtils.isEmpty(supplierResult.getData())) {
                log.error("IncomingCacheServiceImpl--writeIncomingCacheBySelfSign, selfSignEntity:{}", JSONObject.toJSONString(selfSignEntity));
                throw new TfException(ExceptionCodeEnum.SUPPLIER_IS_NULL);
            }
            businessId = Long.valueOf(supplierResult.getData().getId());
        } else {//appId为云店时，截取accesserAcct字段获取云店id
            String shopId = selfSignEntity.getAccesserAcct().substring(4, selfSignEntity.getAccesserAcct().length());
            businessId = devConfig.isPreOrProd() ? Long.valueOf(shopId) : Long.valueOf(selfSignEntity.getAccesserAcct());
        }
        Integer businessType = yunshangAppId.equals(selfSignEntity.getAppId()) ?
                IncomingMemberBusinessTypeEnum.YUNSHANG.getCode() : IncomingMemberBusinessTypeEnum.YUNDIAN.getCode();
        IncomingMessageRespDTO incomingMessageResp = new IncomingMessageRespDTO();
        incomingMessageResp.setBusinessId(businessId);
        incomingMessageResp.setBusinessType(businessType);
        incomingMessageResp.setAccessChannelType(IncomingAccessChannelTypeEnum.UNIONPAY.getCode());
        incomingMessageResp.setUnionpaySignStatus(selfSignEntity.getSigningStatus());
        incomingMessageResp.setAccountNo(selfSignEntity.getMid());
        incomingMessageResp.setAccountNo2(selfSignEntity.getBusinessNo());
        String cacheKey = RedisConstant.INCOMING_MSG_KEY_PREFIX + IncomingAccessChannelTypeEnum.UNIONPAY.getCode() + ":" +
                businessType + ":" + businessId;
        redisCache.setCacheString(cacheKey, JSONObject.toJSONString(incomingMessageResp));
    }

    private Map<String, IncomingMessageRespDTO> queryPnIncomingMessageMap(Map<String, QueryIncomingStatusReqDTO> pnIncomingReqMap) {
        Map<String, IncomingMessageRespDTO> incomingMeaasgeMap = new HashMap<>();
        List<IncomingMessageReqDTO> incomingMessageReqs = new ArrayList<>();
        for (Map.Entry<String, QueryIncomingStatusReqDTO> entry : pnIncomingReqMap.entrySet()) {
            String key = RedisConstant.INCOMING_MSG_KEY_PREFIX +  entry.getValue().getAccessChannelType() + ":"
                    + entry.getValue().getBusinessType() + ":" + entry.getValue().getBusinessId();
            IncomingMessageReqDTO  incomingMessageReq = new IncomingMessageReqDTO();
            incomingMessageReq.setAccessChannelType(entry.getValue().getAccessChannelType());
            incomingMessageReq.setBusinessType(entry.getValue().getBusinessType());
            incomingMessageReq.setBusinessId(entry.getValue().getBusinessId());
            incomingMessageReqs.add(incomingMessageReq);
            //往返回map中放入默认状态“未入网”
            IncomingMessageRespDTO incomingMessageResp = new IncomingMessageRespDTO();
            incomingMessageResp.setBusinessId(entry.getValue().getBusinessId());
            incomingMessageResp.setBusinessType(entry.getValue().getBusinessType());
            incomingMessageResp.setAccessChannelType(entry.getValue().getAccessChannelType());
            incomingMessageResp.setAccessStatus(0);
            incomingMeaasgeMap.put(key, incomingMessageResp);
        }
        //查询数据库
        List<IncomingMessageRespDTO> incomingMessages = tfIncomingInfoService.queryIncomingMessagesByMerchantList(incomingMessageReqs);
        if (CollectionUtils.isEmpty(incomingMessages)) {
            return incomingMeaasgeMap;
        }
        //数据库查询到的数据替换返回map中的值
        incomingMessages.forEach(incomingMessage -> {
            String key = RedisConstant.INCOMING_MSG_KEY_PREFIX  + incomingMessage.getAccessChannelType() + ":"
                    + incomingMessage.getBusinessType() + ":" + incomingMessage.getBusinessId();
            incomingMeaasgeMap.put(key, incomingMessage);
        });
        return incomingMeaasgeMap;
    }


    private Map<String, IncomingMessageRespDTO> queryUnIncomingMessageMap(Map<String, QueryIncomingStatusReqDTO> unIncomingReqMap) {
        Map<String, IncomingMessageRespDTO> incomingMeaasgeMap = new HashMap<>();
        List<String> accts = new ArrayList<>();
        List<Integer> supplierIds = new ArrayList<>();
        Map<String, Integer> supplierIdMap = new HashMap<>();
        for (Map.Entry<String, QueryIncomingStatusReqDTO> entry : unIncomingReqMap.entrySet()) {
            String key = RedisConstant.INCOMING_MSG_KEY_PREFIX +  entry.getValue().getAccessChannelType() + ":"
                    + entry.getValue().getBusinessType() + ":" + entry.getValue().getBusinessId();
            if (IncomingMemberBusinessTypeEnum.YUNSHANG.getCode().equals(entry.getValue().getBusinessType())) {
                supplierIds.add(entry.getValue().getBusinessId().intValue());
            } else {
                String acct = devConfig.isPreOrProd() ? "tfys" + entry.getValue().getBusinessId() : entry.getValue().getBusinessId().toString();
                accts.add(acct);
                supplierIdMap.put(acct, entry.getValue().getBusinessId().intValue());
            }
            //往返回map中放入默认状态“未入网”
            IncomingMessageRespDTO incomingMessageResp = new IncomingMessageRespDTO();
            incomingMessageResp.setBusinessId(entry.getValue().getBusinessId());
            incomingMessageResp.setBusinessType(entry.getValue().getBusinessType());
            incomingMessageResp.setAccessChannelType(entry.getValue().getAccessChannelType());
            incomingMessageResp.setUnionpaySignStatus("-1");
            incomingMeaasgeMap.put(key, incomingMessageResp);
        }
        List<TfSupplierDTO> tfSuppliers = new ArrayList<>();
        if (!CollectionUtils.isEmpty(supplierIds)) {
            tfSuppliers = tfSupplierApi.getTfSupplierList(supplierIds);
        }
        if (!CollectionUtils.isEmpty(tfSuppliers)) {
            tfSuppliers.forEach(tfSupplier -> {
                accts.add(tfSupplier.getSupplierId());
                supplierIdMap.put(tfSupplier.getSupplierId(), tfSupplier.getId());
            });
        }
        //查询银联入网数据
        List<SelfSignEntity> selfSignEntities = selfSignService.querySelfSignsByAccessAccts(accts);
        if (CollectionUtils.isEmpty(selfSignEntities)) {
            return incomingMeaasgeMap;
        }
        selfSignEntities.forEach(selfSignEntity -> {
            //往返回map中放入入网信息
            IncomingMessageRespDTO incomingMessageResp = new IncomingMessageRespDTO();
            incomingMessageResp.setBusinessId(supplierIdMap.get(selfSignEntity.getAccesserAcct()).longValue());
            if (yundianAppId.equals(selfSignEntity.getAppId())) {
                incomingMessageResp.setBusinessType(IncomingMemberBusinessTypeEnum.YUNDIAN.getCode());
            } else {
                incomingMessageResp.setBusinessType(IncomingMemberBusinessTypeEnum.YUNSHANG.getCode());
            }
            incomingMessageResp.setAccessChannelType(IncomingAccessChannelTypeEnum.UNIONPAY.getCode());
            incomingMessageResp.setUnionpaySignStatus(selfSignEntity.getSigningStatus());
            incomingMessageResp.setAccountNo(selfSignEntity.getMid());
            incomingMessageResp.setAccountNo2(selfSignEntity.getBusinessNo());
            incomingMeaasgeMap.put(RedisConstant.INCOMING_MSG_KEY_PREFIX + IncomingAccessChannelTypeEnum.UNIONPAY.getCode() + ":"
                    + incomingMessageResp.getBusinessType() + ":"
                    + supplierIdMap.get(selfSignEntity.getAccesserAcct()).longValue(), incomingMessageResp);
        });
        return incomingMeaasgeMap;
    }

    public static void main(String[] args) {
        String acc = "tfys1221";
        System.out.println(acc.substring(4, acc.length()));
    }
}
