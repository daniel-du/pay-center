package com.tfjt.pay.external.unionpay.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.biz.PayApplicationCallbackBiz;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansCallbackApiBiz;
import com.tfjt.pay.external.unionpay.config.ExecutorConfig;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.TradeResultConstant;
import com.tfjt.pay.external.unionpay.constants.UnionPayEventTypeConstant;
import com.tfjt.pay.external.unionpay.constants.UnionPayTradeResultCodeConstant;
import com.tfjt.pay.external.unionpay.dto.EventDataDTO;
import com.tfjt.pay.external.unionpay.dto.ExtraDTO;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansBaseCallBackDTO;

import com.tfjt.pay.external.unionpay.dto.req.ConsumerPoliciesCheckReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayIncomeDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesCheckRespDTO;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author songx
 * @date 2023-08-14 22:09
 * @email 598482054@qq.com
 */
@Slf4j
@Component
public class UnionPayLoansCallbackApiBizImpl implements UnionPayLoansCallbackApiBiz {

    @Autowired
    private LoanBalanceNoticeService payBalanceNoticeService;


    @Autowired
    private UnionPayLoansCallbackApiService yinLianLoansCallbackApiService;

    @Autowired
    private LoanCallbackService loanCallbackService;

    @Resource
    private LoanOrderService loanOrderService;

    @Resource
    private PayApplicationCallbackBiz payApplicationCallbackBiz;

    @Resource
    private LoanOrderDetailsService loanOrderDetailsService;

    @Resource
    private LoanOrderGoodsService loanOrderGoodsService;

    @Resource
    private UnionPayService unionPayService;


    @Resource
    private LoanBalanceDivideService loanBalanceDivideService;


    @Resource
    private ExecutorConfig executorConfig;

    @Lock4j(keys = "#transactionCallBackReqDTO.eventId", expire = 3000, acquireTimeout = 3000)
    @Override
    public void commonCallback(UnionPayLoansBaseCallBackDTO transactionCallBackReqDTO, HttpServletResponse response) throws ParseException {
        LoanCallbackEntity tfLoanCallbackEntity = loanCallbackService.getOne(new LambdaQueryWrapper<LoanCallbackEntity>().eq(LoanCallbackEntity::getEventId, transactionCallBackReqDTO.getEventId()));
        if (tfLoanCallbackEntity != null) {
            log.info("事件回调已添加{}", transactionCallBackReqDTO.getEventId());
            return;
        }
        String eventType = transactionCallBackReqDTO.getEventType();
        //进件验证
        if (UnionPayEventTypeConstant.MCH_APPLICATION_FINISHED.equals(eventType) || UnionPayEventTypeConstant.SETTLE_ACCT_PAY_AMOUNT_VALIDATION.equals(eventType)) {
            yinLianLoansCallbackApiService.unionPayLoansBaseCallBack(transactionCallBackReqDTO);
            return;
        }
        //母账户入金  交易类通知 
        String eventDataString = JSONObject.toJSONString(transactionCallBackReqDTO.getEventData());
        LoanCallbackEntity loanCallbackEntity = loanCallbackService.saveLog(null, transactionCallBackReqDTO.getEventId(), eventType, eventDataString
                , transactionCallBackReqDTO.getCreatedAt(), null, null);
        log.info("保存回调日志信息:{}", JSONObject.toJSONString(loanCallbackEntity));
        executorConfig.asyncServiceExecutor().execute(()->detailsNotice(loanCallbackEntity));
    }

    /**
     * 交易结果通知
     *
     * @param loanCallbackEntity
     */
    public void treadResult(LoanCallbackEntity loanCallbackEntity) {
        String eventData = loanCallbackEntity.getEventData();
        EventDataDTO eventDataDTO = JSONObject.parseObject(eventData, EventDataDTO.class);
        String tradeId = eventDataDTO.getTradeId();

        // 下单回调
        if (UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_60.equals(tradeId)) {
            LoanOrderEntity orderEntity = loanOrderService.treadResult(eventDataDTO);
            boolean result = payApplicationCallbackBiz.noticeShop(orderEntity, UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_60,loanCallbackEntity.getId());
            //记录通知状态
            this.loanCallbackService.updateNoticeStatus(loanCallbackEntity.getId(),result,orderEntity.getTradeOrderNo());
            //交易成功的进行银联订单确认操作
            if (TradeResultConstant.UNIONPAY_SUCCEEDED.equals(eventDataDTO.getStatus())) {
                //订单确认
                this.confirmOrder(orderEntity);
            }
        }else if(UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_30.equals(tradeId)){
            //TODO 提现 申请处理
        }else if(UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_74.equals(tradeId)){
            //TODO 提现入账
        }else if(UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_51.equals(tradeId)){
            //分账通知
            LoadBalanceDivideEntity divideEntity = loanBalanceDivideService.divideNotice(eventDataDTO);
            boolean result = payApplicationCallbackBiz.noticeFmsDivideNotice(divideEntity, loanCallbackEntity.getEventType(), loanCallbackEntity.getId());
            payApplicationCallbackBiz.noticeShopDivideNotice(divideEntity,loanCallbackEntity.getEventType(),loanCallbackEntity.getId());
        }
    }

    /**
     * 订单确认
     *
     * @param order 订单id
     */
    public void confirmOrder(LoanOrderEntity order) {
        log.info("订单确认:{}",JSONObject.toJSONString(order));
        ConsumerPoliciesCheckReqDTO consumerPoliciesCheckReqDTO = new ConsumerPoliciesCheckReqDTO();
        consumerPoliciesCheckReqDTO.setOutOrderNo(order.getTradeOrderNo());
        consumerPoliciesCheckReqDTO.setGuaranteePaymentId(order.getCombinedGuaranteePaymentId());
        consumerPoliciesCheckReqDTO.setAmount(order.getAmount());

        LambdaQueryWrapper<LoanOrderDetailsEntity> detailsQueryWrapper = new LambdaQueryWrapper<>();
        detailsQueryWrapper.eq(LoanOrderDetailsEntity::getOrderId, order.getId());
        List<LoanOrderDetailsEntity> loanOrderDetailsEntities = this.loanOrderDetailsService.list(detailsQueryWrapper);
        List<ExtraDTO> goods = new ArrayList<>();
        List<Long> detailsId = loanOrderDetailsEntities.stream().map(LoanOrderDetailsEntity::getId).collect(Collectors.toList());
        List<LoanOrderGoodsEntity> loanOrderGoodsEntities = this.loanOrderGoodsService.list(new LambdaQueryWrapper<LoanOrderGoodsEntity>()
                .in(LoanOrderGoodsEntity::getDetailsId, detailsId));
        for (LoanOrderGoodsEntity goodsEntity : loanOrderGoodsEntities) {
            ExtraDTO extraDTO = new ExtraDTO();
            extraDTO.setOrderNo(goodsEntity.getOrderBusinessOrderNo());
            extraDTO.setOrderAmount(String.valueOf(goodsEntity.getProductAmount()));
            extraDTO.setProductCount(String.valueOf(goodsEntity.getProductCount()));
            extraDTO.setProductName(goodsEntity.getProductName());
            goods.add(extraDTO);
        }
        Map<String, Object> extra = new HashMap<>();
        extra.put("productInfos", goods);
        consumerPoliciesCheckReqDTO.setExtra(extra);
        log.info("订单确认调用银联发送消息>>>>>>>>>>>>>>>{}",JSONObject.toJSONString(consumerPoliciesCheckReqDTO));
        Result<ConsumerPoliciesCheckRespDTO> consumerPoliciesCheckRespDTOResult = unionPayService.mergeConsumerPoliciesCheck(consumerPoliciesCheckReqDTO);
        log.info("订单确认调用银联接收消息<<<<<<<<<<<<<<<{}",JSONObject.toJSONString(consumerPoliciesCheckRespDTOResult));
        if (consumerPoliciesCheckRespDTOResult.getCode() == NumberConstant.ONE) {
            order.setConfirmStatus(NumberConstant.ONE);
            this.loanOrderService.updateById(order);
        }
    }

    @Override
    public void confirmOrder() {
        LambdaQueryWrapper<LoanOrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoanOrderEntity::getStatus, TradeResultConstant.UNIONPAY_SUCCEEDED)
                .eq(LoanOrderEntity::getConfirmStatus, NumberConstant.ZERO);
        List<LoanOrderEntity> list = this.loanOrderService.list(queryWrapper);
        for (LoanOrderEntity orderEntity : list) {
            confirmOrder(orderEntity);
        }
    }

    @Override
    public void applicationCallback() {

    }


    /**
     * 处理交易通知
     *
     * @param loanCallbackEntity 日志记录信息
     */
    private void detailsNotice(LoanCallbackEntity loanCallbackEntity) {
        if (UnionPayEventTypeConstant.ROOT_TRANSFER_DEPOSIT.equals(loanCallbackEntity.getEventType())) {
            //处理母账户入金
            balanceIncomeNotice(loanCallbackEntity.getEventData(), loanCallbackEntity.getId());
        } else if (UnionPayEventTypeConstant.TRADE_RESULT.equals(loanCallbackEntity.getEventType())) {
            //处理交易结果
            treadResult(loanCallbackEntity);
        }
    }


    /**
     * 处理银联入金通知
     * @param eventDataString 银联字符串
     */
    public void balanceIncomeNotice(String eventDataString, Long id) {
        UnionPayIncomeDTO unionPayIncomeDTO = JSONObject.parseObject(eventDataString, UnionPayIncomeDTO.class);
        List<LoadBalanceNoticeEntity> list = payBalanceNoticeService.saveByEventDate(unionPayIncomeDTO);
        boolean result = payApplicationCallbackBiz.noticeFmsIncomeNotice(list, unionPayIncomeDTO.getEventType(), unionPayIncomeDTO.getEventId(), id);
        //记录通知状态
        this.loanCallbackService.updateNoticeStatus(id,result,unionPayIncomeDTO.getEventId());
    }

}
