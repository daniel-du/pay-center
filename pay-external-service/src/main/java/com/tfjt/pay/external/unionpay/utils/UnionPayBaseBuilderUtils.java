package com.tfjt.pay.external.unionpay.utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.constants.UnionPayConstant;
import com.tfjt.pay.external.unionpay.dto.RespLwzMsgReturn;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayBaseReq;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayBaseResp;
import com.tfjt.pay.external.unionpay.enums.TransactionCodeEnum;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * @ClassName UnionPayBaseBuilderUtils
 * @description: 银联公共参数赋值
 * @author Lzh
 * @date 2023年08月09日
 * @version: 1.0
 */
@Component
@Slf4j
public class UnionPayBaseBuilderUtils<T> {

    @LoadBalanced()
    @Resource()
    private RestTemplate restTemplate;

    @Value("${unionPayLoans.url}")
    private String url;

    @Value("${unionPayLoans.groupId}")
    private String groupId;
    /**
     * 公共参数赋值
     *
     * @param lwzBussCode
     * @param lwzData
     * @return
     */
    public  T baseBuilder(String lwzBussCode, String lwzData) {
        String srcReqDate = DateFormatUtils.format(new Date(), "yyyyMMdd");
        String srcReqTime = DateFormatUtils.format(new Date(), "hhmmss");
        String nonce = UUID.randomUUID().toString().replace("-", "");

        UnionPayBaseReq unionPayBaseReq =  UnionPayBaseReq.builder()
                .lwzBussCode(lwzBussCode)
                .transCode("203000")
                .verNo("100")
                .srcReqDate(srcReqDate)
                .srcReqTime(srcReqTime)
                .srcReqId(nonce)
                .channelId("043")
                .merNo(null)
                .groupId(groupId)
                .lwzData(lwzData)
                .channelId("043")
                .lwzChannelType("19")
                .build();
        unionPayBaseReq.setSignature(UnionPaySignUtil.sign(unionPayBaseReq));
        return (T)unionPayBaseReq;
    }

    /**
     *
     * @param unionPayBaseReq
     * @return
     */
    public ResponseEntity<T> post(UnionPayBaseReq unionPayBaseReq) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(UnionPayConstant.CONTENT_TYPE, UnionPayConstant.CONTENT_TYPE_VAL);
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(JSON.toJSONString(unionPayBaseReq), httpHeaders);
        //调用银联接口
        log.info("调取结果:{}",stringHttpEntity.getBody());

        ResponseEntity<UnionPayBaseResp> responseEntity = restTemplate.postForEntity(url, stringHttpEntity, UnionPayBaseResp.class);
        return (ResponseEntity<T>)responseEntity;
    }

    /**
     * 公用返回值
     *
     * @param responseEntity
     * @return
     */
    public T getBaseReturn(ResponseEntity<T> responseEntity,Class clazz) {
        UnionPayBaseResp unionPayBaseResp = (UnionPayBaseResp) responseEntity.getBody();
        if (!ObjectUtils.isNotEmpty(unionPayBaseResp)) {
            log.error("银联调用失败{}", responseEntity.getBody());
            throw new TfException(500, unionPayBaseResp.getRespMsg());
        }
        if ( !Objects.equals("LWZ99999", unionPayBaseResp.getRespCode())) {
            log.error("银联调用失败{}", responseEntity.getBody().toString());
            throw new TfException(500, unionPayBaseResp.getRespMsg());
        }

        if (Objects.equals("200", unionPayBaseResp.getRespLwzCode())) {
            log.error("银联调用成功{}", unionPayBaseResp.getLwzRespData());
            return (T)JSON.parseObject(unionPayBaseResp.getLwzRespData(), clazz);
        } else {
            log.error("银联-银行调用失败{}", responseEntity.getBody().toString());
            throw new TfException(500, getRespLwzMsgReturnMsg(unionPayBaseResp.getRespLwzMsg()));
        }
    }

    private String getRespLwzMsgReturnMsg(String respLwzMsg) {
        log.info("错误信息{}", respLwzMsg);
        String mgs = "";
        if(StringUtils.isNotBlank(respLwzMsg)){
            RespLwzMsgReturn respLwzMsgReturn = JSON.parseObject(respLwzMsg, RespLwzMsgReturn.class);
            if(respLwzMsgReturn!=null){
                if(StringUtils.isNotBlank(respLwzMsgReturn.getIssue())){
                    return respLwzMsgReturn.getIssue();
                }
                if(StringUtils.isNotBlank(respLwzMsgReturn.getMessage())){
                    return respLwzMsgReturn.getMessage();
                }
            }
        }
        return mgs;
    }

    public T combination(String lwzBussCode,String lwzData,Class clazz){

        UnionPayBaseReq unionPayBaseReq = (UnionPayBaseReq)baseBuilder(lwzBussCode, lwzData);
        log.info("入参{}", JSON.toJSON(unionPayBaseReq));
        ResponseEntity<T> responseEntity = post(unionPayBaseReq);
        log.info("返回值{}", JSONObject.toJSONString(responseEntity));
        return getBaseReturn(responseEntity, clazz);
    }
}
