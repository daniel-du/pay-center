package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceDivideDetailsEntity;

import java.util.List;

/**
 * 分账详情表
 *
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-14 16:05:56
 */
public interface LoanBalanceDivideDetailsService extends IService<LoanBalanceDivideDetailsEntity> {

    List<LoanBalanceDivideDetailsEntity> listByDivideId(Long id);
}

