package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.FaStandardLocationDictDao;
import com.tfjt.pay.external.unionpay.entity.FaStandardLocationDictEntity;
import com.tfjt.pay.external.unionpay.service.FaStandardLocationDictService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zxy
 * @create 2024/1/9 15:51
 */
@Service
public class FaStandardLocationDictServiceImpl extends BaseServiceImpl<FaStandardLocationDictDao, FaStandardLocationDictEntity> implements FaStandardLocationDictService {
    @Override
    public List<String> getAreasByCode(List<String> saleAreas) {
        return baseMapper.getAreasByCode(saleAreas);
    }
}
