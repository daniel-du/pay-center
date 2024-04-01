package com.tfjt.pay.external.unionpay.dao;

import com.tfjt.pay.external.unionpay.entity.DigitalUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数字人民币开通信息表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-11-28 17:03:59
 */
@Mapper
public interface DigitalUserDao extends BaseMapper<DigitalUserEntity> {
	
}
