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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        loanOrderUnifiedorderResqDTO.setTradeType("loanPay");
        String parameter = JSONObject.toJSONString(loanOrderUnifiedorderResqDTO);
        return sendRequest(orderEntity.getAppId(), parameter, orderEntity.getTradeOrderNo(), eventType, callbackId);
    }

    /**
     * fms系统发送
     *
     * @param list 入账信息
     * @return
     */
    @Override
    public boolean noticeFmsIncomeNotice(List<LoadBalanceNoticeEntity> list, String eventType, String eventId, Long callbackId) {
        return sendRequest(fmsAppId, JSONObject.toJSONString(list), eventId, eventType, callbackId);
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
            dto.setRecvAccountName(listDetail.getRecvBalanceAcctName());
            dto.setRecvBalanceAcctId(listDetail.getRecvBalanceAcctId());
            dto.setBusOrderNo(listDetail.getSubBusinessOrderNo());
            list.add(dto);
        }
        return sendRequest(shopAppId, JSONObject.toJSONString(list), divideEntity.getBusinessOrderNo(), eventType, id);
    }



    @Override
    public boolean retryNotice(LoanRequestApplicationRecordEntity o) {
        return sendRequest(o.getAppId(), o.getRequestParam(), o.getTradeOrderNo(), o.getTradeType(), o.getCallbackId(), o.getRequestUrl());
    }



}
