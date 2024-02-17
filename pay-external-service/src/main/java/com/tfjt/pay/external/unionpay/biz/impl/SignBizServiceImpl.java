package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.biz.SignBizService;
import com.tfjt.pay.external.unionpay.dto.req.SelfSignAppDTO;
import com.tfjt.pay.external.unionpay.dto.req.SelfSignParamDTO;
import com.tfjt.pay.external.unionpay.dto.resp.SigningReviewRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayResult;
import com.tfjt.pay.external.unionpay.entity.PayApplicationCallbackUrlEntity;
import com.tfjt.pay.external.unionpay.entity.SelfSignEntity;
import com.tfjt.pay.external.unionpay.entity.SigningReviewLogEntity;
import com.tfjt.pay.external.unionpay.service.PayCallbackLogService;
import com.tfjt.pay.external.unionpay.service.SelfSignService;
import com.tfjt.pay.external.unionpay.service.SigningReviewLogService;
import com.tfjt.pay.external.unionpay.service.UnionPayCallbackUrlService;
import com.tfjt.pay.external.unionpay.utils.DESUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author tony
 * @version 1.0
 * @title SignBizServiceImpl
 * @description
 * @create 2024/2/5 15:36
 */
@Service
@Slf4j
public class SignBizServiceImpl implements SignBizService {

    @Value("${unionPay.sign.appId}")
    private String appId;

    @Value("${unionPay.sign.appKey}")
    private String appKey;


    @Value("${unionPay.signgys.appId}")
    private String ysAppId;


    @Value("${unionPay.signgys.appKey}")
    private String ysAppKey;

    @Value("${spring.profiles.active}")
    private String env;

    @Resource
    private SigningReviewLogService signingReviewLogService;

    @Resource
    private UnionPayCallbackUrlService unionPayCallbackUrlService;

    @Resource
    private SelfSignService selfSignService;

    @Resource
    private PayCallbackLogService payCallbackLogService;


    @Override
    public UnionPayResult signingReview(String signData, String jsonData, String accesserId) {
        //
        if (Objects.isNull(signData)) {
            log.error("sign_data不能为空");
            return new UnionPayResult().setResCode("9999").setResMsg("sign_data不能为空");
        }
        if (Objects.isNull(jsonData)) {
            log.error("json_data不能为空");
            return new UnionPayResult().setResCode("9999").setResMsg("sign_data不能为空");
        }
        if (Objects.isNull(accesserId)) {
            log.error("accesser_id不能为空");
            return new UnionPayResult().setResCode("9999").setResMsg("accesser_id不能为空");
        }
        //异步写入日志
        SigningReviewLogEntity signingReviewLogEntity = new SigningReviewLogEntity();
        signingReviewLogEntity.setSignData(signData)
                .setJsonData(jsonData)
                .setAccesserId(accesserId)
                .setEnv(env);
        signingReviewLogService.saveLog(signingReviewLogEntity);
        //解密jsonData
        String data = null;
        if (Objects.equals(accesserId, ysAppId)) {
            try {
                data = DESUtil.decrypt(jsonData, ysAppKey);
            } catch (Exception ex) {
                log.error("解密失败", ex);
                return new UnionPayResult().setResCode("1005").setResMsg("解密失败");
            }
        }

        if (Objects.equals(accesserId, appId)) {
            try {
                data = DESUtil.decrypt(jsonData, appKey);
            } catch (Exception ex) {
                log.error("解密失败", ex);
                return new UnionPayResult().setResCode("1005").setResMsg("解密失败");
            }
        }
        log.info("解密后的数据：{}", data);
        SelfSignParamDTO selfSignParamDTO = null;
        if (Objects.nonNull(data)) {
            SigningReviewRespDTO signingReviewRespDTO = JSON.parseObject(data, SigningReviewRespDTO.class);
            selfSignParamDTO = new SelfSignParamDTO();
            selfSignParamDTO.setSigningStatus(signingReviewRespDTO.getApplyStatus());
            selfSignParamDTO.setMid(signingReviewRespDTO.getMerNo());
            selfSignParamDTO.setBusinessNo(signingReviewRespDTO.getCompanyNo());
        }
        //处理业务数据
        if (Objects.nonNull(selfSignParamDTO)) {
            updateSignStatus(selfSignParamDTO);
        }
        return new UnionPayResult().setResCode("0000").setResMsg("成功");
    }


    private void updateSignStatus(SelfSignParamDTO selfSignParamDTO) {

        List<SelfSignEntity> selfSignEntityList = selfSignService.selectByMid(selfSignParamDTO.getMid());

        //rpc调用业务
        RestTemplate restTemplate = new RestTemplate();
        //回调地址查询
        List<PayApplicationCallbackUrlEntity> callbackUrlList = unionPayCallbackUrlService.getCallbackUrlByAppIdAndType(6);

        if (CollUtil.isNotEmpty(callbackUrlList)) {
            for (PayApplicationCallbackUrlEntity callbackUrlEntity : callbackUrlList) {
                List<SelfSignParamDTO> selfSignParamDTOList = new ArrayList<>();
                for (SelfSignEntity selfSign : selfSignEntityList) {
                    selfSignParamDTO.setAppId(callbackUrlEntity.getAppId());
                    selfSignParamDTO.setAccesserAcct(selfSign.getAccesserAcct());
                    selfSignParamDTOList.add(selfSignParamDTO);
                }
                SelfSignAppDTO selfSignAppDTO = new SelfSignAppDTO();
                selfSignAppDTO.setAppId(callbackUrlEntity.getAppId());
                selfSignAppDTO.setSelfSignParamDTOList(selfSignParamDTOList);
                Date r = new Date();
                //发起请求
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.set("Content-Type", "application/json");
                HttpEntity<String> stringHttpEntity = new HttpEntity<>(JSON.toJSONString(selfSignAppDTO), httpHeaders);
                String rest = "";
                try {
                    log.info("请求入网URL" + callbackUrlEntity.getUrl());
                    log.info("请求入网回调参数" + JSON.toJSONString(selfSignAppDTO));

                    rest = restTemplate.postForObject(callbackUrlEntity.getUrl(), stringHttpEntity, String.class);
                    if (StringUtils.isNotBlank(rest)) {
                        JSONObject json = JSONObject.parseObject(rest);
                        if (Objects.equals("0", json.get("code").toString())) {
                            //更新入网签约
                            selfSignService.updateBatchById(selfSignEntityList);
                        }
                    } else {
                        log.error("返回接口为空---");
                    }

                } catch (Exception e) {
                    log.error("远程调用业务接口失败", e);
                }
                payCallbackLogService.saveCallBackLog(callbackUrlEntity.getUrl(), appId, JSON.toJSONString(selfSignAppDTO), rest, 4, r, "");
                XxlJobHelper.log("--------------------------入网商户状态结束共" + selfSignEntityList.size() + "个----------------------");
                log.info("入网商户状态更新结束共{}" + selfSignEntityList.size());
            }
        }

    }
}
