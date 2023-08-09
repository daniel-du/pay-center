package com.tfjt.pay.external.unionpay.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.BankAreaEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 联行号表-城市代码
 * 
 * @author effine
 * @email iballad@163.com
 * @date 2023-05-21 18:51:49
 */
@Mapper
public interface BankAreaDao extends BaseMapper<BankAreaEntity> {
	
}
