package com.tfjt.pay.external.unionpay.web.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.biz.DigitalUserBizService;
import com.tfjt.pay.external.unionpay.dto.req.DigitalSelectReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.DigitalRespDTO;
import com.tfjt.pay.external.unionpay.entity.DigitalUserEntity;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author songx
 * @Date: 2023/11/28/17:33
 * @Description:
 */
@Slf4j
@RequestMapping("/digitalUser")
@RestController
public class DigitalUserController {
    @Resource
    private DigitalUserBizService digitalUserBizService;

    /**
     * 数字人民商户侧查询接口
     * @param request 请求参数
     * @return 查询结果
     * @throws IOException 异常
     */
    @PostMapping("/selectByAccount")
    public DigitalRespDTO selectByAccount(HttpServletRequest request) throws IOException {
        String param = HttpUtil.getString(request.getInputStream(), null, false);
        log.info("数字人民币查询商户信息接收参数:{}",param);
        DigitalSelectReqDTO digitalSelectReqDTO = JSONObject.parseObject(param, DigitalSelectReqDTO.class);
        Result<DigitalRespDTO> result = digitalUserBizService.selectByAccount(digitalSelectReqDTO);
        log.info("数字人民币查询商户信息响应参数:{}",JSONObject.toJSONString(result.getData()));
        return result.getData();
    }

    /**
     * 数字人民币钱包绑定参数
     * @param request 绑定参数
     * @return  绑定结果
     * @throws IOException 异常
     */
    @PostMapping("/bindWallet")
    public DigitalRespDTO bindWallet(HttpServletRequest request) throws IOException {
        String param = HttpUtil.getString(request.getInputStream(), null, false);
        log.info("数字人民币钱包推送接收参数:{}",param);
        DigitalUserEntity digitalUserEntity = JSONObject.parseObject(param, DigitalUserEntity.class);
        Result<DigitalRespDTO> result = digitalUserBizService.bindWallet(digitalUserEntity);
        log.info("数字人民币钱包推送响应参数:{}",JSONObject.toJSONString(result.getData()));
        return result.getData();
    }

    /**
     * 数字人民币钱包解绑
     * @param request 解绑参数
     * @return 解绑响应信息
     * @throws IOException 异常信息
     */
    @PostMapping("/unbindWallet")
    public DigitalRespDTO unbindWallet(HttpServletRequest request) throws IOException {
        String param = HttpUtil.getString(request.getInputStream(), null, false);
        log.info("数字人民币钱包解绑接收参数:{}",param);
        DigitalUserEntity digitalUserEntity = JSONObject.parseObject(param, DigitalUserEntity.class);
        Result<DigitalRespDTO> result = digitalUserBizService.unbindWallet(digitalUserEntity);
        log.info("数字人民币钱包解绑响应参数:{}",JSONObject.toJSONString(result.getData()));
        return result.getData();
    }

}
