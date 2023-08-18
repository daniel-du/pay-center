package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.LoadBalanceNoticeEntity;

import java.util.List;

/**
 * 入金通知
 *
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-12 16:21:00
 */
public interface LoanBalanceNoticeService extends IService<LoadBalanceNoticeEntity> {

    void noticeFms(List<LoadBalanceNoticeEntity> list);

    List<LoadBalanceNoticeEntity> saveByEventDate(String eventDataString);
}

