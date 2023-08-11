package com.tfjt.pay.external.api;

import com.tfjt.pay.external.dto.UnionPayTransferDTO;
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
}
