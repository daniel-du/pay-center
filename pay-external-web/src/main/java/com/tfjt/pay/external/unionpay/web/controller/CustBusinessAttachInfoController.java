package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.service.CustBusinessAttachInfoService;
import com.tfjt.tfcommon.dto.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("custbusinessattachinfo")
public class CustBusinessAttachInfoController {
    @Autowired
    private CustBusinessAttachInfoService custBusinessAttachInfoService;

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    public Result<?> delete(@RequestBody Map<String, Long> params) {
        boolean bool = custBusinessAttachInfoService.removeById(params.get("id"));
        if (bool) {
            return Result.ok();
        } else {
            return Result.failed("删除失败");
        }
    }

}