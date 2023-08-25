package com.tfjt.pay.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.trade.dto.req.PayApplicationEntityDTO;
import com.tfjt.pay.trade.entity.PayApplicationEntity;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.Map;

/**
 * 应用表
 *
 * @author ???
 * @email 598085205@qq.com
 * @date 2022-11-05 10:11:23
 */
public interface PayApplicationService extends IService<PayApplicationEntity> {

    /**
     * 创建应用
     * @param payApplicationEntityDTO
     */
    Result savePayApplication(PayApplicationEntityDTO payApplicationEntityDTO) throws Exception;

    /**
     * 根据应用获取token
     * @param appId
     * @return
     */
    Map getToken(String appId, String appSecret);

    /**
     * 加载appsecret到redis
     * @return
     */
    int loadAppSecret();
}

