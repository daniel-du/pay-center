package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.LoanCallbackEntity;

/**
 * 贷款-进件-回调表
 *
 * @author chenshun
 * @email lixiaolei
 * @date 2023-06-06 14:26:37
 */
public interface LoanCallbackService extends IService<LoanCallbackEntity> {

    void saveLog(Long loanUserId, String eventId, String eventType, String toJSONString, String createdAt, Integer type, String destAcctNo);
}

