package com.tfjt.pay.external.unionpay.api.service;

import com.tfjt.pay.external.unionpay.api.dto.req.AllIncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.AllIncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingStatusRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.*;
import com.tfjt.pay.external.unionpay.api.dto.resp.*;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.List;
import java.util.Map;

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

    /**
     * 根据多个商户信息批量查询进件信息，结果集放入map，key为“入网渠道”-“商户类型”-“商户id”
     * @param incomingMessageReqs
     * @return
     */
    Result<Map<String, IncomingMessageRespDTO>> queryIncomingMessages(List<IncomingMessageReqDTO> incomingMessageReqs);

    /**
     * 根据多个商户信息批量查询入网状态（一个渠道入网成功即算入网成功），key为“商户类型”-“商户id”
     * @param incomingStatusReqs
     * @return
     */
    Result<Map<String, IncomingStatusRespDTO>> queryIncomingStatus(IncomingStatusReqDTO incomingStatusReqs);

    /**
     * 根据商户id、商户类型查询所有渠道入网信息
     * @param reqDTO
     * @return
     */
    Result<List<AllIncomingMessageRespDTO>> queryAllIncomingMessage(AllIncomingMessageReqDTO reqDTO);
    /**
     * 天天企赋-商户签约
     * @param ttqfSignReqDTO
     * @return
     */
    Result<TtqfSignRespDTO> ttqfSign(TtqfSignReqDTO ttqfSignReqDTO);

    /**
     * 天天企赋-手签H5唤起
     * @param ttqfContractReqDTO
     * @return
     */
    Result<TtqfContractRespDTO> ttqfContract(TtqfContractReqDTO ttqfContractReqDTO);

    /**
     * 天天企赋-查询签约信息
     * @param queryTtqfSignMsgReqDTO
     * @return
     */
    Result<QueryTtqfSignMsgRespDTO> queryTtqfSignMsg(QueryTtqfSignMsgReqDTO queryTtqfSignMsgReqDTO);


}
