package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.EventDataDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ServiceFeeOrderRespDTO;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 贷款订单表
 *
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-16 16:42:20
 */
public interface LoanOrderService extends IService<LoanOrderEntity> {
    /**
     * 检验业务单据是否已经存在
     * @param businessOrderNo 业务号
     * @param appId  业务系统id
     * @return  true 已存在 false 不存在
     */
    boolean checkExistBusinessOrderNo(String businessOrderNo, String appId);

    /**
     * 处理交易结果
     * @param eventDataDTO
     */
    LoanOrderEntity treadResult(EventDataDTO eventDataDTO);

    /**
     * 查询当日未确认的订单
     * @return
     */
    List<LoanOrderEntity> listNotConfirmOrder();


    ServiceFeeOrderRespDTO getServiceFeeOrder(String outOrderNo);


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

