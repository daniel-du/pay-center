package com.tfjt.pay.external.unionpay.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.constants.RedisConstant;
import com.tfjt.pay.external.unionpay.entity.SalesAreaIncomingChannelEntity;
import com.tfjt.pay.external.unionpay.enums.CityTypeEnum;
import com.tfjt.pay.external.unionpay.service.SalesAreaIncomingChannelService;
import com.tfjt.tfcommon.core.cache.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
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
                redisCache.setCacheString(key, JSONObject.toJSONString(entity));
                return Integer.valueOf(entity.getChannelCode());
            }else {
                return CityTypeEnum.OLD_CITY.getCode();
            }
        }
        SalesAreaIncomingChannelEntity entity = JSONObject.parseObject(cacheString, SalesAreaIncomingChannelEntity.class);
        if (ObjectUtil.isNotNull(entity) && StringUtils.isNotBlank(entity.getChannelCode())) {
            return Integer.valueOf(entity.getChannelCode());
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

}
