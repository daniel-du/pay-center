package com.tfjt.pay.external.unionpay.api.service;

import com.tfjt.pay.external.unionpay.api.dto.req.BusinessInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.QueryAccessBankStatueReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.BusinessChangeRecodRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryAccessBankStatueRespDTO;
import com.tfjt.tfcommon.dto.response.Paged;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/13 10:38
 */
public interface PabcApiService {
    /**
     * 查询商户信息详情
     */
    Result<Paged<BusinessChangeRecodRespDTO>> getChangeRecord(BusinessInfoReqDTO businessInfoReqDTO);


    Result<Integer> getNetworkTypeByAreaCode(String code);

    Result<List<QueryAccessBankStatueRespDTO>> getNetworkStatus( QueryAccessBankStatueReqDTO queryAccessBankStatueReqDTO);


}
