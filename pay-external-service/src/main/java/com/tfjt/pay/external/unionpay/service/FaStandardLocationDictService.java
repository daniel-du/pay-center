package com.tfjt.pay.external.unionpay.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.FaStandardLocationDictEntity;

import java.util.List;

/**
 * @author zxy
 * @date 2024-01-09 15:51
 */
public interface FaStandardLocationDictService extends IService<FaStandardLocationDictEntity> {

    List<String> getAreasByCode(List<String> saleAreas);
}

