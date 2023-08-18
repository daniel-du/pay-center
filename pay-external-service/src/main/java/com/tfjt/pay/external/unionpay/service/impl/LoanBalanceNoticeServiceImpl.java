package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dao.LoanBalanceNoticeDao;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayIncomeDTO;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayIncomeDetailsDTO;
import com.tfjt.pay.external.unionpay.entity.LoadBalanceNoticeEntity;
import com.tfjt.pay.external.unionpay.service.LoanBalanceNoticeService;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("payBanlanceNoticeService")
public class LoanBalanceNoticeServiceImpl extends ServiceImpl<LoanBalanceNoticeDao, LoadBalanceNoticeEntity> implements LoanBalanceNoticeService {

    @Override
    public void noticeFms(List<LoadBalanceNoticeEntity> list) {
        HttpRequest request = HttpUtil.createPost("");
        HttpResponse execute = request.body(JSONObject.toJSONString(list)).timeout(5000).execute();
        if (execute.getStatus() != 200) {
            throw new TfException("");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<LoadBalanceNoticeEntity> saveByEventDate(String eventDataString) {
        UnionPayIncomeDTO unionPayIncomeDTO = JSONObject.parseObject(eventDataString, UnionPayIncomeDTO.class);
        List<UnionPayIncomeDetailsDTO> eventData = unionPayIncomeDTO.getEventData();
        List<LoadBalanceNoticeEntity> list = new ArrayList<>(eventData.size());
        for (UnionPayIncomeDetailsDTO eventDatum : eventData) {
            LoadBalanceNoticeEntity payBalanceNoticeEntity = new LoadBalanceNoticeEntity();
            BeanUtil.copyProperties(eventDatum, payBalanceNoticeEntity);
            payBalanceNoticeEntity.setEventId(unionPayIncomeDTO.getEventId());
            payBalanceNoticeEntity.setEventType(unionPayIncomeDTO.getEventType());
            payBalanceNoticeEntity.setCreatedAt(DateUtil.dealDateFormat(unionPayIncomeDTO.getCreatedAt()));
            payBalanceNoticeEntity.setCreateTime(new Date());
            list.add(payBalanceNoticeEntity);
        }
        if (!this.saveBatch(list)) {
            log.error("保存母账入金信息失败:{}", JSONObject.toJSONString(list));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        return list;
    }
}