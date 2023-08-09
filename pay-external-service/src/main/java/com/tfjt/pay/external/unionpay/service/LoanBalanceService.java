package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceEntity;
import com.tfjt.pay.external.unionpay.dto.LoanBalanceCreateDto;

/**
 * 贷款余额表
 *
 * @author effine
 * @email iballad@163.com
 * @date 2023-06-06 16:02:09
 */
public interface LoanBalanceService extends IService<LoanBalanceEntity> {
    LoanBalanceEntity getByShopId(Integer shopId);
    void update(LoanBalanceCreateDto dto);
}

