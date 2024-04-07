package com.tfjt.pay.external.unionpay.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.api.dto.resp.TtqfCallbackRespDTO;
import com.tfjt.pay.external.unionpay.biz.IncomingTtqfBizService;
import com.tfjt.pay.external.unionpay.dto.req.IncomingInfoReqDTO;
import com.tfjt.tfcommon.dto.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/22 11:42
 * @description
 */
@RestController
@RequestMapping("/callback")
public class CallbackController {

    @Autowired
    private IncomingTtqfBizService incomingTtqfBizService;

    @PostMapping("/ttqfMessage")
    public TtqfCallbackRespDTO save(@RequestBody JSONObject reqJSON) {
        return incomingTtqfBizService.receviceCallbackMsg(reqJSON);
    }
}
