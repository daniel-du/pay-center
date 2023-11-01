package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.LoanWithdrawalOrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * <p>
 * 提现 Mapper 接口
 * </p>
 *
 * @author young
 * @since 2023-08-17
 */
public interface LoanWithdrawalOrderDao extends BaseMapper<LoanWithdrawalOrderEntity> {

    Page<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(@Param("date") Date date, Page<LoanUnionpayCheckBillDetailsEntity> page);

    Integer countUnCheckBill(@Param("date") Date date);
}
