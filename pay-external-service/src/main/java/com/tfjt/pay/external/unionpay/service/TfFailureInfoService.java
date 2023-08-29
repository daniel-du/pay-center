package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.TfFailureInfoEntity;

/**
 *
 */
public interface TfFailureInfoService extends IService<TfFailureInfoEntity> {

    /**
     * 保存 幂等性错误日志
     * @param keyName
     * @param name
     * @param arguments
     */
    void saveLog(String keyName, String name, Object[] arguments);
}
