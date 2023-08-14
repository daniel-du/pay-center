package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.PayBalanceDivideEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分账信息表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-12 16:21:00
 */
@Mapper
public interface PayBalanceDivideDao extends BaseMapper<PayBalanceDivideEntity> {
	
}
