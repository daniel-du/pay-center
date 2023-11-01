package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.LoanWithdrawalOrderEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 提现 服务类
 * </p>
 *
 * @author young
 * @since 2023-08-17
 */
public interface LoanWithdrawalOrderService extends IService<LoanWithdrawalOrderEntity> {

    LoanWithdrawalOrderEntity getWithdrawalOrderByNo(String outOrderNo);


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
