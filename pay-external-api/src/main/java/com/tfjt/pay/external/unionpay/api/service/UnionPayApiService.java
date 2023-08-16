package com.tfjt.pay.external.unionpay.api.service;

import com.tfjt.pay.external.unionpay.api.dto.req.BalanceDivideReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.LoanOrderUnifiedorderDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.BalanceAcctDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.SubBalanceDivideRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.UnionPayTransferDTO;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.List;
import java.util.Map;

/**
 * 银联dubbo接口层
 * @author songx
 * @date 2023-08-10 17:02
 * @email 598482054@qq.com
 */
public interface UnionPayApiService {
    /**
     * 转账接口
     * @param payTransferDTO
     */
    Result<String> transfer(UnionPayTransferDTO payTransferDTO);

    /**
     * 获取同福母账户当前账户余额
     * @return
     */
    Result<Integer> currentBalance();


    /**
     * 获取指定电子账簿id的账户信息
     * @param balanceAcctId 电子账簿id
     * @return
     */
    Result<BalanceAcctDTO> getBalanceByAccountId(String balanceAcctId);

    /**
     * 批量获取指定电子账簿id的账户信息
     * @return
     */
    Result<Map<String,BalanceAcctDTO>> listBalanceByAccountIds(List<String> balanceAcctIds);

    /**
     * 资金分账操作
     * @param balanceDivideReq
     * @return
     */
    Result<Map<String, SubBalanceDivideRespDTO>> balanceDivide(BalanceDivideReqDTO balanceDivideReq);

    /**
     *  下单接口
     * @param loanOrderUnifiedorderDTO
     * @return
     */
    Result unifiedorder(LoanOrderUnifiedorderDTO loanOrderUnifiedorderDTO);

}
