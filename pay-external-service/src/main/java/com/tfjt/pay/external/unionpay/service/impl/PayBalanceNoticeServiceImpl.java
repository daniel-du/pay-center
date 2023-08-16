package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dao.PayBalanceNoticeDao;
import com.tfjt.pay.external.unionpay.entity.PayBalanceNoticeEntity;
import com.tfjt.pay.external.unionpay.service.PayBalanceNoticeService;
import com.tfjt.pay.external.unionpay.utils.HttpUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;


@Service("payBanlanceNoticeService")
public class PayBalanceNoticeServiceImpl extends ServiceImpl<PayBalanceNoticeDao, PayBalanceNoticeEntity> implements PayBalanceNoticeService {

    @Async
    @Override
    public void noticeFms(List<PayBalanceNoticeEntity> list) {
        HttpUtil.post("", JSONObject.toJSONString(list), 3000);
        List<Long> ids = list.stream().map(PayBalanceNoticeEntity::getId).collect(Collectors.toList());
        LambdaUpdateWrapper<PayBalanceNoticeEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(PayBalanceNoticeEntity::getNoticeFlag, NumberConstant.ONE)
                .in(PayBalanceNoticeEntity::getId, ids);
        this.update(wrapper);

    }
}