package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.service.StandardLocationApiService;
import com.tfjt.tfcloud.goods.api.StandardLocationService;
import com.tfjt.tfcloud.goods.dto.FaStandardLocationDictDto;
import com.tfjt.tfcommon.dto.response.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StandardLocationServiceImpl implements StandardLocationApiService {

    @DubboReference(retries = 1, timeout = 60000, check = false)
    private StandardLocationService standardLocationService;

    @Override
    public Result<List<FaStandardLocationDictDto>> locationList(String provinceCode, String cityCode, String districtCode) {
        List<FaStandardLocationDictDto> list = standardLocationService.locationList(provinceCode, cityCode, districtCode);
        return Result.ok(list);
    }
}
