package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.api.dto.resp.AllSalesAreaRespDTO;
import com.tfjt.pay.external.unionpay.entity.SalesAreaIncomingChannelEntity;

import java.util.List;
import java.util.Set;

/**
 * @Author zxy
 * @create 2023/12/12 10:39
 */
public interface SalesAreaIncomingChannelService extends IService<SalesAreaIncomingChannelEntity> {
    List<AllSalesAreaRespDTO> getAllSaleAreas();

    /**
     * 根据区code集合批量查询入网渠道配置
     * @param codes
     * @return
     */
    List<SalesAreaIncomingChannelEntity> queryByDistrictsCodes(List<String> codes);

    /**
     * 根据区code集合批量查询入网渠道配置
     * @param codes
     * @return
     */
    List<SalesAreaIncomingChannelEntity> queryByDistrictsCodesSet(Set<String> codes);
}
