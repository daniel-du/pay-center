package com.tfjt.pay.external.query.api.service;

import com.tfjt.pay.external.query.api.dto.req.QueryIncomingStatusReqDTO;
import com.tfjt.pay.external.query.api.dto.resp.QueryIncomingStatusRespDTO;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.List;
import java.util.Map;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/2/29 15:15
 * @description 进件信息查询
 */
public interface IncomingQueryApiService {

    /**
     * 根据商户id、商户类型、区域（单个）查询入网状态
     * @param queryIncomingStatusReqDTO
     * @return
     */
    Result<QueryIncomingStatusRespDTO> queryIncomingStatus(QueryIncomingStatusReqDTO queryIncomingStatusReqDTO);

    /**
     * 根据商户id、商户类型、区域（单个）批量查询入网状态,结果集放入map，key为“入网渠道”-“商户类型”-“商户id”
     * @param queryIncomingStatusReqDTOS
     * @return
     */
    Result<Map<String, QueryIncomingStatusRespDTO>> batchQueryIncomingStatus(List<QueryIncomingStatusReqDTO> queryIncomingStatusReqDTOS);

    /**
     * 根据商户id、商户类型、区域（多个）查询入网状态
     * @param queryIncomingStatusReqDTO
     * @return
     */
    Result<QueryIncomingStatusRespDTO> queryIncomingStatusByAreaCodes(QueryIncomingStatusReqDTO queryIncomingStatusReqDTO);



}
