package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.LoanNoticeRecordEntity;

import java.util.Map;

/**
 * 银联通知记录表
 *
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-16 17:14:28
 */
public interface LoanNoticeRecordService extends IService<LoanNoticeRecordEntity> {
    /**
     * 判断回调是否已经处理
     * @param eventId 事件id
     * @return true 已经存在 false  不存在
     */
    boolean existEventId(String eventId);
}

