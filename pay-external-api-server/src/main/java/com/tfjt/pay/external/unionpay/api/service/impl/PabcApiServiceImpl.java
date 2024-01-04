package com.tfjt.pay.external.unionpay.api.service.impl;

import com.tfjt.pay.external.unionpay.api.dto.req.BusinessInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.BusinessChangeRecodRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.BusinessInfoRespDTO;
import com.tfjt.pay.external.unionpay.api.service.PabcApiService;
import com.tfjt.tfcommon.dto.response.Paged;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author zxy
 * @create 2023/12/13 10:42
 */
@Slf4j
@DubboService
public class PabcApiServiceImpl implements PabcApiService {

    /**
     * 查询商户信息详情
     * @param businessInfoReqDTO 查询入参
     * @return
     */
    @Override
    public Result<BusinessInfoRespDTO> getBusinessInfo(BusinessInfoReqDTO businessInfoReqDTO) {
        String buisnessId = businessInfoReqDTO.getBuisnessId();
        String businessType = businessInfoReqDTO.getBusinessType();
        return null;
    }

    /**
     * 查询变更信息
     * @param businessInfoReqDTO 查询入参
     * @return
     */
    @Override
    public Result<Paged<BusinessChangeRecodRespDTO>> getChangeRecord(BusinessInfoReqDTO businessInfoReqDTO) {
        return null;
    }

    @Override
    public Result<Integer> getNetworkTypeByAreaCode(String code) {
        return null;
    }
}
