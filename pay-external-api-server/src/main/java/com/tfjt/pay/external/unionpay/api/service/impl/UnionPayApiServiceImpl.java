package com.tfjt.pay.external.unionpay.api.service.impl;

import com.tfjt.pay.external.unionpay.api.dto.req.*;
import com.tfjt.pay.external.unionpay.api.dto.resp.*;
import com.tfjt.pay.external.unionpay.api.service.UnionPayApiService;
import com.tfjt.pay.external.unionpay.biz.*;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.*;

/**
 * 银联接口服务实现类
 *
 * @author songx
 * @date 2023-08-10 17:02
 * @email 598482054@qq.com
 */
@Slf4j
@DubboService
public class UnionPayApiServiceImpl implements UnionPayApiService {
    @Resource
    private LoanUnionPayCheckBillBizService loanUnionPayCheckBillBiz;
    @Resource
    private LoanOrderBizService loanOrderBiz;
    @Resource
    private PayBalanceDivideBizService payBalanceDivideBiz;

    @Resource
    private LoanUserBizService loanUserBizService;
    @Resource
    private UnionPayLoansBizService unionPayLoansBizService;

    /**
     * 转账交易接口
     * @param payTransferDTO 转账参数
     * @return  转账结果
     */
    @Override
    public Result<String> transfer(UnionPayTransferRespDTO payTransferDTO) {
        return loanOrderBiz.transfer(payTransferDTO);
    }

    /**
     * 获取同福母账户信息
     * @return 获取同福母账户信息
     */
    @Override
    public Result<Integer> currentBalance() {
        return loanUserBizService.currentBalance();
    }

    @Override
    public Result<ParentBalanceRespDTO> currentBalanceInfo() {
        return loanUserBizService.currentBalanceInfo();
    }

    /**
     * 获取指定电子账簿的账户信息
     * @param balanceAcctId 电子账簿id
     * @return 电子账户信息
     */
    @Override
    public Result<BalanceAcctRespDTO> getBalanceByAccountId(String balanceAcctId) {
        return loanUserBizService.getBalanceByAccountId(balanceAcctId);
    }

    /**
     * 获取电子账簿列表
     * @param balanceAcctIds 电子账簿ids
     * @return 账簿列表
     */
    @Override
    public Result<Map<String, BalanceAcctRespDTO>> listBalanceByAccountIds(List<String> balanceAcctIds) {
        return loanUserBizService.listBalanceByAccountIds(balanceAcctIds);
    }

    /**
     * 分账参数
     * @param balanceDivideReq 分账参数
     * @return 分账结果
     */
    @Override
    public Result<Map<String, SubBalanceDivideRespDTO>> balanceDivide(UnionPayBalanceDivideReqDTO balanceDivideReq) {
        return payBalanceDivideBiz.balanceDivide(balanceDivideReq);
    }


    /**
     * 提现
     * @param withdrawalReqDTO 提现参数
     * @return 提现结果
     */
    @Override
    public Result<WithdrawalRespDTO> withdrawalCreation(WithdrawalReqDTO withdrawalReqDTO) {
        return this.unionPayLoansBizService.withdrawalCreation(withdrawalReqDTO);
    }

    /**
     * 查询交易结果
     * @param businessOrderNo 交易订单号
     * @param appId 应用appid
     * @return 交易结果
     */
    @Override
    public Result<LoanQueryOrderRespDTO> orderQuery(String businessOrderNo, String appId) {
        return loanOrderBiz.orderQuery(businessOrderNo,appId);
    }

    /**
     * 担保下单结果
     * @param loanOrderUnifiedorderDTO  下单参数
     * @return 下单结果
     */
    @Override
    public Result<MergeConsumerRepDTO> unifiedorder(UnionPayLoanOrderUnifiedorderReqDTO loanOrderUnifiedorderDTO) {
        return this.loanOrderBiz.unifiedorder(loanOrderUnifiedorderDTO);
    }

    /**
     * 下载电子账单
     * @param unionPayCheckBillReqDTO 电子账单
     * @return 电子账单列表
     */
    @Override
    public Result<String> downloadCheckBill(UnionPayCheckBillReqDTO unionPayCheckBillReqDTO) {
        return  loanUnionPayCheckBillBiz.downloadCheckBill(unionPayCheckBillReqDTO);
    }

}
