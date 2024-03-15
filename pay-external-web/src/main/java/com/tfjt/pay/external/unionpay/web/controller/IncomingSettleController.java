package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.biz.IncomingBusinessBizService;
import com.tfjt.pay.external.unionpay.biz.IncomingSettleBizService;
import com.tfjt.pay.external.unionpay.dto.req.IncomingBusinessReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSettleReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingBusinessRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingSettleRespDTO;
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
@RequestMapping("/incoming/settle")
public class IncomingSettleController {

    @Autowired
    private IncomingSettleBizService incomingSettleBizService;

    /**
     * 根据id查询进件商户身份信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<IncomingSettleRespDTO> get(@PathVariable Long id) {
        return incomingSettleBizService.getById(id);
    }

    /**
     * 进件商户营业信息保存
     * @param incomingSettleReqDTO
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestBody IncomingSettleReqDTO incomingSettleReqDTO) {
        return incomingSettleBizService.save(incomingSettleReqDTO);
    }

    /**
     * 进件商户营业信息修改
     * @param incomingSettleReqDTO
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody IncomingSettleReqDTO incomingSettleReqDTO) {
        return incomingSettleBizService.update(incomingSettleReqDTO);
    }
}
