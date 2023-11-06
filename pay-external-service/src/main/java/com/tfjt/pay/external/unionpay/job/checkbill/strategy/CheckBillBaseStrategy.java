package com.tfjt.pay.external.unionpay.job.checkbill.strategy;

import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;

import java.util.Date;
import java.util.List;

/**
 *  对账单业务表
 * @author songx
 * @Date: 2023/11/06/12:20
 * @Description:
 */
public interface CheckBillBaseStrategy {
    /**
     * 查询指定日期为核对的数据量
     * @param date 指定日期
     * @return 条数
     */
    Integer unCheckCount(Date date);

    /**
     * 查询待核对的数据列表
     * @param date       对账日期
     * @param pageNo     页数
     * @param pageSize   每页显示条数
     * @return 待核对数据
     */
    List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, Integer pageNo, Integer pageSize);

    /**
     * 获取数据业务对应的表面
     * @return 表名
     */
    String getTableName();

    /**
     * 获取业务类型
     * @return 业务类型
     */
    String getTypeName();
}
