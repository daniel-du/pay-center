package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.biz.IncomingBizService;
import com.tfjt.pay.external.unionpay.dto.req.IncomingChangeAccessMainTypeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingCheckCodeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingInfoReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSubmitMessageReqDTO;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/save")
    public Result save(@RequestBody IncomingInfoReqDTO incomingInfoReqDTO) {
        return incomingBizService.incomingSave(incomingInfoReqDTO);
    }

    @PostMapping("/submitMessage")
    public Result submitMessage(@RequestBody IncomingSubmitMessageReqDTO incomingSubmitMessageReqDTO) {
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
}
