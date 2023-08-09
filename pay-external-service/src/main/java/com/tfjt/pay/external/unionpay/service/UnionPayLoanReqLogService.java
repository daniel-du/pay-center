package com.tfjt.pay.external.unionpay.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansBaseReturn;
import com.tfjt.pay.external.unionpay.entity.UnionPayLoanReqLogEntity;

import java.util.Date;

/**
 * 银联-贷款-日志表
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-23 13:48:30
 */
public interface UnionPayLoanReqLogService extends IService<UnionPayLoanReqLogEntity> {

    void asyncSaveLog(UnionPayLoansBaseReturn unionPayLoansBaseReturn, Object p, Date req, Long loanUserId);
}

