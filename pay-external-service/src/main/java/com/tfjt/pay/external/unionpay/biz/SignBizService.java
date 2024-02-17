package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.resp.UnionPayResult;

/**
 * @author tony
 * @version 1.0
 * @title SignBizService
 * @description
 * @create 2024/2/5 14:38
 */
public interface SignBizService {
    UnionPayResult signingReview(String signData, String jsonData, String accesserId);
}
