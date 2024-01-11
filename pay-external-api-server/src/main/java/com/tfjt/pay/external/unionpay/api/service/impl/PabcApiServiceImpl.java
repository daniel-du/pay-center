package com.tfjt.pay.external.unionpay.api.service.impl;

import com.tfjt.pay.external.unionpay.api.dto.req.BusinessInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.QueryAccessBankStatueReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.BusinessChangeRecodRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryAccessBankStatueRespDTO;
import com.tfjt.pay.external.unionpay.api.service.PabcApiService;
import com.tfjt.pay.external.unionpay.biz.PabcBizService;
import com.tfjt.tfcommon.dto.response.Paged;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/13 10:42
 */
@Slf4j
@DubboService
public class PabcApiServiceImpl implements PabcApiService {

    @Autowired
    private PabcBizService pabcBizService;



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
        return pabcBizService.getNetworkTypeByAreaCode(code);
    }

    @Override
    public Result<List<QueryAccessBankStatueRespDTO>> getNetworkStatus(QueryAccessBankStatueReqDTO queryAccessBankStatueReqDTO) {
        return pabcBizService.getNetworkStatus(queryAccessBankStatueReqDTO);
    }
}
