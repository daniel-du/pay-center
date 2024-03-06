package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.resp.PabcCityInfoRespDTO;
import com.tfjt.pay.external.unionpay.entity.PabcPubPayCityEntity;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 15:35
 */
public interface PabcPubPayCityService extends IService<PabcPubPayCityEntity> {
    List<PabcCityInfoRespDTO> getCityList(String provinceCode, String bankCode);
}
