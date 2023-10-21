package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.api.dto.req.WithdrawalReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.BankInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.WithdrawalRespDTO;
import com.tfjt.tfcommon.dto.response.Result;

/**
 * 进件业务层接口
 */
public interface UnionPayLoansBizService {

    /**
     * 解除绑定银行卡
     *
     * @param deleteSettleAcctParams
     * @return
     */
    public void unbindSettleAcct(BankInfoReqDTO BankInfoReqDTO);

    /**
     * 绑定银行卡
     * @param BankInfoReqDTO
     * @return
     */
     String bindSettleAcct(BankInfoReqDTO  BankInfoReqDTO);

    /**
     * 提现
     * @param withdrawalReqDTO
     * @return
     */
    Result<WithdrawalRespDTO> withdrawalCreation(WithdrawalReqDTO withdrawalReqDTO);

}
