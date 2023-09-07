package com.tfjt.pay.external.unionpay.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserKeyInformationChangeRecordLog;


/**
 * 进件用户关键信息变更记录表
 *
 * @author zxy
 * @date 2023-09-07 09:58
 */
public interface LoanUserKeyInformationChangeRecordLogService extends IService<LoanUserKeyInformationChangeRecordLog> {

    void saveLog(Long id, String outRequestNo, String mchApplicationId, String settleAcctId, LoanUserEntity tfLoanUserEntityOld);
}

