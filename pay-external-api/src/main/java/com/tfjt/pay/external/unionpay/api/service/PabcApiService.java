package com.tfjt.pay.external.unionpay.api.service;

import com.tfjt.pay.external.unionpay.api.dto.req.BusinessBasicInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.BusinessInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.QueryAccessBankStatueReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.AllSalesAreaRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.PayChannelRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryAccessBankStatueRespDTO;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/13 10:38
 */
public interface PabcApiService {


    Result<Integer> getNetworkTypeByAreaCode(String code);

    Result<List<QueryAccessBankStatueRespDTO>> getNetworkStatus( QueryAccessBankStatueReqDTO queryAccessBankStatueReqDTO);


    Result<Integer> getNetworkTypeByAreaCode(List<String> code);

    Result<IncomingMessageRespDTO>  getIncomingInfo(BusinessInfoReqDTO businessInfoReqDTO);


    Result<List<PayChannelRespDTO>> getAllSaleAreas(Integer areaLevel, String distinctName);

    Result<List<AllSalesAreaRespDTO>> getAllSaleAreas();

    Result<Boolean> isIncomingByBusinessIdAndType(List<BusinessBasicInfoReqDTO> dtos);


}
