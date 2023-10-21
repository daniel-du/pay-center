package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansCallbackApiBiz;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.*;
import com.tfjt.pay.external.unionpay.dto.EventDataDTO;
import com.tfjt.pay.external.unionpay.dto.ExtraDTO;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansBaseCallBackDTO;
import com.tfjt.pay.external.unionpay.dto.req.ConsumerPoliciesCheckReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayIncomeDetailsDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesCheckRespDTO;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.tfcommon.dto.response.Result;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

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
    private LoanOrderDetailsService loanOrderDetailsService;

    @Resource
    private LoanOrderGoodsService loanOrderGoodsService;

    @Resource
    private UnionPayService unionPayService;


    @Resource
    private LoanBalanceDivideService loanBalanceDivideService;

    @Resource
    private LoanWithdrawalOrderService withdrawalOrderService;

    @Resource
    private LoanRequestApplicationRecordService loanRequestApplicationRecordService;

    @Resource
    TfAccountConfig tfAccountConfig;

    @Lock4j(keys = "#transactionCallBackReqDTO.eventId", expire = 3000, acquireTimeout = 3000)
    @Override
    public String commonCallback(UnionPayLoansBaseCallBackDTO transactionCallBackReqDTO) throws ParseException {
        Long id = null;
        LoanCallbackEntity tfLoanCallbackEntity = loanCallbackService.getOne(new LambdaQueryWrapper<LoanCallbackEntity>().eq(LoanCallbackEntity::getEventId, transactionCallBackReqDTO.getEventId()));
        if (tfLoanCallbackEntity != null) {
            log.info("事件回调已添加{}", transactionCallBackReqDTO.getEventId());
            return "";
        }
        String eventType = transactionCallBackReqDTO.getEventType();
        //进件验证
        if (UnionPayEventTypeConstant.MCH_APPLICATION_FINISHED.equals(eventType) || UnionPayEventTypeConstant.SETTLE_ACCT_PAY_AMOUNT_VALIDATION.equals(eventType)) {
            id = yinLianLoansCallbackApiService.unionPayLoansBaseCallBack(transactionCallBackReqDTO);
        }
        //保存日志
        LoanCallbackEntity loanCallbackEntity = loanCallbackService.saveLog(id, transactionCallBackReqDTO, null, null);
        log.info("保存回调日志信息:{}", JSONObject.toJSONString(loanCallbackEntity));
        detailsNotice(loanCallbackEntity);
        return "";
    }

    /**
     * 交易结果通知
     *
     * @param loanCallbackEntity
     */
    public Long tradeResult(LoanCallbackEntity loanCallbackEntity) {
        String eventData = loanCallbackEntity.getEventData();
        EventDataDTO eventDataDTO = JSONObject.parseObject(eventData, EventDataDTO.class);
        String tradeType = eventDataDTO.getTradeType().toString();

        // 下单回调
        if (UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_60.equals(tradeType)) {
            LoanOrderEntity orderEntity = loanOrderService.treadResult(eventDataDTO);
            //交易成功的进行银联订单确认操作
            if (TradeResultConstant.UNIONPAY_SUCCEEDED.equals(eventDataDTO.getStatus())) {
                //订单确认
                this.confirmOrder(orderEntity);
                //如果包含服务费则通知母账户
                LoanOrderEntity serviceFeeOrder = loanOrderService.getServiceFeeOrder(eventDataDTO.getOutOrderNo());
                if(Objects.nonNull(serviceFeeOrder)){
                    log.info("服务费通知===========");
                    //母账户服务费
                    List<LoadBalanceNoticeEntity> list = new ArrayList<>();
                    LoadBalanceNoticeEntity loadBalanceNotice = new LoadBalanceNoticeEntity();
                    loadBalanceNotice.setAmount(serviceFeeOrder.getAmount());
                    loadBalanceNotice.setBalanceAcctId(tfAccountConfig.getBalanceAcctId());
                    loadBalanceNotice.setBalanceAcctNo(tfAccountConfig.getBalanceAcctNo());
                    loadBalanceNotice.setTradeId(serviceFeeOrder.getCombinedGuaranteePaymentId());
                    loadBalanceNotice.setTradeType(UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_99);
                    loadBalanceNotice.setCreateTime(serviceFeeOrder.getCreateAt());
                    loadBalanceNotice.setRecordedAt(serviceFeeOrder.getFinishedAt());
                    loadBalanceNotice.setEventId(loanCallbackEntity.getEventId());
                    loadBalanceNotice.setId(0L);
                    loadBalanceNotice.setPayBalanceAcctId(serviceFeeOrder.getPayBalanceAcctId());
                    loadBalanceNotice.setPayBankAcctName(serviceFeeOrder.getPayBalanceAcctName());
                    list.add(loadBalanceNotice);
                    log.info("服务费通知参数{}", JSON.toJSONString(loadBalanceNotice));
                    loanRequestApplicationRecordService.noticeFmsIncomeNotice(list, UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_10, loanCallbackEntity.getEventId(), loanCallbackEntity.getId());
                }
            }
            loanRequestApplicationRecordService.noticeShop(orderEntity, tradeType, loanCallbackEntity.getId());
            return orderEntity.getLoanUserId();
        }
        if (UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_30.equals(tradeType)) {

            if (eventDataDTO.getOutOrderNo().contains(CommonConstants.FMS_WITHDRAW)) {
                //母账户提现
                noticeParent(loanCallbackEntity, eventDataDTO, tradeType);
            } else {
                LoanWithdrawalOrderEntity withdrawalOrder = withdrawalOrderService.getWithdrawalOrderByNo(eventDataDTO.getOutOrderNo());
                if (withdrawalOrder != null) {
                    withdrawalOrder.setStatus(eventDataDTO.getStatus());
                    withdrawalOrderService.updateById(withdrawalOrder);
                    loanRequestApplicationRecordService.noticeWithdrawalNotice(withdrawalOrder, tradeType, loanCallbackEntity.getId());
                }
            }

            return null;

        }
        if (UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_51.equals(tradeType)) {
            //分账通知
            LoadBalanceDivideEntity divideEntity = loanBalanceDivideService.divideNotice(eventDataDTO);
            loanRequestApplicationRecordService.noticeFmsDivideNotice(divideEntity, tradeType, loanCallbackEntity.getId());
            loanRequestApplicationRecordService.noticeShopDivideNotice(divideEntity, tradeType, loanCallbackEntity.getId());
        }
        if (UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_20.equals(tradeType)) {
            //判断母账户
            if (eventDataDTO.getOutOrderNo().contains(CommonConstants.FMS_DEPOSIT)) {
                //母账户充值
                noticeParent(loanCallbackEntity, eventDataDTO, tradeType);

            }

        }


        return null;
    }

    /**
     * 母账户交易回调状态更新
     *
     * @param loanCallbackEntity
     * @param eventDataDTO
     */
    private void noticeParent(LoanCallbackEntity loanCallbackEntity, EventDataDTO eventDataDTO, String tradeType) {
        List<LoadBalanceNoticeEntity> list = new ArrayList<>();
        LoadBalanceNoticeEntity loadBalanceNotice = new LoadBalanceNoticeEntity();
        loadBalanceNotice.setTradeId(eventDataDTO.getTradeId());
        loadBalanceNotice.setStatus(eventDataDTO.getStatus());
        loadBalanceNotice.setId(loanCallbackEntity.getId());
        loadBalanceNotice.setTradeType(tradeType);
        list.add(loadBalanceNotice);
        loanRequestApplicationRecordService.noticeFmsIncomeNotice(list, UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_10, loanCallbackEntity.getEventId(), loanCallbackEntity.getId());
    }

    /**
     * 订单确认
     *
     * @param order 订单id
     */
    public void confirmOrder(LoanOrderEntity order) {
        log.info("订单确认:{}", JSONObject.toJSONString(order));
        ConsumerPoliciesCheckReqDTO consumerPoliciesCheckReqDTO = new ConsumerPoliciesCheckReqDTO();


        LambdaQueryWrapper<LoanOrderDetailsEntity> detailsQueryWrapper = new LambdaQueryWrapper<>();
        detailsQueryWrapper.eq(LoanOrderDetailsEntity::getOrderId, order.getId())
                .gt(LoanOrderDetailsEntity::getAmount, NumberConstant.ZERO)
                .eq(LoanOrderDetailsEntity::getConfirmedAmount, NumberConstant.ZERO);
        List<LoanOrderDetailsEntity> loanOrderDetailsEntities = this.loanOrderDetailsService.list(detailsQueryWrapper);
        for (LoanOrderDetailsEntity loanOrderDetailsEntity : loanOrderDetailsEntities) {
            List<ExtraDTO> goods = new ArrayList<>();
            List<LoanOrderGoodsEntity> loanOrderGoodsEntities = this.loanOrderGoodsService.list(new LambdaQueryWrapper<LoanOrderGoodsEntity>()
                    .eq(LoanOrderGoodsEntity::getDetailsId, loanOrderDetailsEntity.getId()));
            for (LoanOrderGoodsEntity goodsEntity : loanOrderGoodsEntities) {
                ExtraDTO extraDTO = new ExtraDTO();
                extraDTO.setOrderNo(goodsEntity.getOrderBusinessOrderNo());
                extraDTO.setOrderAmount(String.valueOf(goodsEntity.getProductAmount()));
                extraDTO.setProductCount(String.valueOf(goodsEntity.getProductCount()));
                extraDTO.setProductName(goodsEntity.getProductName());
                goods.add(extraDTO);
            }
            consumerPoliciesCheckReqDTO.setAmount(loanOrderDetailsEntity.getAmount());
            Map<String, Object> extra = new HashMap<>();
            extra.put("productInfos", goods);
            consumerPoliciesCheckReqDTO.setExtra(extra);
            consumerPoliciesCheckReqDTO.setGuaranteePaymentId(loanOrderDetailsEntity.getGuaranteePaymentId());
            consumerPoliciesCheckReqDTO.setOutOrderNo(loanOrderDetailsEntity.getSubBusinessOrderNo());
            try {
                log.info("订单确认调用银联发送消息>>>>>>>>>>>>>>>{}", JSONObject.toJSONString(consumerPoliciesCheckReqDTO));
                Result<ConsumerPoliciesCheckRespDTO> consumerPoliciesCheckRespDTOResult = unionPayService.mergeConsumerPoliciesCheck(consumerPoliciesCheckReqDTO);
                log.info("订单确认调用银联接收消息<<<<<<<<<<<<<<<{}", JSONObject.toJSONString(consumerPoliciesCheckRespDTOResult));
                if (consumerPoliciesCheckRespDTOResult.getCode() == NumberConstant.ZERO) {
                    loanOrderDetailsEntity.setConfirmStatus(NumberConstant.ONE);
                    this.loanOrderDetailsService.updateById(loanOrderDetailsEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void confirmOrder() {
        List<LoanOrderEntity> list = this.loanOrderService.listNotConfirmOrder();
        for (LoanOrderEntity orderEntity : list) {
            XxlJobHelper.log("未确认的订单:", JSONObject.toJSONString(orderEntity));
            confirmOrder(orderEntity);
        }
    }

    @Override
    public void applicationCallback() {
        List<LoanRequestApplicationRecordEntity> list = loanRequestApplicationRecordService.listError();
        if (CollectionUtil.isNotEmpty(list)) {
            XxlJobHelper.log("通知失败{}调", list.size());
        }
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(o -> {
                XxlJobHelper.log("重试回掉：{}x", o);
                loanRequestApplicationRecordService.retryNotice(o);
            });
        }

    }


    /**
     * 处理交易通知
     *
     * @param loanCallbackEntity 日志记录信息
     */
    private void detailsNotice(LoanCallbackEntity loanCallbackEntity) {
        String eventType = loanCallbackEntity.getEventType();
        if (UnionPayEventTypeConstant.ROOT_TRANSFER_DEPOSIT.equals(eventType)
                || UnionPayEventTypeConstant.TRANSFER_DEPOSIT.equals(eventType)
                || UnionPayEventTypeConstant.WITHDRAWAL_RETURN.equals(eventType)
                || UnionPayEventTypeConstant.LARGE_PAYMENT.equals(eventType)
                || UnionPayEventTypeConstant.LARGE_PAYMENT_DEPOSIT_REFUND_RETURN.equals(eventType)
                || UnionPayEventTypeConstant.TRANSFER_DEPOSIT_REFUND_RETURN.equals(eventType)) {
            //处理母账户入金
            balanceIncomeNotice(loanCallbackEntity.getEventData(), loanCallbackEntity.getId(), loanCallbackEntity.getEventType(), loanCallbackEntity.getEventId(), loanCallbackEntity.getCreatedAt());
        } else if (UnionPayEventTypeConstant.TRADE_RESULT.equals(eventType)) {
            //处理交易结果
            tradeResult(loanCallbackEntity);
        }
    }

    /**
     * 处理银联入金通知
     *
     * @param eventDataString 银联字符串
     * @param createdAt
     */
    public void balanceIncomeNotice(String eventDataString, Long id, String eventType, String eventId, String createdAt) {
        log.info(eventDataString);
        JSONObject jsonObject = JSONObject.parseObject(eventDataString);
        log.info(jsonObject.toJSONString());
        UnionPayIncomeDetailsDTO unionPayIncomeDTO = JSONObject.parseObject(eventDataString, UnionPayIncomeDetailsDTO.class);
        List<LoadBalanceNoticeEntity> list = payBalanceNoticeService.saveByEventDate(unionPayIncomeDTO, eventType, eventId, createdAt);
        loanRequestApplicationRecordService.noticeFmsIncomeNotice(list, UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_10, eventId, id);
    }

}
