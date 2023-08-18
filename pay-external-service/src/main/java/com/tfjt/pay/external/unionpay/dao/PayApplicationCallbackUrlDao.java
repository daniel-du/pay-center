package com.tfjt.pay.external.unionpay.dao;

import com.tfjt.pay.external.unionpay.entity.PayApplicationCallbackUrlEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用表-回调
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-18 20:59:16
 */
@Mapper
public interface PayApplicationCallbackUrlDao extends BaseMapper<PayApplicationCallbackUrlEntity> {
	
}
