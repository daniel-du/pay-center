package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dao.LoanBalanceNoticeDao;
import com.tfjt.pay.external.unionpay.entity.LoadBalanceNoticeEntity;
import com.tfjt.pay.external.unionpay.service.LoanBalanceNoticeService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;


@Service("payBanlanceNoticeService")
public class LoanBalanceNoticeServiceImpl extends ServiceImpl<LoanBalanceNoticeDao, LoadBalanceNoticeEntity> implements LoanBalanceNoticeService {

    @Async
    @Override
    public void noticeFms(List<LoadBalanceNoticeEntity> list) {
        HttpUtil.post("", JSONObject.toJSONString(list), 3000);
        List<Long> ids = list.stream().map(LoadBalanceNoticeEntity::getId).collect(Collectors.toList());
        LambdaUpdateWrapper<LoadBalanceNoticeEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(LoadBalanceNoticeEntity::getNoticeFlag, NumberConstant.ONE)
                .in(LoadBalanceNoticeEntity::getId, ids);
        this.update(wrapper);

    }
}