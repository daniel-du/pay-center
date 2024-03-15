package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.req.IncomingMerchantReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingMerchantRespDTO;
import com.tfjt.tfcommon.dto.response.Result;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/11 16:08
 * @description 进件-商户身份信息服务
 */
public interface IncomingMerchantBizService {

    /**
     * 根据id查询商户身份信息
     * @param id
     * @return
     */
    Result<IncomingMerchantRespDTO> getById(Long id);

    /**
     * 保存商户身份信息
     * @param incomingMerchantReqDTO
     * @return
     */
    Result save(IncomingMerchantReqDTO incomingMerchantReqDTO);

    /**
     * 修改商户身份信息
     * @param incomingMerchantReqDTO
     * @return
     */
    Result update(IncomingMerchantReqDTO incomingMerchantReqDTO);
}
