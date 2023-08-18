package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;

import java.util.Map;

/**
 * 贷款订单表
 *
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-16 16:42:20
 */
public interface LoanOrderService extends IService<LoanOrderEntity> {
    /**
     * 检验业务单据是否已经存在
     * @param businessOrderNo 业务号
     * @param appId  业务系统id
     * @return  true 已存在 false 不存在
     */
    boolean checkExistBusinessOrderNo(String businessOrderNo, String appId);
}

