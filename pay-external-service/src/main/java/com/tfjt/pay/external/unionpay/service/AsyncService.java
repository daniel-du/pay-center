package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.query.api.dto.req.QueryIncomingStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.BusinessBasicInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.dto.BusinessIsIncomingRespDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;

import java.util.List;
import java.util.Map;

/**
 * @Author zxy
 * @create 2024/1/16 9:48
 */
public interface AsyncService {

    public void dingWarning(Long supplierId, List<Integer> newIdentifyList, List<String> newSaleAreas, Boolean saleFlag, Boolean identityFlag, List<String> oldSaleAreas, List<Integer> oldIdentifyList);

    void dingWarning(List<BusinessBasicInfoReqDTO> dtos, List<BusinessIsIncomingRespDTO> businessList);


}
