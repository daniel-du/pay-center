package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.query.api.dto.req.QueryIncomingStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.biz.IncomingBizService;
import com.tfjt.pay.external.unionpay.biz.IncomingQueryBizService;
import com.tfjt.pay.external.unionpay.config.DevConfig;
import com.tfjt.pay.external.unionpay.dto.req.IncomingChangeAccessMainTypeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingCheckCodeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingInfoReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSubmitMessageReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingSubmitMessageRespDTO;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/13 11:36
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/incoming")
public class IncomingController {

    @Autowired
    private IncomingBizService incomingBizService;

    @Autowired
    private IncomingQueryBizService incomingQueryBizService;

    @Autowired
    private DevConfig devConfig;

    @PostMapping("/save")
    public Result save(@RequestBody IncomingInfoReqDTO incomingInfoReqDTO) {
        return incomingBizService.incomingSave(incomingInfoReqDTO);
    }

    @PostMapping("/submitMessage")
    public Result<IncomingSubmitMessageRespDTO> submitMessage(@RequestBody IncomingSubmitMessageReqDTO incomingSubmitMessageReqDTO) {
        return incomingBizService.incomingSubmit(incomingSubmitMessageReqDTO);
    }

    @PostMapping("/checkCode")
    public Result checkCode(@RequestBody IncomingCheckCodeReqDTO incomingCheckCodeReqDTO) {
        return incomingBizService.checkCode(incomingCheckCodeReqDTO);
    }

    @PostMapping("/changeAccessMainType")
    public Result changeAccessMainType(@RequestBody IncomingChangeAccessMainTypeReqDTO changeAccessMainTypeReqDTO) {
        return incomingBizService.changeAccessMainType(changeAccessMainTypeReqDTO);
    }

    @PostMapping("/dataExtract")
    public Result unionpayDataExtract() {
        return incomingBizService.unionpayDataExtract();
    }

    @PostMapping("/queryIncomingMsg")
    public Result<IncomingMessageRespDTO> queryIncomingMsg(@RequestBody IncomingMessageReqDTO incomingMessageReqDTO) {
        return incomingBizService.queryIncomingMessage(incomingMessageReqDTO);
    }

    @PostMapping("/bacthIncoming")
    public Result bacthIncoming() {
        return incomingBizService.bacthIncoming();
    }

    @GetMapping("/getActive")
    public Result getActive() {
        return Result.ok(devConfig.getActive());
    }

    @PostMapping("/queryIncomingStatus")
    public Result queryIncomingStatus(@RequestBody IncomingStatusReqDTO incomingStatusReqDTO) {
        return incomingBizService.queryIncomingStatus(incomingStatusReqDTO);
    }

    @PostMapping("/getIncomingStatusByCodes")
    public Result getIncomingAreaChannel(@RequestBody QueryIncomingStatusReqDTO reqDTO) {
        return incomingQueryBizService.queryIncomingStatusByAreaCodes(reqDTO);
    }

    @PostMapping("/getIncomingStatusByCode")
    public Result getIncomingStatus(@RequestBody QueryIncomingStatusReqDTO reqDTO) {
        return incomingQueryBizService.queryIncomingStatus(reqDTO);
    }

    @PostMapping("/batchQueryIncomingStatus")
    public Result batchQueryIncomingStatus(@RequestBody List<QueryIncomingStatusReqDTO> queryIncomingStatusReqDTOS) {
        return incomingQueryBizService.batchQueryIncomingStatus(queryIncomingStatusReqDTOS);
    }

    @PostMapping("/queryIncomingMessages")
    public Result queryIncomingMessages(@RequestBody List<IncomingMessageReqDTO> incomingMessageReqDTOS) {
        return incomingBizService.queryIncomingMessages(incomingMessageReqDTOS);
    }


}
