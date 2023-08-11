package com.tfjt.pay.external.unionpay.api.service;

import com.tfjt.tfcommon.dto.response.Result;

import java.util.Map;

/**
 * @author songx
 * @date 2023-08-11 10:06
 * @email 598482054@qq.com
 */
public interface LoanApiService {

    Result<LoanTransferToTfDTO> getBalanceAcctId(String type, String bid);

    Result<Map<String,Object>> incomingIsFinish(String type, String bid);

}
