package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.dao.LoanBalanceNoticeDao;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayIncomeDetailsDTO;
import com.tfjt.pay.external.unionpay.entity.LoadBalanceNoticeEntity;
import com.tfjt.pay.external.unionpay.service.LoanBalanceNoticeService;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("payBanlanceNoticeService")
public class LoanBalanceNoticeServiceImpl extends ServiceImpl<LoanBalanceNoticeDao, LoadBalanceNoticeEntity> implements LoanBalanceNoticeService {



    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<LoadBalanceNoticeEntity> saveByEventDate(UnionPayIncomeDetailsDTO eventDatum, String eventType, String eventId, String createdAt) {

        List<LoadBalanceNoticeEntity> list = new ArrayList<>();
        LoadBalanceNoticeEntity payBalanceNoticeEntity = new LoadBalanceNoticeEntity();
        BeanUtil.copyProperties(eventDatum, payBalanceNoticeEntity);
        payBalanceNoticeEntity.setEventId(eventId);
        payBalanceNoticeEntity.setEventType(eventType);
        payBalanceNoticeEntity.setCreatedAt(DateUtil.dealDateFormat(createdAt));
        payBalanceNoticeEntity.setCreateTime(new Date());
        list.add(payBalanceNoticeEntity);
        if (!this.saveBatch(list)) {
            log.error("保存母账入金信息失败:{}", JSONObject.toJSONString(list));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        return list;
    }
}