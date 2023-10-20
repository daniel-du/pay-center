package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.api.dto.UserTypeDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.PaymentPasswordReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayIncomingDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.*;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.List;
import java.util.Map;

public interface LoanUserBizService {
    void applicationStatusUpdateJob(String jobParam);

    /**
     * 设置支付密码
     * @param paymentPasswordDTO
     * @return
     */
    public Result<String> savePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO);

    /**
     * 更新支付密码
     * @param paymentPasswordDTO
     * @return
     */
    public Result<String> updatePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO);

    /**
     *
     * @param userType
     * @return
     */
    public Result<String> getSalt(UserTypeDTO userType);
    /**
     * 判断密码是否存在
     */
    public Result<Boolean> isExist(UserTypeDTO userType);

    /**
     * 验证支付密码
     * @param paymentPasswordDTO
     * @return
     */
    public Result<Boolean> verifyPassword(PaymentPasswordReqDTO paymentPasswordDTO);

    /**
     * 获取同福电子账户信息
     * @return 同福电子账簿信息
     */
    Result<Integer> currentBalance();
    Result<ParentBalanceRespDTO> currentBalanceInfo();

    /**
     * 根据电子账户获取账户信息
     * @param balanceAcctId 电子账簿id
     * @return  电子账簿信息
     */
    Result<BalanceAcctRespDTO> getBalanceByAccountId(String balanceAcctId);
    /**
     * 批量获取电子账户获取账户信息
     * @param balanceAcctIds 电子账簿id
     * @return  电子账簿信息
     */
    Result<Map<String, BalanceAcctRespDTO>> listBalanceByAccountIds(List<String> balanceAcctIds);

    /**
     * 根据类型与业务id获取电子账簿信息
     * @param type 1 商家 2 经销商
     * @param bid  业务id
     * @return 电子账簿信息
     */
    Result<LoanTransferToTfRespDTO> getBalanceAcctId(String type, String bid);

    /**
     * 判断指定的商户进件是否完成
     * @param type 1 商家 2 经销商
     * @param bid   业务id
     * @return 是否完成
     */
    Result<Map<String, Object>> incomingIsFinish(String type, String bid);

    /**
     * 批量判断商户是否完成进件
     * @param list 商户信息
     * @return
     */
    Result<Map<String, Object>> listIncomingIsFinish(List<UnionPayIncomingDTO> list);

    Result<List<CustBankInfoRespDTO>> getCustBankInfoList(Integer type, String bid);

    /**
     * 获取电子账簿信息
     * @param type 1 商家 2 经销商
     * @param busId  业务id
     * @return  账号信息
     */
    Result<BalanceAcctRespDTO> getAccountInfoByBusId(String type, String busId);

    /**
     * 批量获取电子账簿信息
     * @param type   1 商家 2  经销商
     * @param busIds  业务ids
     * @return  账号信息
     */
    Result<List<BalanceAcctRespDTO>> listAccountInfoByBusId(String type, List<String> busIds);

    LoanUserEntity getById(Long id);

    /**
     *
     * @param amount
     * @param orderNo
     * @return
     */
    Result<DepositRespDTO> deposit(Integer amount, String orderNo);
}
