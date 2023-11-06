package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 贷款-银联对账记录表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-10-28 11:17:26
 */
@Mapper
public interface LoanUnionpayCheckBillDetailsDao extends BaseMapper<LoanUnionpayCheckBillDetailsEntity> {
	
}
