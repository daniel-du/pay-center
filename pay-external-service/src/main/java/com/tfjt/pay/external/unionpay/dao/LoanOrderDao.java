package com.tfjt.pay.external.unionpay.dao;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tfjt.pay.external.unionpay.dto.resp.ServiceFeeOrderRespDTO;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 贷款订单表
 *
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-16 16:42:20
 */
@Mapper
public interface LoanOrderDao extends BaseMapper<LoanOrderEntity> {

    List<LoanOrderEntity> listNotConfirmOrder(DateTime date);

    ServiceFeeOrderRespDTO getServiceFeeOrder(@Param("outOrderNo") String outOrderNo);

    Page<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(@Param("date") Date date, Page<LoanUnionpayCheckBillDetailsEntity> page);

    Integer countUnCheckBill(@Param("date")Date date);
}
