package com.tfjt.pay.external.unionpay.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.LoanCallbackApplicationDao;
import com.tfjt.pay.external.unionpay.entity.LoanCallbackApplicationEntity;
import com.tfjt.pay.external.unionpay.service.LoanCallbackApplicationService;


@Service("loanCallbackApplicationService")
public class LoanCallbackApplicationServiceImpl extends ServiceImpl<LoanCallbackApplicationDao, LoanCallbackApplicationEntity> implements LoanCallbackApplicationService {


}