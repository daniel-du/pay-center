package com.tfjt.pay.external.unionpay.service;

import java.util.List;

/**
 * @Author zxy
 * @create 2024/1/16 9:48
 */
public interface AsyncService {

    public void dingWarning(Long supplierId, List<Integer> newIdentifyList, List<String> newSaleAreas, Boolean saleFlag, Boolean identityFlag, List<String> oldSaleAreas, List<Integer> oldIdentifyList);
}
