package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.biz.IncomingMerchantBizService;
import com.tfjt.pay.external.unionpay.dto.req.IncomingMerchantReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingMerchantRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingMerchantInfoEntity;
import com.tfjt.tfcommon.dto.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/11 14:58
 * @description 进件-商户身份信息相关接口
 */
@RestController
@RequestMapping("/incoming/merchant")
public class IncomingMerchantController {

    @Autowired
    private IncomingMerchantBizService incomingMerchantBizService;

    /**
     * 根据id查询进件商户身份信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<IncomingMerchantRespDTO> get(@PathVariable Long id) {
        return incomingMerchantBizService.getById(id);
    }

    /**
     * 进件商户身份信息保存
     * @param incomingMerchantReqDTO
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestBody IncomingMerchantReqDTO incomingMerchantReqDTO) {
        return Result.ok();
    }

    /**
     * 进件商户身份信息修改
     * @param incomingMerchantReqDTO
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody IncomingMerchantReqDTO incomingMerchantReqDTO) {
        return Result.ok();
    }
}
