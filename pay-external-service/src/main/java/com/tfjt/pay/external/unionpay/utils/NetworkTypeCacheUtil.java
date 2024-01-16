package com.tfjt.pay.external.unionpay.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.tfjt.pay.external.unionpay.constants.RedisConstant;
import com.tfjt.pay.external.unionpay.entity.SalesAreaIncomingChannelEntity;
import com.tfjt.pay.external.unionpay.service.SalesAreaIncomingChannelService;
import com.tfjt.tfcommon.core.cache.RedisCache;
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


    public List<String> getNetworkTypeCacheList() {
        List<String> cacheList = redisCache.getCacheList(RedisConstant.NETWORK_TYPE_BY_AREA_CODE);
        if (CollectionUtil.isEmpty(cacheList)) {
            List<SalesAreaIncomingChannelEntity> list = salesAreaIncomingChannelService.list();
            if (CollectionUtil.isNotEmpty(list)) {
                cacheList = list.stream().map(SalesAreaIncomingChannelEntity::getDistrictsCode).collect(Collectors.toList());
                redisCache.setCacheList(RedisConstant.NETWORK_TYPE_BY_AREA_CODE,cacheList);
            }
        }
        return cacheList;
    }
}
