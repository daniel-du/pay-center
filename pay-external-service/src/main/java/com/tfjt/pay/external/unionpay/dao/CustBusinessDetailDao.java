package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.CustBusinessDetailEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 营业信息表
 * 
 * @author effine
 * @email iballad@163.com
 * @date 2023-06-05 15:06:04
 */
@Mapper
public interface CustBusinessDetailDao extends BaseMapper<CustBusinessDetailEntity> {
	
}
