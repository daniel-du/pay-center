package com.tfjt.pay.external.unionpay.service;


import com.tfjt.pay.external.unionpay.dto.UnionPayLoansBaseCallBackDTO;

/**
 * 银联-贷款回调服务层
 */
public interface UnionPayLoansCallbackApiService {

    Long unionPayLoansBaseCallBack(UnionPayLoansBaseCallBackDTO unionPayLoansBaseCallBackDTO);
}
