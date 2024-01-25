package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.api.dto.resp.AllSalesAreaRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.PayChannelRespDTO;
import com.tfjt.pay.external.unionpay.dao.SalesAreaIncomingChannelDao;
import com.tfjt.pay.external.unionpay.entity.SalesAreaIncomingChannelEntity;
import com.tfjt.pay.external.unionpay.service.SalesAreaIncomingChannelService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/12 10:40
 */
@Service
public class SalesAreaIncomingChannelServiceImpl extends BaseServiceImpl<SalesAreaIncomingChannelDao, SalesAreaIncomingChannelEntity> implements SalesAreaIncomingChannelService {
    @Override
    public List<AllSalesAreaRespDTO> getAllSaleAreas() {
        return super.list(AllSalesAreaRespDTO.class);
    }
}
