package com.tfjt.pay.external.unionpay.dao;

import com.tfjt.pay.external.unionpay.entity.LoanCallbackApplicationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * payt服务通知记录表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-20 21:47:02
 */
@Mapper
public interface LoanCallbackApplicationDao extends BaseMapper<LoanCallbackApplicationEntity> {
	
}
