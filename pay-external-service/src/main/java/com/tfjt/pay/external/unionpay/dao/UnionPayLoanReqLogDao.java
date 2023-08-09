package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.UnionPayLoanReqLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 银联-贷款-日志表
 * 
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-23 13:48:30
 */
@Mapper
public interface UnionPayLoanReqLogDao extends BaseMapper<UnionPayLoanReqLogEntity> {
	
}
