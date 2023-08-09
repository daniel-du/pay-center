package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.CustHoldingEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 控股信息表
 * 
 * @author effine
 * @email iballad@163.com
 * @date 2023-06-05 15:10:56
 */
@Mapper
public interface CustHoldingDao extends BaseMapper<CustHoldingEntity> {
	
}
