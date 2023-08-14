package com.tfjt.pay.external.unionpay.dao;

import com.tfjt.pay.external.unionpay.entity.PayBalanceNoticeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入金通知
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-12 16:21:00
 */
@Mapper
public interface PayBalanceNoticeDao extends BaseMapper<PayBalanceNoticeEntity> {
	
}
