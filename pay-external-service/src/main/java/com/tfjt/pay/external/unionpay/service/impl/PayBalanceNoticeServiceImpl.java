package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.PayBalanceNoticeDao;
import com.tfjt.pay.external.unionpay.entity.PayBalanceNoticeEntity;
import com.tfjt.pay.external.unionpay.service.PayBalanceNoticeService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;



@Service("payBanlanceNoticeService")
public class PayBalanceNoticeServiceImpl extends ServiceImpl<PayBalanceNoticeDao, PayBalanceNoticeEntity> implements PayBalanceNoticeService {

}