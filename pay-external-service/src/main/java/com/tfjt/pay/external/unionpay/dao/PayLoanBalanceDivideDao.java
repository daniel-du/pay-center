package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.LoadBalanceDivideEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分账记录表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-14 16:05:56
 */
@Mapper
public interface PayLoanBalanceDivideDao extends BaseMapper<LoadBalanceDivideEntity> {
	
}
