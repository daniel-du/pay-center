package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.LoanCallbackEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 贷款-进件-回调表
 * 
 * @author chenshun
 * @email lixiaolei
 * @date 2023-06-06 14:26:37
 */
@Mapper
public interface LoanCallbackDao extends BaseMapper<LoanCallbackEntity> {
	
}
