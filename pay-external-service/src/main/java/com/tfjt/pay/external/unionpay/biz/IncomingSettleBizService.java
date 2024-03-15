package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.req.IncomingSettleReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingSettleRespDTO;
import com.tfjt.tfcommon.dto.response.Result;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/11 16:08
 * @description 进件-商户结算信息服务
 */
public interface IncomingSettleBizService {

    /**
     * 根据id查询商户结算信息
     * @param id
     * @return
     */
    Result<IncomingSettleRespDTO> getById(Long id);

    /**
     * 保存商户结算信息
     * @param incomingSettleReqDTO
     * @return
     */
    Result save(IncomingSettleReqDTO incomingSettleReqDTO);

    /**
     * 修改商户结算信息
     * @param incomingSettleReqDTO
     * @return
     */
    Result update(IncomingSettleReqDTO incomingSettleReqDTO);
}
