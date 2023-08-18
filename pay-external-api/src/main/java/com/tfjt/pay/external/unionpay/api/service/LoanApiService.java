package com.tfjt.pay.external.unionpay.api.service;

import com.tfjt.pay.external.unionpay.api.dto.resp.LoanTransferToTfRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.CustBankInfoRespDTO;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.List;
import java.util.Map;

/**
 * @author songx
 * @date 2023-08-11 10:06
 * @email 598482054@qq.com
 */
public interface LoanApiService {
    /**
     * 获取向同福转账的账户余额与同福母账号信息
     * @param type  用户类型
     * @param bid  业务id
     * @return  用户余额与同福收款账号信息
     */
    Result<LoanTransferToTfRespDTO> getBalanceAcctId(String type, String bid);

    Result<Map<String,Object>> incomingIsFinish(String type, String bid);

    /**
     * 通过贷款用户ID获取银行卡
     * @param loanUserId
     * @return
     */
    Result<List<CustBankInfoRespDTO>> getCustBankInfoList(Long loanUserId);

}
