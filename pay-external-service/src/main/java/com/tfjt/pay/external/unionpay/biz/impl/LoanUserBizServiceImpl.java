package com.tfjt.pay.external.unionpay.biz.impl;

import com.tfjt.pay.external.unionpay.biz.LoanUserBizService;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoanUserBizServiceImpl implements LoanUserBizService {

    @Autowired
    private LoanUserService loanUserService;
    @Override
    public void applicationStatusUpdateJob(String jobParam) {
         loanUserService.applicationStatusUpdateJob(jobParam);
    }
}
