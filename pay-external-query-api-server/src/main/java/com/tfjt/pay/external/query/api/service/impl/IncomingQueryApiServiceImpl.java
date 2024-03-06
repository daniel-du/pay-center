package com.tfjt.pay.external.query.api.service.impl;

import com.tfjt.pay.external.query.api.dto.req.QueryIncomingStatusReqDTO;
import com.tfjt.pay.external.query.api.dto.resp.QueryIncomingStatusRespDTO;
import com.tfjt.pay.external.query.api.service.IncomingQueryApiService;
import com.tfjt.pay.external.unionpay.biz.IncomingQueryBizService;
import com.tfjt.tfcommon.dto.response.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/4 14:24
 * @description
 */
@DubboService
public class IncomingQueryApiServiceImpl implements IncomingQueryApiService {

    @Autowired
    private IncomingQueryBizService incomingQueryBizService;

    @Override
    public Result<QueryIncomingStatusRespDTO> queryIncomingStatus(QueryIncomingStatusReqDTO queryIncomingStatusReqDTO) {
        return incomingQueryBizService.queryIncomingStatus(queryIncomingStatusReqDTO);
    }

    @Override
    public Result<Map<String, QueryIncomingStatusRespDTO>> batchQueryIncomingStatus(List<QueryIncomingStatusReqDTO> queryIncomingStatusReqDTOS) {
        return incomingQueryBizService.batchQueryIncomingStatus(queryIncomingStatusReqDTOS);
    }

    @Override
    public Result<QueryIncomingStatusRespDTO> queryIncomingStatusByAreaCodes(QueryIncomingStatusReqDTO queryIncomingStatusReqDTO) {
        return incomingQueryBizService.queryIncomingStatusByAreaCodes(queryIncomingStatusReqDTO);
    }
}
