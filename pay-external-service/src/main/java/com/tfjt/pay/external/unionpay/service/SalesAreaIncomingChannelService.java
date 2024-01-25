package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.api.dto.resp.AllSalesAreaRespDTO;
import com.tfjt.pay.external.unionpay.entity.SalesAreaIncomingChannelEntity;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/12 10:39
 */
public interface SalesAreaIncomingChannelService extends IService<SalesAreaIncomingChannelEntity> {
    List<AllSalesAreaRespDTO> getAllSaleAreas();
}
