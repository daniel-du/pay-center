package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.DigitalUserEntity;

/**
 * 数字人民币开通信息表
 *
 * @author songx
 * @email 598482054@163.com
 * @date 2023-11-28 17:03:59
 */
public interface DigitalUserService extends IService<DigitalUserEntity> {
    /**
     * 根据签约协议号查询数字人民币开通信息
     * @param signContract 签约协议号
     * @return 签约用户信息
     */
    DigitalUserEntity selectUserBySignContract(String signContract);
}

