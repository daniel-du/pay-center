package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.biz.PayApplicationCallbackBiz;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.TradeResultConstant;
import com.tfjt.pay.external.unionpay.dto.req.DivideNoticeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.ShopDivideLogDTO;
import com.tfjt.pay.external.unionpay.dto.resp.LoanOrderDetailsRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.LoanOrderUnifiedorderResqDTO;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.service.LoanBalanceDivideDetailsService;
import com.tfjt.pay.external.unionpay.service.LoanOrderDetailsService;
import com.tfjt.pay.external.unionpay.service.LoanRequestApplicationRecordService;
import com.tfjt.pay.external.unionpay.service.PayApplicationCallbackUrlService;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.*;

/**
 * @author songx
 * @date 2023-08-18 18:18
 * @email 598482054@qq.com
 */
@Slf4j
@Component
public class PayApplicationCallbackBizImpl implements PayApplicationCallbackBiz {

    @Resource
    private PayApplicationCallbackUrlService payApplicationCallbackUrlService;

    @Resource
    private LoanOrderDetailsService loanOrderDetailsService;

    @Resource
    private LoanRequestApplicationRecordService recordService;

    @Resource
    private LoanBalanceDivideDetailsService loanBalanceDivideDetailsService;

    @Resource
    private LoanUserService loanUserService;


    @Value("${unionPay.loan.fms.app-id}")
    private String fmsAppId;

    @Value("${unionPay.loan.shop.app-id}")
    private String shopAppId;

    @Override
    public boolean noticeShop(LoanOrderEntity orderEntity, String eventType, Long callbackId) {
        //构建发送shop服务的参数信息
        LoanOrderUnifiedorderResqDTO loanOrderUnifiedorderResqDTO = new LoanOrderUnifiedorderResqDTO();
        BeanUtil.copyProperties(orderEntity, loanOrderUnifiedorderResqDTO);
        loanOrderUnifiedorderResqDTO.setResultCode(TradeResultConstant.UNIONPAY_SUCCEEDED.equals(orderEntity.getStatus()) ? TradeResultConstant.PAY_SUCCESS : TradeResultConstant.PAY_FAILED);
        loanOrderUnifiedorderResqDTO.setTransactionId(orderEntity.getCombinedGuaranteePaymentId());
        LambdaQueryWrapper<LoanOrderDetailsEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoanOrderDetailsEntity::getOrderId, orderEntity.getId());
        List<LoanOrderDetailsEntity> list = loanOrderDetailsService.list(queryWrapper);
        List<LoanOrderDetailsRespDTO> detailsRespDTOS = new ArrayList<>();
        for (LoanOrderDetailsEntity orderDetailsEntity : list) {
            LoanOrderDetailsRespDTO loanOrderDetailsRespDTO = new LoanOrderDetailsRespDTO();
            BeanUtil.copyProperties(orderDetailsEntity, loanOrderDetailsRespDTO);
            detailsRespDTOS.add(loanOrderDetailsRespDTO);
        }
        loanOrderUnifiedorderResqDTO.setDetailsDTOList(detailsRespDTOS);
        loanOrderUnifiedorderResqDTO.setTotalFee(orderEntity.getAmount());
        loanOrderUnifiedorderResqDTO.setOutTradeNo(orderEntity.getBusinessOrderNo());
        String parameter = JSONObject.toJSONString(loanOrderUnifiedorderResqDTO);
        return sendRequest(orderEntity.getAppId(), parameter, orderEntity.getTradeOrderNo(), eventType, callbackId);
    }

    /**
     * fms系统发送
     * @param list 入账信息
     * @return
     */
    @Override
    public boolean noticeFmsIncomeNotice(List<LoadBalanceNoticeEntity> list, String eventType, String eventId, Long callbackId) {
        return sendRequest(fmsAppId, JSONObject.toJSONString(list), eventId,eventType, callbackId);
    }

    @Override
    public boolean noticeFmsDivideNotice(LoadBalanceDivideEntity divideEntity, String eventType, Long id) {
        List<DivideNoticeReqDTO> list = new ArrayList<>();
        List<LoanBalanceDivideDetailsEntity> listDetails = loanBalanceDivideDetailsService.listByDivideId(divideEntity.getId());
        for (LoanBalanceDivideDetailsEntity listDetail : listDetails) {
            DivideNoticeReqDTO divideNoticeReqDTO = new DivideNoticeReqDTO();
            divideNoticeReqDTO.setPaySystemId(listDetail.getId());
            divideNoticeReqDTO.setFinishAt(Objects.isNull(listDetail.getFinishedAt()) ? null : listDetail.getFinishedAt().getTime());
            divideNoticeReqDTO.setOrderNo(listDetail.getSubBusinessOrderNo());
            divideNoticeReqDTO.setStatus(listDetail.getStatus());
            list.add(divideNoticeReqDTO);
        }
        return sendRequest(divideEntity.getAppId(), JSONObject.toJSONString(list), divideEntity.getTradeOrderNo(), eventType, id);
    }

    @Override
    public boolean noticeShopDivideNotice(LoadBalanceDivideEntity divideEntity, String eventType, Long id) {
        List<LoanBalanceDivideDetailsEntity> listDetails = loanBalanceDivideDetailsService.listByDivideId(divideEntity.getId());
        List<ShopDivideLogDTO> list = new ArrayList<>(listDetails.size());
        for (LoanBalanceDivideDetailsEntity listDetail : listDetails) {
            LoanUserEntity user = loanUserService.getByBalanceAcctId(listDetail.getRecvBalanceAcctId());
            ShopDivideLogDTO dto = new ShopDivideLogDTO();
            dto.setMoney(new BigDecimal(listDetail.getAmount().toString()).divide(new BigDecimal("100"), NumberConstant.TWO, RoundingMode.HALF_UP));
            dto.setStatus(TradeResultConstant.UNIONPAY_SUCCEEDED.equals(listDetail.getStatus()) ? NumberConstant.ONE : NumberConstant.ZERO);
            dto.setLogType(user.getType());
            dto.setShopId(user.getBusId());
            dto.setType(NumberConstant.THREE);
            dto.setPayAccountName(divideEntity.getPayBalanceAcctName());
            dto.setPayBalanceAcctId(divideEntity.getPayBalanceAcctId());
            list.add(dto);
        }
        return sendRequest(shopAppId, JSONObject.toJSONString(list), divideEntity.getBusinessOrderNo(), eventType, id);
    }

    /**
     * 提现回掉通知业务
     *
     * @param withdrawalOrderEntity
     * @param id
     * @return
     */
    @Override
    public boolean noticeWithdrawalNotice(LoanWithdrawalOrderEntity withdrawalOrderEntity, String eventType, Long id) {
        String callbackUrl = payApplicationCallbackUrlService.getCallBackUrlByTypeAndAppId(eventType, withdrawalOrderEntity.getAppId());
        if (StringUtil.isNotBlank(callbackUrl)) {
            Map<String, Object> params = new HashMap<>();
            params.put("withdrawalOrderNo", withdrawalOrderEntity.getWithdrawalOrderNo());
            params.put("status", withdrawalOrderEntity.getStatus());
            return sendRequest(withdrawalOrderEntity.getAppId(), JSONObject.toJSONString(params), withdrawalOrderEntity.getWithdrawalOrderNo(), callbackUrl, id);
        } else {
            throw new TfException("未配置回调地址");
        }
    }

    @Override
    public boolean retryNotice(LoanRequestApplicationRecordEntity o) {
        return sendRequest(o.getAppId(),o.getRequestParam(),o.getTradeOrderNo(),o.getTradeType(),o.getCallbackId(),o.getRequestUrl());
    }


    private boolean sendRequest(String appId, String parameter, String tradeOrderNo, String eventType, Long callbackId) {
        String callBackUrl = payApplicationCallbackUrlService.getCallBackUrlByTypeAndAppId(eventType, appId);
        return sendRequest(appId,parameter,tradeOrderNo,eventType,callbackId,callBackUrl);
    }
    /**
     * 发送请求并记录请求日志信息
     *
     * @param appId        请求的APPid
     * @param parameter    参数
     * @param tradeOrderNo 交易单号
     * @param eventType    通知类型
     * @param callbackId   关联银联回调记录表id
     */
    private boolean sendRequest(String appId, String parameter, String tradeOrderNo, String eventType, Long callbackId,String callBackUrl) {

        LoanRequestApplicationRecordEntity record = builderRecord(appId, parameter, tradeOrderNo, callbackId,callBackUrl,eventType);
        long start = System.currentTimeMillis();
        String result = "";
        try {

            log.info("应用服务发送交易通知>>>>>>>>>>>>>:{},请求参数:{},appId:{}", callBackUrl, parameter, appId);
            result = HttpUtil.post(callBackUrl, parameter);
            log.info("接受应用服务发送交易通知<<<<<<<<<<:{}", result);
            record.setResponseParam(result);
            record.setResponseCode(HttpStatus.HTTP_OK);
        } catch (Exception e) {
            log.error("应用服务发送交易异常<<<<<<<<<<<<<<<:{},请求参数:{},appId:{},e:{}", callBackUrl, parameter, appId, e.getMessage());
            record.setResponseParam(e.getMessage());
            record.setResponseCode(HttpStatus.HTTP_INTERNAL_ERROR);
        }
        long end = System.currentTimeMillis();
        record.setResponseTime((int) (end - start));
        //异步记录请求日志
        boolean b = "success".equalsIgnoreCase(result);
        record.setCallbackStatus(b? NumberConstant.ONE:NumberConstant.ZERO);
        recordService.asyncSave(record);
        return b;
    }

    /**
     * 构造通知的记录信息
     *
     * @param appId        应用APPid
     * @param parameter    发送参数
     * @param tradeOrderNo 业务id
     * @param callbackId   银联通知表id
     * @param callBackUrl  请求地址
     * @param eventType    事件类型 同银联回调类型
     * @return 通知记录信息
     */
    private LoanRequestApplicationRecordEntity builderRecord(String appId, String parameter, String tradeOrderNo, Long callbackId, String callBackUrl, String eventType) {
        LoanRequestApplicationRecordEntity record = new LoanRequestApplicationRecordEntity();
        record.setAppId(appId);
        record.setTradeType(eventType);
        record.setRequestParam(parameter);
        record.setTradeOrderNo(tradeOrderNo);
        record.setCreateTime(new Date());
        record.setCallbackId(callbackId);
        record.setRequestUrl(callBackUrl);
        return record;
    }
}
