package com.tfjt.pay.external.unionpay.dao;

import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 贷款银联对账表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-18 20:59:10
 */
@Mapper
public interface LoanUnionpayCheckBillDao extends BaseMapper<LoanUnionpayCheckBillEntity> {

    void loadFile(String absolutePath);
}
