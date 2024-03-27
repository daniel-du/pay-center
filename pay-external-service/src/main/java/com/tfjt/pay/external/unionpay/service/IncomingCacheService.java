package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.query.api.dto.req.QueryIncomingStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.entity.SelfSignEntity;

import java.util.List;
import java.util.Map;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/25 9:57
 * @description 进件缓存服务
 */
public interface IncomingCacheService {

    /**
     * 写入进件缓存
     * @param incomingSubmitMessageDTO
     */
    void writeIncomingCache(IncomingSubmitMessageDTO incomingSubmitMessageDTO);

    /**
     * 批量写入进件缓存
     * @param accessChannel
     * @param incomingReqMap
     */
    void batchWriteIncomingCache(Integer accessChannel, Map<String, QueryIncomingStatusReqDTO> incomingReqMap);

    /**
     * 批量写入进件缓存
     * @param queryDBReqs
     */
    void batchWriteIncomingCache(List<IncomingMessageReqDTO> queryDBReqs);

    /**
     * 根据银联进件信息写入缓存
     * @param selfSignEntity
     */
    void writeIncomingCacheBySelfSign(SelfSignEntity selfSignEntity);
}
