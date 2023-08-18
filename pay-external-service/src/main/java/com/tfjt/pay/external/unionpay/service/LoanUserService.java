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


    void applicationStatusUpdateJob(String jobParam);

    LoanUserEntity getBySettleAcctId(String settleAcctId);

    Result<?>  updateLoanUserDto(TfLoanUserEntityDTO tfLoanUserEntity);

    /**
     * 异步同步业务表
     * @param tfLoanUserEntity
     */
    void asynNotice(LoanUserEntity tfLoanUserEntity);

    List<UnionPayLoanUserRespDTO> listLoanUserByBusId(String type, List<String> busIds);
}

