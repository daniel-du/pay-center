package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.CustBusinessCreateDto;
import com.tfjt.pay.external.unionpay.entity.CustBusinessInfoEntity;

import java.util.List;

/**
 * 经营信息
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-20 09:27:38
 */
public interface CustBusinessInfoService extends IService<CustBusinessInfoEntity> {

    List<CustBusinessCreateDto> getBusinessAttach(String loanUserId);
}

