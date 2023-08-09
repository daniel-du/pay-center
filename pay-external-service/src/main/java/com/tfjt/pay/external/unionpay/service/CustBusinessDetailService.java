package com.tfjt.pay.external.unionpay.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.CustBusinessCreateDto;
import com.tfjt.pay.external.unionpay.dto.CustBusinessDeleteDto;
import com.tfjt.pay.external.unionpay.entity.CustBusinessDetailEntity;
import com.tfjt.tfcommon.dto.response.Result;

/**
 * 营业信息表
 *
 * @author effine
 * @email iballad@163.com
 * @date 2023-06-05 15:06:04
 */
public interface CustBusinessDetailService extends IService<CustBusinessDetailEntity> {
    CustBusinessDetailEntity getByLoanUserId(Long id);

    Result<?> save(CustBusinessCreateDto dto);
    Result<?> update(CustBusinessCreateDto dto);

    Result<?> delete(CustBusinessDeleteDto dto);
}

