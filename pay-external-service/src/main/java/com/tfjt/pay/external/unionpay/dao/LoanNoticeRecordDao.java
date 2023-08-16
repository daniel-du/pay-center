package com.tfjt.pay.external.unionpay.dao;

import com.tfjt.pay.external.unionpay.entity.LoanNoticeRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 银联通知记录表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-16 17:14:28
 */
@Mapper
public interface LoanNoticeRecordDao extends BaseMapper<LoanNoticeRecordEntity> {
	
}
