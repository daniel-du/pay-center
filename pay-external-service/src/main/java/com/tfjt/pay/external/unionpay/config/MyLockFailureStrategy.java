package com.tfjt.pay.external.unionpay.config;

import com.baomidou.lock.LockFailureStrategy;
import com.baomidou.lock.exception.LockFailureException;
import com.tfjt.pay.external.unionpay.service.TfFailureInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * 分布式锁
 */
@Component
@Slf4j
public class MyLockFailureStrategy implements LockFailureStrategy {
    @Resource
    private TfFailureInfoService tfFailureInfoService;

    @Override
    public void onLockFailure(String key, Method method, Object[] arguments) {
        log.error("获取锁失败key:{}方法:{}参数:{}",key, method.getName(), arguments);
        this.tfFailureInfoService.saveLog(key,method.getName(),arguments);
        throw new LockFailureException("重复提交，请稍后再试。");
    }
}
