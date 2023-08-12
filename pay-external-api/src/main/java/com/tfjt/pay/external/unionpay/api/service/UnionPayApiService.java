package com.tfjt.pay.external.unionpay.api.service;

import com.tfjt.pay.external.unionpay.api.dto.resp.UnionPayTransferDTO;
import com.tfjt.tfcommon.dto.response.Result;

/**
 * 银联dubbo接口层
 * @author songx
 * @date 2023-08-10 17:02
 * @email 598482054@qq.com
 */
public interface UnionPayApiService {
    /**
     * 转账接口
     * @param payTransferDTO
     */
    Result<String> transfer(UnionPayTransferDTO payTransferDTO);

    /**
     * 获取同福母账户当前账户余额
     * @return
     */
    Result<Integer> currentBalance();
}
