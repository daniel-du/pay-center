package com.tfjt.pay.external.unionpay.api.service;

import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayIncomingDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.*;
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

    /**
     * 指定店铺是否完成进件
     * @param type 类型
     * @param bid  bid
     * @return refuan
     */
    Result<Map<String,Object>> incomingIsFinish(String type, String bid);

    /**
     * 批量获取是否进件 判断供应商和商家是否同时都完成进件
     * 并返回要支付商家的余额信息
     * @param list  判断列表
     * @return  是否可以使用贷款
     */
    Result<Map<String,Object>> listIncomingIsFinish(List<UnionPayIncomingDTO> list);


    /**
     * 通过贷款用户ID获取银行卡
     * @param type
     * @param bid 类型1商家2供应商
     * @return
     */
    Result<List<CustBankInfoRespDTO>> getCustBankInfoList(Integer type, String bid);


    /**
     * 业务id获取电子账户信息
     * @param busId 业务id
     * @return
     */
    Result<BalanceAcctRespDTO> getAccountInfoByBusId(String type,String busId);

    /**
     * 业务id获取电子账户信息
     * @param type 1 商家 2 经销商
     * @param busIds 业务ids
     * @return
     */
    Result<List<BalanceAcctRespDTO>> listAccountInfoByBusId(String type,List<String> busIds);

    /**
     * 解除绑定银行卡
     * @param bankInfoReqDTO
     * @return
     */
    Result<String> unbindSettleAcct(BankInfoReqDTO bankInfoReqDTO);

    /**
     * 绑定结算银行卡
     * @param bankInfoReqDTO
     * @return
     */
    Result<String> bindSettleAcct(BankInfoReqDTO bankInfoReqDTO);


    /**
     * 解绑母账户
     * @param loanUserId
     * @return
     */
    Result<String> unbindParentSettleAcct(Long loanUserId);
    /**
     * 打款验证
     * @param loanUserId
     * @param payAmount
     * @return
     */
    Result<UnionPayLoansSettleAcctDTO> settleAcctsValidate(Long loanUserId, Integer payAmount);

    /**
     * 获取验证状态
     * @param type
     * @param bid
     * @return
     */
    Result<String> getAcctValidateStatus(Integer type, String bid);

    Result<String> deposit(Integer amount,String orderNo);

    Result<List<BankCodeRespDTO>> getBankCodeByName(String bankName);

}
