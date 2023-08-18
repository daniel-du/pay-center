package com.tfjt.pay.external.unionpay.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.PayApplicationCallbackUrlDao;
import com.tfjt.pay.external.unionpay.entity.PayApplicationCallbackUrlEntity;
import com.tfjt.pay.external.unionpay.service.PayApplicationCallbackUrlService;


@Service("payApplicationCallbackUrlService")
public class PayApplicationCallbackUrlServiceImpl extends ServiceImpl<PayApplicationCallbackUrlDao, PayApplicationCallbackUrlEntity> implements PayApplicationCallbackUrlService {

}