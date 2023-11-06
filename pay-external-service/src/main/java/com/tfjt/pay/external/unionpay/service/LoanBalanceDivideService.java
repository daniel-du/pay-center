package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.EventDataDTO;
import com.tfjt.pay.external.unionpay.entity.LoadBalanceDivideEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;

import java.util.Date;
import java.util.List;


/**
 * 分账记录表
 *
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-14 16:05:56
 */
public interface LoanBalanceDivideService extends IService<LoadBalanceDivideEntity> {
    /**
     * 查询主交易单号是否存在
     * @param businessOrderNo 主交易单号
     * @return  true 已存在 false  不存在
     */
    boolean checkExistBusinessOrderNo(String businessOrderNo);

    /**
     * 处理分账信息
     * @param eventDataDTO 银联返回状态
     * @return 分账信息
     */
    LoadBalanceDivideEntity divideNotice(EventDataDTO eventDataDTO);

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

