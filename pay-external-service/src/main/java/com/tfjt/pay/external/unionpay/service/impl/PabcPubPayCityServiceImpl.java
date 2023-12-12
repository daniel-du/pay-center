package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.PabcPubPayCityDao;
import com.tfjt.pay.external.unionpay.dto.resp.PabcCityInfoRespDTO;
import com.tfjt.pay.external.unionpay.entity.PabcPubPayCityEntity;
import com.tfjt.pay.external.unionpay.service.PabcPubPayCityService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 15:38
 */
@Service
@Slf4j
public class PabcPubPayCityServiceImpl extends BaseServiceImpl<PabcPubPayCityDao, PabcPubPayCityEntity> implements PabcPubPayCityService {
    @Override
    public List<PabcCityInfoRespDTO> getCityList(String provinceCode, String bankCode) {
        return baseMapper.getCityList(provinceCode,bankCode);
    }
}
