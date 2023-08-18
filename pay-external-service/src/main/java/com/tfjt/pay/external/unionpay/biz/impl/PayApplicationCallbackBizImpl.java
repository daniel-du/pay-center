package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.biz.PayApplicationCallbackBiz;
import com.tfjt.pay.external.unionpay.constants.UnionPayTradeResultConstant;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author songx
 * @date 2023-08-18 18:18
 * @email 598482054@qq.com
 */
@Slf4j
@Component
public class PayApplicationCallbackBizImpl implements PayApplicationCallbackBiz {

    @Override
    public boolean noticeShop(LoanOrderEntity orderEntity) {
        //String appId = orderEntity.getAppId();
        JSONObject param = new JSONObject();
        param.put("out_trade_no",orderEntity.getBusinessOrderNo());
        param.put("transaction_id",orderEntity.getCombinedGuaranteePaymentId());
        param.put("result_code", UnionPayTradeResultConstant.SUCCEEDED.equals(orderEntity.getStatus())?"TRADE_SUCCESS":"TRADE_ERROR");
        param.put("trade_type","loanType");
        param.put("total_fee",orderEntity.getAmount());
        String post = HttpUtil.post("", param.toJSONString());
        return "success".equalsIgnoreCase(post);
    }
}
