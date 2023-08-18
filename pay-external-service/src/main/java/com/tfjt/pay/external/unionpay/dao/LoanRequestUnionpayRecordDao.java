package com.tfjt.pay.external.unionpay.dao;

import com.tfjt.pay.external.unionpay.entity.LoanRequestUnionpayRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 贷款-调用银联日志表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-18 20:59:10
 */
@Mapper
public interface LoanRequestUnionpayRecordDao extends BaseMapper<LoanRequestUnionpayRecordEntity> {
	
}
