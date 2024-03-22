package com.tfjt.pay.external.unionpay.api.service.impl;

import com.tfjt.pay.external.unionpay.api.dto.req.*;
import com.tfjt.pay.external.unionpay.api.dto.resp.*;
import com.tfjt.pay.external.unionpay.api.service.IncomingApiService;
import com.tfjt.pay.external.unionpay.biz.IncomingBizService;
import com.tfjt.pay.external.unionpay.biz.IncomingTtqfBizService;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/25 16:19
 * @description 进件信息服务
 */
@Slf4j
@DubboService
public class IncomingApiServiceImpl implements IncomingApiService {

    @Autowired
    private IncomingBizService incomingBizService;

    @Autowired
    private IncomingTtqfBizService incomingTtqfBizService;

    /**
     * 根据商户信息查询进件信息
     * @param incomingMessageReqDTO
     * @return
     */
    @Override
    public Result<IncomingMessageRespDTO> queryIncomingMessage(IncomingMessageReqDTO incomingMessageReqDTO) {
        return incomingBizService.queryIncomingMessage(incomingMessageReqDTO);
    }

    @Override
    public Result<Map<String, IncomingMessageRespDTO>> queryIncomingMessages(List<IncomingMessageReqDTO> incomingMessageReqs) {
        return incomingBizService.queryIncomingMessages(incomingMessageReqs);
    }

    @Override
    public Result<Map<String, IncomingStatusRespDTO>> queryIncomingStatus(IncomingStatusReqDTO incomingStatusReq) {
        return incomingBizService.queryIncomingStatus(incomingStatusReq);
    }

    @Override
    public Result<TtqfSignRespDTO> ttqfSign(TtqfSignReqDTO ttqfSignReqDTO) {
        return incomingBizService.ttqfSign(ttqfSignReqDTO);
    }

    @Override
    public Result<TtqfContractRespDTO> ttqfContract(TtqfContractReqDTO ttqfContractReqDTO) {
        return incomingTtqfBizService.ttqfContract(ttqfContractReqDTO);
    }

    @Override
    public Result<QueryTtqfSignMsgRespDTO> queryTtqfSignMsg(QueryTtqfSignMsgReqDTO queryTtqfSignMsgReqDTO) {
        return incomingTtqfBizService.queryTtqfSignMsg(queryTtqfSignMsgReqDTO);
    }
}
