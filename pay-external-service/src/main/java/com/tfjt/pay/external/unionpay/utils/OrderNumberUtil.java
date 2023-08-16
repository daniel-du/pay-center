package com.tfjt.pay.external.unionpay.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.lock.annotation.Lock4j;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.RedisConstant;
import com.tfjt.tfcommon.core.cache.RedisCache;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 订单号生成工具
 *
 * @author songx
 * @date 2023-08-10 17:02
 * @email 598482054@qq.com
 */
@Component
public class OrderNumberUtil {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsss");

    @Autowired
    private RedisCache redisCache;

    /**
     * 生成当日不重复打订单号
     * @param type 交易类型 com.tfjt.pay.external.unionpay.constants.TransactionTypeConstants
     * @return
     */
    //@Lock4j(keys = {"generateOrderNumber"}, expire = 1000)
    public String generateOrderNumber(String type) {
        // 获取当前日期
        String currentDate = dateFormat.format(new Date());
        // 生成随机数
        Random randomGenerator = new Random();
        int randomNum = randomGenerator.nextInt(10000);
        // 组合订单号
        String orderNumber = type + currentDate + String.format("%04d", randomNum);
        Object cacheObject = redisCache.getCacheObject(RedisConstant.PAY_GENERATE_ORDER_NO + ":" + orderNumber);
        if (cacheObject != null) {
            orderNumber = generateOrderNumber(type);
        } else {
            redisCache.setCacheObject(RedisConstant.PAY_GENERATE_ORDER_NO + ":" + orderNumber, orderNumber, NumberConstant.ONE, TimeUnit.DAYS);
        }
        return orderNumber;
    }
}