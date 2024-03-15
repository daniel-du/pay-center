package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.biz.IncomingBusinessBizService;
import com.tfjt.pay.external.unionpay.biz.IncomingMerchantBizService;
import com.tfjt.pay.external.unionpay.dto.req.IncomingBusinessReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingBusinessRespDTO;
import com.tfjt.tfcommon.dto.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/12 14:58
 * @description 进件-商户营业信息相关接口
 */
@RestController
@RequestMapping("/incoming/business")
public class IncomingBusinessController {

    @Autowired
    private IncomingBusinessBizService incomingBusinessBizService;

    /**
     * 根据id查询进件商户身份信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<IncomingBusinessRespDTO> get(@PathVariable Long id) {
        return incomingBusinessBizService.getById(id);
    }

    /**
     * 进件商户营业信息保存
     * @param incomingBusinessReqDTO
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestBody IncomingBusinessReqDTO incomingBusinessReqDTO) {
        return incomingBusinessBizService.save(incomingBusinessReqDTO);
    }

    /**
     * 进件商户营业信息修改
     * @param incomingBusinessReqDTO
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody IncomingBusinessReqDTO incomingBusinessReqDTO) {
        return incomingBusinessBizService.update(incomingBusinessReqDTO);
    }
}
