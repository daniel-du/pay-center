package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.query.api.dto.req.QueryIncomingStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.biz.IncomingBizService;
import com.tfjt.pay.external.unionpay.biz.IncomingQueryBizService;
import com.tfjt.pay.external.unionpay.config.DevConfig;
import com.tfjt.pay.external.unionpay.dto.req.*;
import com.tfjt.pay.external.unionpay.dto.resp.AllIncomingMessageRespDTO;
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

    /**
     * 测试功能
     * @param incomingMessageReqDTO
     * @return
     */
    @PostMapping("/queryIncomingMsg")
    public Result<IncomingMessageRespDTO> queryIncomingMsg(@RequestBody IncomingMessageReqDTO incomingMessageReqDTO) {
        return incomingBizService.queryIncomingMessage(incomingMessageReqDTO);
    }

    @PostMapping("/bacthIncoming")
    public Result bacthIncoming() {
        return incomingBizService.bacthIncoming();
    }

    @PostMapping("/queryAllIncomingMessage")
    public Result<List<AllIncomingMessageRespDTO>> queryAllIncomingMessage(@RequestBody AllIncomingMessageReqDTO reqDTO) {
        return incomingBizService.queryAllIncomingMessage(reqDTO);
    }

    /**
     * 测试功能
     * @return
     */
    @GetMapping("/getActive")
    public Result getActive() {
        return Result.ok(devConfig.getActive());
    }

    /**
     * 测试功能
     * @param incomingStatusReqDTO
     * @return
     */
    @PostMapping("/queryIncomingStatus")
    public Result queryIncomingStatus(@RequestBody IncomingStatusReqDTO incomingStatusReqDTO) {
        return incomingBizService.queryIncomingStatus(incomingStatusReqDTO);
    }

    /**
     * 测试功能
     * @param reqDTO
     * @return
     */
    @PostMapping("/getIncomingStatusByCodes")
    public Result getIncomingAreaChannel(@RequestBody QueryIncomingStatusReqDTO reqDTO) {
        return incomingQueryBizService.queryIncomingStatusByAreaCodes(reqDTO);
    }

    /**
     * 测试功能
     * @param reqDTO
     * @return
     */
    @PostMapping("/getIncomingStatusByCode")
    public Result getIncomingStatus(@RequestBody QueryIncomingStatusReqDTO reqDTO) {
        return incomingQueryBizService.queryIncomingStatus(reqDTO);
    }

    /**
     * 测试功能
     * @param queryIncomingStatusReqDTOS
     * @return
     */
    @PostMapping("/batchQueryIncomingStatus")
    public Result batchQueryIncomingStatus(@RequestBody List<QueryIncomingStatusReqDTO> queryIncomingStatusReqDTOS) {
        return incomingQueryBizService.batchQueryIncomingStatus(queryIncomingStatusReqDTOS);
    }

    /**
     * 测试功能
     * @param incomingMessageReqDTOS
     * @return
     */
    @PostMapping("/queryIncomingMessages")
    public Result queryIncomingMessages(@RequestBody List<IncomingMessageReqDTO> incomingMessageReqDTOS) {
        return incomingBizService.queryIncomingMessages(incomingMessageReqDTOS);
    }


}
