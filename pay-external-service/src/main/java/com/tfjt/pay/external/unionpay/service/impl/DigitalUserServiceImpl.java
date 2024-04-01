package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.dao.DigitalUserDao;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.entity.DigitalUserEntity;
import com.tfjt.pay.external.unionpay.service.DigitalUserService;


/**
 * @author songx
 */
@Service("digitalUserService")
public class DigitalUserServiceImpl extends ServiceImpl<DigitalUserDao, DigitalUserEntity> implements DigitalUserService {


    @Override
    public DigitalUserEntity selectUserBySignContract(String signContract) {
        return this.getOne(Wrappers.<DigitalUserEntity>lambdaUpdate().eq(DigitalUserEntity::getSignContract,signContract));
    }
}