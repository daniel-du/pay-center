package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;

import java.util.Date;
import java.util.List;

/**
 * @author songx
 * @Date: 2023/11/06/15:58
 * @Description: 提现biz
 */
public interface LoanWithdrawalOrderBizService {
    /**
     * 查询未核对的数量
     * @param date 待核对日期
     * @return 数量
     */
    Integer unCheckCount(Date date);

    /**
     *
     * @param date
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, Integer pageNo, Integer pageSize);
}
