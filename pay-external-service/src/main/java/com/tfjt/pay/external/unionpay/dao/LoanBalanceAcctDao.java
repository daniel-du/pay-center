package com.tfjt.pay.external.unionpay.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceAcctEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 贷款用户电子账单
 * 
 * @author effine
 * @email iballad@163.com
 * @date 2023-05-23 08:48:02
 */
@Mapper
public interface LoanBalanceAcctDao extends BaseMapper<LoanBalanceAcctEntity> {

    LoanBalanceAcctEntity getBalanceAcctIdByBidAndType(@Param("busId") String busId, @Param("type") String type);
}
