package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansBaseCallBackDTO;
import com.tfjt.pay.external.unionpay.entity.LoanCallbackEntity;

/**
 * 贷款-进件-回调表
 *
 * @author chenshun
 * @email lixiaolei
 * @date 2023-06-06 14:26:37
 */
public interface LoanCallbackService extends IService<LoanCallbackEntity> {

    LoanCallbackEntity saveLog(Long loanUserId, UnionPayLoansBaseCallBackDTO unionPayLoansBaseCallBackDTO, Integer type, String destAcctNo);

    void updateNoticeStatus(Long id, boolean result, String tradeOrderNo);
}

