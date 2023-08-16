package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.LoadBalanceDivideEntity;


/**
 * 分账记录表
 *
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-14 16:05:56
 */
public interface LoanBalanceDivideService extends IService<LoadBalanceDivideEntity> {
    /**
     * 查询主交易单号是否存在
     * @param businessOrderNo 主交易单号
     * @return  true 已存在 false  不存在
     */
    boolean checkExistBusinessOrderNo(String businessOrderNo);
}

