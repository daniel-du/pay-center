package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.api.dto.req.QueryTtqfSignMsgReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.TtqfContractReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryTtqfSignMsgRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.TtqfContractRespDTO;
import com.tfjt.tfcommon.dto.response.Result;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/21 17:12
 * @description
 */
public interface IncomingTtqfBizService {

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

    /**
     * 批量更新天天企赋签约状态
     */
    void updateTtqfSignStatus();
}
