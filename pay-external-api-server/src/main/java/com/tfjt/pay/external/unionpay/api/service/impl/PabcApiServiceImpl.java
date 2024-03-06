package com.tfjt.pay.external.unionpay.api.service.impl;

import com.tfjt.pay.external.unionpay.api.dto.req.BusinessBasicInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.BusinessInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.QueryAccessBankStatueReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.AllSalesAreaRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.PayChannelRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryAccessBankStatueRespDTO;
import com.tfjt.pay.external.unionpay.api.service.PabcApiService;
import com.tfjt.pay.external.unionpay.biz.PabcBizService;
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



    @Override
    public Result<Integer> getNetworkTypeByAreaCode(String code) {
        return pabcBizService.getNetworkTypeByAreaCode(code);
    }

    @Override
    public Result<List<QueryAccessBankStatueRespDTO>> getNetworkStatus(QueryAccessBankStatueReqDTO queryAccessBankStatueReqDTO) {
        return pabcBizService.getNetworkStatus(queryAccessBankStatueReqDTO);
    }

    @Override
    public Result<Integer> getNetworkTypeByAreaCode(List<String> code) {
        return pabcBizService.getNetworkTypeByAreaCode(code);
    }

    @Override
    public Result<IncomingMessageRespDTO> getIncomingInfo(BusinessInfoReqDTO businessInfoReqDTO) {

        IncomingMessageRespDTO respDTO = pabcBizService.getIncomingInfo(businessInfoReqDTO);
        return Result.ok(respDTO);
    }

    @Override
    public Result<List<PayChannelRespDTO>> getAllSaleAreas(Integer areaLevel, String distinctName) {
        log.info("获取省市区下拉列表参数areaLevel:{},distinctName:{}",areaLevel,distinctName);
        return Result.ok(pabcBizService.getAllSaleAreas(areaLevel,distinctName));
    }

    @Override
    public Result<List<AllSalesAreaRespDTO>> getAllSaleAreas() {
        return Result.ok(pabcBizService.getAllSaleAreas());
    }

    @Override
    public Result<Boolean> isIncomingByBusinessIdAndType(List<BusinessBasicInfoReqDTO> dtos) {
        return Result.ok(pabcBizService.isIncomingByBusinessIdAndType(dtos));
    }
}
