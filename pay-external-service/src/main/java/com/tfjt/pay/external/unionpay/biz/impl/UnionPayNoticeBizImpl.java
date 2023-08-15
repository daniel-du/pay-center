package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.biz.UnionPayNoticeBiz;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayIncomeDTO;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayIncomeDetailsDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayBaseResp;

import com.tfjt.pay.external.unionpay.entity.PayBalanceNoticeEntity;
import com.tfjt.pay.external.unionpay.service.PayBalanceNoticeService;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
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
public class UnionPayNoticeBizImpl implements UnionPayNoticeBiz {

    @Autowired
    private PayBalanceNoticeService payBalanceNoticeService;
    @Override
    public void balanceIncomeNotice(UnionPayBaseResp unionPayBaseResp, HttpServletResponse response) {
        //
        // checkSign()
        String lwzRespData = unionPayBaseResp.getLwzRespData();
        UnionPayIncomeDTO unionPayIncomeDTO = JSONObject.parseObject(lwzRespData, UnionPayIncomeDTO.class);
        List<UnionPayIncomeDetailsDTO> eventData = unionPayIncomeDTO.getEventData();
        List<PayBalanceNoticeEntity> list = new ArrayList<>(eventData.size());
        for (UnionPayIncomeDetailsDTO eventDatum : eventData) {
            PayBalanceNoticeEntity payBalanceNoticeEntity = new PayBalanceNoticeEntity();
            BeanUtil.copyProperties(eventDatum,payBalanceNoticeEntity);
            payBalanceNoticeEntity.setEventId(unionPayIncomeDTO.getEventId());
            payBalanceNoticeEntity.setEventType(unionPayIncomeDTO.getEventType());
            payBalanceNoticeEntity.setCreatedAt(DateUtil.dealDateFormat(unionPayIncomeDTO.getCreatedAt()));
            payBalanceNoticeEntity.setCreateTime(new Date());
            list.add(payBalanceNoticeEntity);
        }
        if(this.payBalanceNoticeService.saveBatch(list)){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        //

    }
}
