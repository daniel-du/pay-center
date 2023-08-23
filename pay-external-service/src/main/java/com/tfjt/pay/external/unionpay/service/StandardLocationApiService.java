package com.tfjt.pay.external.unionpay.service;


import com.tfjt.tfcloud.goods.dto.FaStandardLocationDictDto;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.List;

public interface StandardLocationApiService {

    Result<List<FaStandardLocationDictDto>> locationList(String provinceCode, String cityCode, String districtCode);
}
