package com.tfjt.pay.external.unionpay.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceDivideDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 分账详情表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-14 16:05:56
 */
@Mapper
public interface LoanBalanceDivideDetailsDao extends BaseMapper<LoanBalanceDivideDetailsEntity> {

    Page<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(@Param("date") Date date, Page<LoanUnionpayCheckBillDetailsEntity> page);

    Integer countUnCheckBill(@Param("date") Date date);
}
