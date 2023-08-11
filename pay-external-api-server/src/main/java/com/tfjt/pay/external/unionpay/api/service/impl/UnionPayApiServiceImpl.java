package com.tfjt.pay.external.unionpay.api.service.impl;

import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 银联接口服务实现类
 * @author songx
 * @date 2023-08-10 17:02
 * @email 598482054@qq.com
 */
@Slf4j
@DubboService
public class UnionPayApiServiceImpl implements UnionPayApiService {
    @Override
    public Result<String> transfer(UnionPayTransferDTO payTransferDTO) {
        return null;
    }
}
