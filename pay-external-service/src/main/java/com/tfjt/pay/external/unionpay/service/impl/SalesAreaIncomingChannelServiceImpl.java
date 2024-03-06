package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.api.dto.resp.AllSalesAreaRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.PayChannelRespDTO;
import com.tfjt.pay.external.unionpay.dao.SalesAreaIncomingChannelDao;
import com.tfjt.pay.external.unionpay.entity.SalesAreaIncomingChannelEntity;
import com.tfjt.pay.external.unionpay.service.SalesAreaIncomingChannelService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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

    /**
     * 根据区code集合批量查询入网渠道配置
     * @param codes
     * @return
     */
    @Override
    public List<SalesAreaIncomingChannelEntity> queryByDistrictsCodes(List<String> codes) {
        LambdaQueryWrapper<SalesAreaIncomingChannelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SalesAreaIncomingChannelEntity::getDistrictsCode, codes);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<SalesAreaIncomingChannelEntity> queryByDistrictsCodesSet(Set<String> codes) {
        LambdaQueryWrapper<SalesAreaIncomingChannelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SalesAreaIncomingChannelEntity::getDistrictsCode, codes);
        return this.baseMapper.selectList(queryWrapper);
    }
}
