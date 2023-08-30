package com.tfjt.pay.external.unionpay.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.LoanUserInfoDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayLoanUserRespDTO;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.tfcloud.business.dto.TfLoanUserEntityDTO;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.List;

/**
 * 贷款-用户
 *
 * @author effine
 * @email iballad@163.com
 * @date 2023-05-20 11:23:12
 */
public interface LoanUserService extends IService<LoanUserEntity> {
    Result<?> saveLoanUser(LoanUserEntity loanUserEntity);

    Result<?> updateLoanUser(Long id,  Integer loanUserType);

    LoanUserInfoDTO getLoanUerInfo(Long loanUserId);

    /**
     *
     * @param busId 业务id
     * @param type 类型1商家2供应商
     * @return
     */
    LoanUserEntity getLoanUserByBusIdAndType(String busId, Integer type);


    void applicationStatusUpdateJob(String jobParam);

    LoanUserEntity getBySettleAcctId(String settleAcctId);

    Result<?>  updateLoanUserDto(TfLoanUserEntityDTO tfLoanUserEntity);

    /**
     * 异步同步业务表
     * @param tfLoanUserEntity
     */
    void asynNotice(LoanUserEntity tfLoanUserEntity);

    List<UnionPayLoanUserRespDTO> listLoanUserByBusId(String type, List<String> busIds);

    /**
     * 根据电子账簿id获取用户信息
     * @param balanceAcctId 电子账簿id
     */
    LoanUserEntity getByBalanceAcctId(String balanceAcctId);

    Integer getBankCallStatus(Long loanUserId);

    /**
     * 根据电子账簿id获取用户id
     * @param balanceAcctId 电子账簿id
     * @return 用户id
     */
    Long getLoanUserIdByBalanceAccId(String balanceAcctId);

    /**
     * 账号信息是否正确
     * @param balanceAcctId 电子账户id
     * @param totalAmount   检验当前电子账户金额是否大于 totalAmount金额
     * @param balanceAcctName 电子账户名称
     */
    void checkLoanAccount(String balanceAcctId, Integer totalAmount, String balanceAcctName);
}

