package com.tfjt.pay.external.unionpay.service.impl;


import com.tfjt.pay.external.unionpay.dao.BankDao;
import com.tfjt.pay.external.unionpay.entity.BankEntity;
import com.tfjt.pay.external.unionpay.service.BankService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BankServiceImpl extends BaseServiceImpl<BankDao, BankEntity> implements BankService {

}