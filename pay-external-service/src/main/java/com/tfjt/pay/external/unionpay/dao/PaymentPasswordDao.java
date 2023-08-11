package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.PaymentPasswordEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentPasswordDao extends BaseMapper<PaymentPasswordEntity> {
}
