package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.req.BankInfoReqDTO;

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
    public boolean bindSettleAcct(BankInfoReqDTO  BankInfoReqDTO);
}
