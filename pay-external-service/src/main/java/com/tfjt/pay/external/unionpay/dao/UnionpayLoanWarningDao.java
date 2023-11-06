package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.UnionpayLoanWarningEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 银联贷款交易明细报警表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-10-28 15:33:05
 */
@Mapper
public interface UnionpayLoanWarningDao extends BaseMapper<UnionpayLoanWarningEntity> {
	
}
