package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.CustBankInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户银行信息
 * 
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-20 09:27:39
 */
@Mapper
public interface CustBankInfoDao extends BaseMapper<CustBankInfoEntity> {
	
}
