package com.tfjt.pay.external.unionpay.api.service;

import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.tfcommon.dto.response.Result;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/25 16:17
 * @description 进件信息服务
 */
public interface IncomingApiService {

    /**
     * 根据商户信息查询进件信息
     * @param incomingMessageReqDTO
     * @return
     */
    Result<IncomingMessageRespDTO> queryIncomingMessage(IncomingMessageReqDTO incomingMessageReqDTO);
}
