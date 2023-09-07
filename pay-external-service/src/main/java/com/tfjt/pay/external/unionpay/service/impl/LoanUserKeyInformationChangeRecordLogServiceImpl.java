package com.tfjt.pay.external.unionpay.service.impl;



import com.tfjt.pay.external.unionpay.dao.LoanUserKeyInformationChangeRecordLogDao;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserKeyInformationChangeRecordLog;
import com.tfjt.pay.external.unionpay.service.LoanUserKeyInformationChangeRecordLogService;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;


@Service
@Slf4j
public class LoanUserKeyInformationChangeRecordLogServiceImpl extends BaseServiceImpl<LoanUserKeyInformationChangeRecordLogDao, LoanUserKeyInformationChangeRecordLog> implements LoanUserKeyInformationChangeRecordLogService {

    @Override
    @Async
    public void saveLog(Long id, String outRequestNo, String mchApplicationId, String settleAcctId, LoanUserEntity tfLoanUserEntityOld) {



        LoanUserKeyInformationChangeRecordLog recordLog = new LoanUserKeyInformationChangeRecordLog();
        recordLog.setLoanUserId(id);
        Boolean flag = false;
        if (StringUtil.isNotBlank(outRequestNo)) {
            String outRequestNoOld = tfLoanUserEntityOld.getOutRequestNo();
            if (!outRequestNo.equals(outRequestNoOld)) {
                flag = true;
                recordLog.setOutRequestNo(outRequestNo);
            }
        }
        if (StringUtil.isNotBlank(mchApplicationId)) {
            String mchApplicationIdOld = tfLoanUserEntityOld.getMchApplicationId();
            if (!mchApplicationId.equals(mchApplicationIdOld)) {
                flag = true;
                recordLog.setMchApplicationId(mchApplicationId);
            }
        }
        if (StringUtil.isNotBlank(settleAcctId)) {
            String settleAcctIdOld = tfLoanUserEntityOld.getSettleAcctId();
            if (!settleAcctId.equals(settleAcctIdOld)) {
                flag = true;
                recordLog.setSettleAcctId(settleAcctId);
            }
        }
        if (flag) {
            this.save(recordLog);
        }
    }
}
