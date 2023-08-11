package com.tfjt.pay.external.api;

import com.tfjt.pay.external.dto.LoanTransferToTfDTO;
import com.tfjt.tfcommon.dto.response.Result;

/**
 * @author songx
 * @date 2023-08-11 10:06
 * @email 598482054@qq.com
 */
public interface LoanApiService {

    Result<LoanTransferToTfDTO> getBalanceAcctId(String type, String bid);

}
