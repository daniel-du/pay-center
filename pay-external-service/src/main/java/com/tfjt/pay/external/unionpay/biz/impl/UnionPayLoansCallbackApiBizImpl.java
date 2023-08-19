package com.tfjt.pay.external.unionpay.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.biz.PayApplicationCallbackBiz;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansCallbackApiBiz;
import com.tfjt.pay.external.unionpay.config.ExecutorConfig;
import com.tfjt.pay.external.unionpay.constants.UnionPayEventTypeConstant;
import com.tfjt.pay.external.unionpay.constants.UnionPayTradeResultCodeConstant;
import com.tfjt.pay.external.unionpay.dto.EventDataDTO;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansBaseCallBackDTO;

import com.tfjt.pay.external.unionpay.entity.LoadBalanceNoticeEntity;
import com.tfjt.pay.external.unionpay.entity.LoanCallbackEntity;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;
import com.tfjt.pay.external.unionpay.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;

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
    private ExecutorConfig executorConfig;

    @Lock4j(keys = "#transactionCallBackReqDTO.eventId", expire = 3000, acquireTimeout = 3000)
    @Override
    public void commonCallback(UnionPayLoansBaseCallBackDTO transactionCallBackReqDTO, HttpServletResponse response) throws ParseException {
        LoanCallbackEntity tfLoanCallbackEntity = loanCallbackService.getOne(new LambdaQueryWrapper<LoanCallbackEntity>().eq(LoanCallbackEntity::getEventId, transactionCallBackReqDTO.getEventId()));
        if(tfLoanCallbackEntity!=null){
            log.info("事件回调已添加{}",transactionCallBackReqDTO.getEventId());
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
        log.info("保存回调日志信息:{}",JSONObject.toJSONString(loanCallbackEntity));
        executorConfig.asyncServiceExecutor().execute(detailsNotice(loanCallbackEntity));
    }

    /**
     * 处理交易通知
     * @param loanCallbackEntity 日志记录信息
     * @return 线程
     */
    private Runnable detailsNotice(LoanCallbackEntity loanCallbackEntity) {
        return () -> {
            if (UnionPayEventTypeConstant.ROOT_TRANSFER_DEPOSIT.equals(loanCallbackEntity.getEventType())) {
                //处理母账户入金
                balanceIncomeNotice(loanCallbackEntity.getEventData(),loanCallbackEntity.getId());
            }else if(UnionPayEventTypeConstant.TRADE_RESULT.equals(loanCallbackEntity.getEventType())){
                //处理交易结果
                treadResult(loanCallbackEntity);
            }
        };
    }

    /**
     * 交易结果通知
     * @param loanCallbackEntity
     */
    public void treadResult(LoanCallbackEntity loanCallbackEntity) {
        String eventData = loanCallbackEntity.getEventData();
        EventDataDTO eventDataDTO = JSONObject.parseObject(eventData,EventDataDTO.class);
        String tradeId = eventDataDTO.getTradeId();

        LoanCallbackEntity byId = loanCallbackService.getById(loanCallbackEntity.getId());
        // 下单回调
        if (UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_60.equals(tradeId)){
            LoanOrderEntity orderEntity = loanOrderService.treadResult(eventDataDTO);
            byId.setTreadOrderNo(orderEntity.getTradeOrderNo());
            if(payApplicationCallbackBiz.noticeShop(orderEntity,UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_60,byId.getNoticeUrl())){
                byId.setNoticeStatus(2);
            }else{
                byId.setNoticeStatus(1);
                byId.setNoticeErrorNumber(byId.getNoticeErrorNumber()+1);
            }
            this.loanCallbackService.updateById(byId);
        }
    }


    /**
     * 处理银联入金通知
     * @param eventDataString 银联字符串
     */
    public void balanceIncomeNotice(String eventDataString,Long id) {
        List<LoadBalanceNoticeEntity> list = payBalanceNoticeService.saveByEventDate(eventDataString);
        LoanCallbackEntity byId = loanCallbackService.getById(id);
        try{
            payBalanceNoticeService.noticeFms(list);
            byId.setNoticeStatus(2);
        } catch (Exception e){
            byId.setNoticeStatus(1);
            byId.setNoticeErrorNumber(byId.getNoticeErrorNumber()+1);
        }
        this.loanCallbackService.updateById(byId);
    }
}
