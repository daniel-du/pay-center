package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.resp.BankNameAndCodeRespDTO;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 16:04
 */
public interface PabcBizService {
    Result<List<BankNameAndCodeRespDTO>> getBankInfoByName(String name);
}
