package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.CustHoldingCreateDto;
import com.tfjt.pay.external.unionpay.dto.CustHoldingDeleteDto;
import com.tfjt.pay.external.unionpay.entity.CustHoldingEntity;

/**
 * 控股信息表
 *
 * @author effine
 * @email iballad@163.com
 * @date 2023-06-05 15:10:56
 */
public interface CustHoldingService extends IService<CustHoldingEntity> {
    CustHoldingEntity getByLoanUserId(Long id);
    Long save(CustHoldingCreateDto dto);
    Long update(CustHoldingCreateDto dto);
    Long delete(CustHoldingDeleteDto dto);
}

