package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.req.QueryAccessBankStatueReqDTO;
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

}
