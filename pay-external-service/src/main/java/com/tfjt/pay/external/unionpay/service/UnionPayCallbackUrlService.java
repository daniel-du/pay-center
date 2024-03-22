package com.tfjt.pay.external.unionpay.service;


import com.tfjt.pay.external.unionpay.entity.PayApplicationCallbackUrlEntity;

import java.util.List;

/**
 * 银联-贷款回调服务层
 */
public interface UnionPayCallbackUrlService {


    List<PayApplicationCallbackUrlEntity> getCallbackUrlByAppIdAndType(Integer type);
}
