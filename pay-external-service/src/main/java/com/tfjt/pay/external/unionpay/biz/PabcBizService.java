package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.entity.AsyncMessageEntity;
import com.tfjt.pay.external.unionpay.api.dto.req.BusinessBasicInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.BusinessInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingModuleStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.AllSalesAreaRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.PayChannelRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryAccessBankStatueRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.QueryAccessBankStatueReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.*;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 16:04
 */
public interface PabcBizService {
    Result<List<PabcBankNameAndCodeRespDTO>> getBankInfoByName(String name);

    Result<List<PabcProvinceInfoRespDTO>> getProvinceList(String name);

    Result<List<PabcCityInfoRespDTO>> getCityList(String provinceCode, String bankCode);

    Result<List<PabcBranchBankInfoRespDTO>> getBranchBankInfo(String bankCode, String cityCode, String branchBankName);

    Result<List<QueryAccessBankStatueRespDTO>> getNetworkStatus(QueryAccessBankStatueReqDTO queryAccessBankStatueReqDTO);

    Result<Integer> getNetworkTypeByAreaCode(String code);

    Result<MoudleStatusRespDTO> getModuleStatus(IncomingModuleStatusReqDTO incomingModuleStatusReqDTO);

    com.tfjt.dto.response.Result<String> saveChangeInfo(AsyncMessageEntity message);

    Result<Integer> getNetworkTypeByAreaCode(List<String> code);


    IncomingMessageRespDTO getIncomingInfo(BusinessInfoReqDTO businessInfoReqDTO);

    List<PayChannelRespDTO> getAllSaleAreas(Integer areaLevel, String distinctName);

    List<AllSalesAreaRespDTO> getAllSaleAreas();

    Boolean isIncomingByBusinessIdAndType(List<BusinessBasicInfoReqDTO> dtos);
}
