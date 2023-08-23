package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.service.StandardLocationApiService;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("location")
public class StandardLocationController {

    @Autowired
    private StandardLocationApiService tfstandardLocationService;


    @GetMapping("/getLocationList")
    public Result<?> getLocationList(@RequestParam String provinceCode, @RequestParam String cityCode, @RequestParam String districtCode){

        Result<?> resultList = tfstandardLocationService.locationList(provinceCode,cityCode,districtCode);
        return resultList;
    }
}