package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.dto.LoanBalanceDto;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 贷款余额表
 * 
 * @author effine
 * @email iballad@163.com
 * @date 2023-06-06 16:02:09
 */
@Mapper
public interface LoanBalanceDao extends BaseMapper<LoanBalanceEntity> {
    LoanBalanceDto getByShopId(Integer shopId);
}
