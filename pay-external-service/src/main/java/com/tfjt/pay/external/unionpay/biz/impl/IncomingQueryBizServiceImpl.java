package com.tfjt.pay.external.unionpay.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.tfjt.api.TfSupplierApiService;
import com.tfjt.dto.TfSupplierDTO;
import com.tfjt.pay.external.query.api.dto.req.QueryIncomingStatusReqDTO;
import com.tfjt.pay.external.query.api.dto.resp.QueryIncomingStatusRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.biz.IncomingQueryBizService;
import com.tfjt.pay.external.unionpay.config.DevConfig;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.RedisConstant;
import com.tfjt.pay.external.unionpay.entity.SalesAreaIncomingChannelEntity;
import com.tfjt.pay.external.unionpay.entity.SelfSignEntity;
import com.tfjt.pay.external.unionpay.enums.*;
import com.tfjt.pay.external.unionpay.service.SalesAreaIncomingChannelService;
import com.tfjt.pay.external.unionpay.service.SelfSignService;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.tfjt.pay.external.unionpay.utils.NetworkTypeCacheUtil;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/4 14:35
 * @description 进件信息查询相关
 */
@Slf4j
@Service
public class IncomingQueryBizServiceImpl implements IncomingQueryBizService {

    @Autowired
    private TfIncomingInfoService tfIncomingInfoService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private NetworkTypeCacheUtil networkTypeCacheUtil;

    @Autowired
    private SelfSignService selfSignService;

    @Autowired
    private SalesAreaIncomingChannelService salesAreaIncomingChannelService;

    @Autowired
    private DevConfig devConfig;

    @DubboReference(retries = 0, timeout = 2000, check = false)
    private TfSupplierApiService tfSupplierApiService;

    private static final String NO_ACCESS_STATUS = "00";

    private static final String HAS_ACCESS_STATUS = "03";


    private static final Set<Integer> PN_OPEN_ACCOUNT_STATUS_SET = new HashSet<>();

    static {
        PN_OPEN_ACCOUNT_STATUS_SET.add(IncomingAccessStatusEnum.SIGN_SUCCESS.getCode());
        PN_OPEN_ACCOUNT_STATUS_SET.add(IncomingAccessStatusEnum.BINK_CARD_SUCCESS.getCode());
        PN_OPEN_ACCOUNT_STATUS_SET.add(IncomingAccessStatusEnum.ACCESS_SUCCESS.getCode());
        PN_OPEN_ACCOUNT_STATUS_SET.add(IncomingAccessStatusEnum.SMS_VERIFICATION_SUCCESS.getCode());
    }

    /**
     * 根据商户id、商户类型、区域（单个）查询入网状态
     * @param queryIncomingStatusReqDTO
     * @return
     */
    @Override
    public Result<QueryIncomingStatusRespDTO> queryIncomingStatus(QueryIncomingStatusReqDTO queryIncomingStatusReqDTO) {
        log.info("IncomingQueryBizServiceImpl--queryIncomingStatus, reqDTO:{}", JSONObject.toJSONString(queryIncomingStatusReqDTO));
        ValidatorUtils.validateEntity(queryIncomingStatusReqDTO);
        //该查询入参中“区域code”不能为空
        if (StringUtils.isBlank(queryIncomingStatusReqDTO.getAreaCode())) {
            return Result.failed(ExceptionCodeEnum.QUERY_INCOMING_MSG_ILLEGAL_ARGUMENT);
        }
        //根据区域获取入网渠道
        Integer channelCode = networkTypeCacheUtil.getNetworkTypeCacheList(queryIncomingStatusReqDTO.getAreaCode());
        QueryIncomingStatusRespDTO queryIncomingStatusRespDTO =
                getIncomingStatusResp(channelCode, queryIncomingStatusReqDTO.getBusinessType(), queryIncomingStatusReqDTO.getBusinessId());

        log.info("IncomingQueryBizServiceImpl--queryIncomingStatus, queryIncomingStatusRespDTO:{}", JSONObject.toJSONString(queryIncomingStatusRespDTO));
//        if (ObjectUtils.isEmpty(incomingMessageRespDTO)) {
//            incomingMessageRespDTO.setAccessStatus(0);
//            incomingMessageRespDTO.setUnionpaySignStatus(NO_ACCESS_STATUS);
//            return Result.ok();
//        }

        return Result.ok(queryIncomingStatusRespDTO);
    }

    /**
     * 根据商户id、商户类型、区域（单个）批量查询入网状态,结果集放入map，key为“入网渠道”-“商户类型”-“商户id”
     * @param queryIncomingStatusReqDTOS
     * @return
     */
    @Override
    public Result<Map<String, QueryIncomingStatusRespDTO>> batchQueryIncomingStatus(List<QueryIncomingStatusReqDTO> queryIncomingStatusReqDTOS) {
        return null;
    }

    /**
     * 根据商户id、商户类型、区域（多个）查询入网状态
     * @param queryIncomingStatusReqDTO
     * @return
     */
    @Override
    public Result<QueryIncomingStatusRespDTO> queryIncomingStatusByAreaCodes(QueryIncomingStatusReqDTO queryIncomingStatusReqDTO) {
        QueryIncomingStatusRespDTO incomingStatusResp = new QueryIncomingStatusRespDTO();
        incomingStatusResp.setBusinessId(queryIncomingStatusReqDTO.getBusinessId());
        incomingStatusResp.setBusinessType(queryIncomingStatusReqDTO.getBusinessType());
        List<String> incomingChannels;
        Set<String> cacheKeys = new HashSet<>();
        Set<String> cacheNullCodes = new HashSet<>();
        queryIncomingStatusReqDTO.getAreaCodes().forEach(areaCode -> {
            cacheKeys.add(RedisConstant.NETWORK_TYPE_BY_AREA_CODE + areaCode);
            cacheNullCodes.add(areaCode);
        });
        //批量查询Redis
        incomingChannels = redisTemplate.opsForValue().multiGet(cacheKeys);
        List<SalesAreaIncomingChannelEntity> areaIncomingChannels = new ArrayList<>();
        //如果缓存查询为空，则查询数据库
        if (CollectionUtils.isEmpty(incomingChannels)) {
            areaIncomingChannels = salesAreaIncomingChannelService.queryByDistrictsCodes(queryIncomingStatusReqDTO.getAreaCodes());
            //异步更新缓存
            networkTypeCacheUtil.writeCache(cacheNullCodes);
        } else {
            //遍历序列化缓存数据，将缓存中存在的数据从 cacheNullCodes 中移除
            for (String incomingChannel : incomingChannels) {
                SalesAreaIncomingChannelEntity areaIncomingChannel = JSONObject.parseObject(incomingChannel, SalesAreaIncomingChannelEntity.class);
                areaIncomingChannels.add(areaIncomingChannel);
                cacheNullCodes.remove(areaIncomingChannel.getChannelCode());
            }
            //如果 cacheNullCodes 中剩余元素，即缓存未查到，尝试查询数据库获取
            if (!CollectionUtils.isEmpty(cacheNullCodes)) {
//                List<String> areaCodes = cacheNullCodes.stream().collect(Collectors.toList());
                areaIncomingChannels.addAll(salesAreaIncomingChannelService.queryByDistrictsCodesSet(cacheNullCodes));
                //异步更新缓存
                networkTypeCacheUtil.writeCache(cacheNullCodes);
            }
        }
        Integer flag;
        //未查询到任何渠道配置数据，该渠道为银联
        if (CollectionUtils.isEmpty(areaIncomingChannels)) {
            flag = NumberConstant.TWO;
        } else {
            //补齐渠道信息完成集合
            getAreaIncomingChannels(cacheKeys, queryIncomingStatusReqDTO.getAreaCodes(), areaIncomingChannels);
            //根据区域渠道集合获取入网渠道标识，1：平安  2：银联  3：平安+银联
            flag = getChannelFlag(areaIncomingChannels);
        }
        //根据入网渠道标识、商户类型、商户id获取入网状态
        String incomingStatus = getIncomingStatusByChannelFlag(flag, queryIncomingStatusReqDTO.getBusinessType(), queryIncomingStatusReqDTO.getBusinessId());
        log.info("IncomingQueryBizServiceImpl--queryIncomingStatusByAreaCodes, incomingStatus:{}", incomingStatus);
        incomingStatusResp.setIncomingStatus(incomingStatus);
        return Result.ok(incomingStatusResp);
    }

    /**
     * 根据渠道、商户类型、商户id 获取入网状态
     * @param channelCode
     * @param businessType
     * @param businessId
     * @return
     */
    private QueryIncomingStatusRespDTO getIncomingStatusResp(Integer channelCode, Integer businessType, Long businessId) {
        QueryIncomingStatusRespDTO queryIncomingStatusRespDTO = new QueryIncomingStatusRespDTO();
        queryIncomingStatusRespDTO.setBusinessId(businessId);
        queryIncomingStatusRespDTO.setBusinessType(businessType);
        //根据区域获取入网渠道
        String cacheKey = RedisConstant.INCOMING_MSG_KEY_PREFIX + channelCode + ":" +
                businessType + ":" + businessId;
        queryIncomingStatusRespDTO.setAccessChannelType(channelCode);
        //获取缓存
        String incomingMsgStr = redisCache.getCacheString(cacheKey);
        log.info("IncomingQueryBizServiceImpl--queryIncomingStatus, incomingMsgStr:{}", incomingMsgStr);
        //缓存命中
        if (StringUtils.isNotBlank(incomingMsgStr)) {
            IncomingMessageRespDTO incomingMessageRespDTO = JSONObject.parseObject(incomingMsgStr, IncomingMessageRespDTO.class);
            queryIncomingStatusRespDTO.setIncomingStatus(getAccessStatus(channelCode, incomingMessageRespDTO));
            return queryIncomingStatusRespDTO;
        }
        IncomingMessageReqDTO incomingMessageReqDTO = new IncomingMessageReqDTO();
        BeanUtils.copyProperties(queryIncomingStatusRespDTO, incomingMessageReqDTO);
        //根据渠道查询数据库中入网信息
        IncomingMessageRespDTO incomingMessageRespDTO = getIncomingMessage(channelCode, incomingMessageReqDTO);
        log.info("IncomingQueryBizServiceImpl--queryIncomingStatus, incomingMessageRespDTO:{}", JSONObject.toJSONString(incomingMessageRespDTO));
//        if (ObjectUtils.isEmpty(incomingMessageRespDTO)) {
//            incomingMessageRespDTO.setAccessStatus(0);
//            incomingMessageRespDTO.setUnionpaySignStatus(NO_ACCESS_STATUS);
//            return Result.ok();
//        }
        //设置缓存
        redisCache.setCacheString(cacheKey, JSONObject.toJSONString(incomingMessageRespDTO));
        queryIncomingStatusRespDTO.setIncomingStatus(getAccessStatus(channelCode, incomingMessageRespDTO));
        return queryIncomingStatusRespDTO;
    }

    /**
     * 根据入网信息获取入网状态
     * @param channelCode
     * @return
     */
    private String getAccessStatus(Integer channelCode, IncomingMessageRespDTO incomingMessageRespDTO) {
        String accessStatus = NO_ACCESS_STATUS;
        //根据渠道判断是否入网成功
        if (IncomingAccessChannelTypeEnum.PINGAN.getCode().equals(channelCode)) {
            if (PN_OPEN_ACCOUNT_STATUS_SET.contains(incomingMessageRespDTO.getAccessStatus())) {
                accessStatus = HAS_ACCESS_STATUS;
            }
        }
        if (IncomingAccessChannelTypeEnum.UNIONPAY.getCode().equals(channelCode)) {
            if (HAS_ACCESS_STATUS.equals(incomingMessageRespDTO.getUnionpaySignStatus())) {
                accessStatus = HAS_ACCESS_STATUS;
            }
        }
        return accessStatus;
    }

    /**
     * 获取入网信息
     * @param channelCode
     * @param incomingMessageReqDTO
     * @return
     */
    private IncomingMessageRespDTO getIncomingMessage(Integer channelCode, IncomingMessageReqDTO incomingMessageReqDTO) {
        IncomingMessageRespDTO incomingMessageRespDTO = new IncomingMessageRespDTO();
        //查询平安入网进件信息
        if (IncomingAccessChannelTypeEnum.PINGAN.getCode().equals(channelCode)) {
            incomingMessageRespDTO = tfIncomingInfoService.queryIncomingMessageByMerchant(incomingMessageReqDTO);
            //如果结算类型为对公，会员名称返回“营业名称”，否则返回“法人姓名”
            if (IncomingSettleTypeEnum.CORPORATE.getCode().equals(incomingMessageRespDTO.getSettlementAccountType())) {
                incomingMessageRespDTO.setMemberName(incomingMessageRespDTO.getBusinessName());
            } else {
                incomingMessageRespDTO.setMemberName(incomingMessageRespDTO.getLegalName());
            }
        }
        //查询银联入网进件信息
        if (IncomingAccessChannelTypeEnum.UNIONPAY.getCode().equals(channelCode)) {
            String acct = devConfig.isPreOrProd() ? "tfys" + incomingMessageReqDTO.getBusinessId() : incomingMessageReqDTO.getBusinessId().toString();
            //如果是云商，先查询supplier获取supplierId
            if (IncomingMemberBusinessTypeEnum.YUNSHANG.getCode().equals(incomingMessageReqDTO.getBusinessType())) {
                com.tfjt.tfcommon.utils.Result<TfSupplierDTO> result =  tfSupplierApiService.getSupplierInfoById(incomingMessageReqDTO.getBusinessId());
                if (ObjectUtils.isEmpty(result) || ObjectUtils.isEmpty(result.getData())) {
                    incomingMessageRespDTO.setUnionpaySignStatus(NO_ACCESS_STATUS);
                    return incomingMessageRespDTO;
                }
                acct = result.getData().getSupplierId();
            }
            SelfSignEntity selfSignEntity = selfSignService.querySelfSignByAccessAcct(acct);
            if (ObjectUtils.isEmpty(selfSignEntity)) {
                incomingMessageRespDTO.setUnionpaySignStatus(NO_ACCESS_STATUS);
            } else {
                incomingMessageRespDTO.setUnionpaySignStatus(selfSignEntity.getSigningStatus());
                incomingMessageRespDTO.setAccountNo(selfSignEntity.getMid());
            }

        }
        return incomingMessageRespDTO;
    }

    /**
     * 根据入网渠道标识、商户类型、商户id获取入网状态
     * @param channelFlag
     * @param businessType
     * @param businessId
     * @return
     */
    private String getIncomingStatusByChannelFlag(Integer channelFlag, Integer businessType, Long businessId) {
        //标识不等于3时，根据标识获取对应渠道的入网状态
        if (!NumberConstant.THREE.equals(channelFlag)) {
            QueryIncomingStatusRespDTO statusResp = getIncomingStatusResp(channelFlag,
                    businessType, businessId);
            log.info("IncomingQueryBizServiceImpl--queryIncomingStatusByAreaCodes, statusResp:{}", JSONObject.toJSONString(statusResp));
            return statusResp.getIncomingStatus();
        }
        //标识等于3时，需要同时判断“银联”与“平安”入网状态，都入网成功才返回成功
        QueryIncomingStatusRespDTO pnResp = getIncomingStatusResp(IncomingAccessChannelTypeEnum.PINGAN.getCode(),
                businessType, businessId);
        QueryIncomingStatusRespDTO unResp = getIncomingStatusResp(IncomingAccessChannelTypeEnum.UNIONPAY.getCode(),
                businessType, businessId);
        log.info("IncomingQueryBizServiceImpl--queryIncomingStatusByAreaCodes, pnResp:{}, unResp:{}",
                JSONObject.toJSONString(pnResp), JSONObject.toJSONString(unResp));
        if (HAS_ACCESS_STATUS.equals(pnResp.getIncomingStatus()) && HAS_ACCESS_STATUS.equals(unResp.getIncomingStatus())) {
            return HAS_ACCESS_STATUS;
        } else {
            return NO_ACCESS_STATUS;
        }
    }

    /**
     * 获取区域对应渠道完整集合
     * @param cacheKeys
     * @param areaCodes
     * @param areaIncomingChannels
     */
    private void getAreaIncomingChannels(Set<String> cacheKeys, List<String> areaCodes, List<SalesAreaIncomingChannelEntity> areaIncomingChannels) {
        Map<String, SalesAreaIncomingChannelEntity> areaIncomingChannelMap = new HashMap<>();
        //将查询到的数据集合存入map
        for (SalesAreaIncomingChannelEntity areaIncomingChannel : areaIncomingChannels) {
            areaIncomingChannelMap.put(areaIncomingChannel.getDistrictsCode(), areaIncomingChannel);
        }
        //如果入参区域数大于查询到的数据条数，将未查询到的区域设置为“银联”渠道，放入集合
        if (cacheKeys.size() > areaIncomingChannels.size()) {
            for (String areaCode : areaCodes) {
                if (areaIncomingChannelMap.containsKey(areaCode)) {
                    continue;
                }
                SalesAreaIncomingChannelEntity salesAreaIncomingChannelEntity = new SalesAreaIncomingChannelEntity();
                salesAreaIncomingChannelEntity.setDistrictsCode(areaCode);
                salesAreaIncomingChannelEntity.setChannelCode(IncomingAccessChannelTypeEnum.UNIONPAY.getCode().toString());
                areaIncomingChannels.add(salesAreaIncomingChannelEntity);
            }
        }
    }
    /**
     * 根据区域渠道集合获取入网渠道标识，1：平安  2：银联  3：平安+银联
     * @param areaIncomingChannels
     * @return
     */
    private Integer getChannelFlag(List<SalesAreaIncomingChannelEntity> areaIncomingChannels) {
        Integer flag;
        Map<String, Boolean> channelConfigFlagMap = new HashMap<>();
        //遍历结果集合，以channelCode为key存入map
        for (SalesAreaIncomingChannelEntity areaIncomingChannel : areaIncomingChannels) {
            channelConfigFlagMap.put(areaIncomingChannel.getChannelCode(), Boolean.TRUE);
        }
        //如果map中同时存在“平安”与“银联”渠道数据，设置标识为3：两个渠道都需要进件，否则设置为map中对应的channelCode
        if (channelConfigFlagMap.containsKey(IncomingAccessChannelTypeEnum.PINGAN.getCode().toString()) &&
                channelConfigFlagMap.containsKey(IncomingAccessChannelTypeEnum.UNIONPAY.getCode().toString())) {
            flag = NumberConstant.THREE;
        } else {
            flag = channelConfigFlagMap.containsKey(IncomingAccessChannelTypeEnum.PINGAN.getCode().toString()) ?
                    NumberConstant.ONE : NumberConstant.TWO;
        }
        return flag;
    }
    public static void main(String[] a) {
        IncomingMessageRespDTO mes = new IncomingMessageRespDTO();
        IncomingMessageRespDTO mes2 = mes;
        mes.setAccessStatus(1);
        mes2.setAccessStatus(2);
        System.out.println("mes:" + JSONObject.toJSONString(mes) + "-" + JSONObject.toJSONString(mes2));

    }
}
