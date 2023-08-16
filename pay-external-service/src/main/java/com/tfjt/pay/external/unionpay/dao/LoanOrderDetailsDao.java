package com.tfjt.pay.external.unionpay.dao;

import com.tfjt.pay.external.unionpay.entity.LoanOrderDetailsEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 贷款订单商户收款信息表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-16 16:42:20
 */
@Mapper
public interface LoanOrderDetailsDao extends BaseMapper<LoanOrderDetailsEntity> {
	
}
