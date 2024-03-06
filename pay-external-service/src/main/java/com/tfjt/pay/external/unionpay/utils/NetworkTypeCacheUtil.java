package com.tfjt.pay.external.unionpay.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.constants.RedisConstant;
import com.tfjt.pay.external.unionpay.entity.SalesAreaIncomingChannelEntity;
import com.tfjt.pay.external.unionpay.enums.CityTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessChannelTypeEnum;
import com.tfjt.pay.external.unionpay.service.SalesAreaIncomingChannelService;
import com.tfjt.tfcommon.core.cache.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author zxy
 * @create 2024/1/10 9:21
 */
@Component
public class NetworkTypeCacheUtil {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private SalesAreaIncomingChannelService salesAreaIncomingChannelService;


    public Integer getNetworkTypeCacheList(String code) {
        String key = RedisConstant.NETWORK_TYPE_BY_AREA_CODE + code;
        String cacheString = redisCache.getCacheString(key);
        if (StringUtils.isBlank(cacheString)) {
            SalesAreaIncomingChannelEntity entity = salesAreaIncomingChannelService.getOne(new LambdaQueryWrapper<SalesAreaIncomingChannelEntity>().eq(SalesAreaIncomingChannelEntity::getDistrictsCode, code));
            if (ObjectUtil.isNotNull(entity)) {
                return Integer.valueOf(entity.getChannelCode());
            }
        }
        return CityTypeEnum.OLD_CITY.getCode();
    }

    public List<String> getAllNetworkTypeCacheList() {
        List<String> cacheString = redisCache.getCacheList(RedisConstant.NETWORK_TYPE_BY_AREA_CODE_All);
        if (CollectionUtil.isEmpty(cacheString)) {
            List<SalesAreaIncomingChannelEntity> list = salesAreaIncomingChannelService.list();
            if (CollectionUtil.isNotEmpty(list)) {
                cacheString = list.stream().map(SalesAreaIncomingChannelEntity::getDistrictsCode).collect(Collectors.toList());
                redisCache.setCacheList(RedisConstant.NETWORK_TYPE_BY_AREA_CODE_All,cacheString);
            }
        }
        return cacheString;
    }

    /**
     * 异步写入渠道配置缓存
     * @param areaCodes
     */
    @Async
    public void writeCache(Set<String> areaCodes) {
        List<SalesAreaIncomingChannelEntity> areaIncomingChannels = salesAreaIncomingChannelService.queryByDistrictsCodesSet(areaCodes);
        if (!CollectionUtils.isEmpty(areaIncomingChannels)) {
            //遍历数据库查询结果，写入缓存，移除set中元素
            for (SalesAreaIncomingChannelEntity areaIncomingChannel : areaIncomingChannels) {
                redisCache.setCacheString(RedisConstant.NETWORK_TYPE_BY_AREA_CODE + areaIncomingChannel.getChannelCode(), JSONObject.toJSONString(areaIncomingChannel));
                areaCodes.remove(areaIncomingChannel.getChannelCode());
            }
        }
        //剩余set中元素不为空时，剩余区域未配置渠道，默认为银联，写入缓存
        if (!CollectionUtils.isEmpty(areaCodes)) {
            for (String areaCode : areaCodes) {
                SalesAreaIncomingChannelEntity salesAreaIncomingChannel = new SalesAreaIncomingChannelEntity();
                salesAreaIncomingChannel.setChannelCode(IncomingAccessChannelTypeEnum.UNIONPAY.getCode().toString());
                salesAreaIncomingChannel.setDistrictsCode(areaCode);
                redisCache.setCacheString(RedisConstant.NETWORK_TYPE_BY_AREA_CODE + areaCode, JSONObject.toJSONString(salesAreaIncomingChannel));
            }
        }

    }

}
