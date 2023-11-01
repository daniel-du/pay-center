package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.EventDataDTO;
import com.tfjt.pay.external.unionpay.entity.LoanOrderDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;

import java.util.Date;
import java.util.List;


/**
 * 贷款订单商户收款信息表
 *
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-16 16:42:20
 */
public interface LoanOrderDetailsService extends IService<LoanOrderDetailsEntity> {

    /**
     * 查询未核对的银联下单
     * @param date   交易日期
     * @param pageNo  页数
     * @param pageSize  每页显示条数
     * @return 待核对数据
     */
    @SuppressWarnings("unused")
    List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, Integer pageNo, Integer pageSize);

    /**
     * 查询未核对的银联下单数量
     * @param date   交易日期
     * @return 待核对数量
     */
    @SuppressWarnings("unused")
    Integer countUnCheckBill(Date date);
}

