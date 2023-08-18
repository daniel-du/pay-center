package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.tfjt.pay.external.unionpay.biz.UnionPayCallbackBiz;
import com.tfjt.pay.external.unionpay.dto.EventDataDTO;
import com.tfjt.pay.external.unionpay.dto.req.TransactionCallBackReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayIncomeDTO;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayIncomeDetailsDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayBaseResp;

import com.tfjt.pay.external.unionpay.entity.LoadBalanceNoticeEntity;
import com.tfjt.pay.external.unionpay.entity.LoanNoticeRecordEntity;
import com.tfjt.pay.external.unionpay.service.LoanBalanceNoticeService;
import com.tfjt.pay.external.unionpay.service.LoanNoticeRecordService;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author songx
 * @date 2023-08-14 22:09
 * @email 598482054@qq.com
 */
@Slf4j
@Component
public class UnionPayCallbackBizImpl implements UnionPayCallbackBiz {

    @Autowired
    private LoanBalanceNoticeService payBalanceNoticeService;

    @Autowired
    private LoanNoticeRecordService loanNoticeRecordService;

    @Autowired
    private LoanNoticeRecordService recordService;


    @Override
    public void balanceIncomeNotice(UnionPayBaseResp unionPayBaseResp, HttpServletResponse response) {
        String lwzRespData = unionPayBaseResp.getLwzRespData();
        UnionPayIncomeDTO unionPayIncomeDTO = JSONObject.parseObject(lwzRespData, UnionPayIncomeDTO.class);
        List<UnionPayIncomeDetailsDTO> eventData = unionPayIncomeDTO.getEventData();
        List<LoadBalanceNoticeEntity> list = new ArrayList<>(eventData.size());
        for (UnionPayIncomeDetailsDTO eventDatum : eventData) {
            LoadBalanceNoticeEntity payBalanceNoticeEntity = new LoadBalanceNoticeEntity();
            BeanUtil.copyProperties(eventDatum,payBalanceNoticeEntity);
            payBalanceNoticeEntity.setEventId(unionPayIncomeDTO.getEventId());
            payBalanceNoticeEntity.setEventType(unionPayIncomeDTO.getEventType());
            payBalanceNoticeEntity.setCreatedAt(DateUtil.dealDateFormat(unionPayIncomeDTO.getCreatedAt()));
            payBalanceNoticeEntity.setCreateTime(new Date());
            list.add(payBalanceNoticeEntity);
        }
        if(this.payBalanceNoticeService.saveBatch(list)){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        payBalanceNoticeService.noticeFms(list);
    }

    @Lock4j(keys = "#transactionCallBackReqDTO.eventId",expire = 3000,acquireTimeout = 3000)
    @Override
    public void commonCallback(TransactionCallBackReqDTO transactionCallBackReqDTO, HttpServletResponse response) throws ParseException {
        String eventId = transactionCallBackReqDTO.getEventId();
        if(recordService.existEventId(eventId)){
            return ;
        }
        LoanNoticeRecordEntity loanNoticeRecordEntity = new LoanNoticeRecordEntity();
        EventDataDTO eventData = transactionCallBackReqDTO.getEventData();
        BeanUtil.copyProperties(eventData,loanNoticeRecordEntity);
        loanNoticeRecordEntity.setCreateAt(DateUtil.parseDate(transactionCallBackReqDTO.getCreatedAt(),DateUtil.YYYY_MM_DD_T_HH_MM_SS_SSSXXX));
        loanNoticeRecordEntity.setEventId(transactionCallBackReqDTO.getEventId());
        loanNoticeRecordEntity.setEventType(transactionCallBackReqDTO.getEventType());
        String finishedAt = eventData.getFinishedAt();
        if (StringUtil.isNoneBlank(finishedAt)){
            loanNoticeRecordEntity.setFinishedAt(DateUtil.parseDate(finishedAt,DateUtil.YYYY_MM_DD_T_HH_MM_SS_SSSXXX));
        }
        loanNoticeRecordEntity.setOrderNo(eventData.getOutOrderNo());
        if(!this.loanNoticeRecordService.save(loanNoticeRecordEntity)){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        //TODO 处理各自的业务逻辑
    }
}
