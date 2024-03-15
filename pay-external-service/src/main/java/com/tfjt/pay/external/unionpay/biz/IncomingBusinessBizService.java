package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.req.IncomingBusinessReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingMerchantReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingBusinessRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingMerchantRespDTO;
import com.tfjt.tfcommon.dto.response.Result;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/11 16:08
 * @description 进件-商户营业信息服务
 */
public interface IncomingBusinessBizService {

    /**
     * 根据id查询商户营业信息
     * @param id
     * @return
     */
    Result<IncomingBusinessRespDTO> getById(Long id);

    /**
     * 保存商户营业信息
     * @param incomingBusinessReqDTO
     * @return
     */
    Result save(IncomingBusinessReqDTO incomingBusinessReqDTO);

    /**
     * 修改商户营业信息
     * @param incomingBusinessReqDTO
     * @return
     */
    Result update(IncomingBusinessReqDTO incomingBusinessReqDTO);
}
