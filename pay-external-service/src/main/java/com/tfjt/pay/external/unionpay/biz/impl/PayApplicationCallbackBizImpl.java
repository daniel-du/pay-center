package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.biz.PayApplicationCallbackBiz;
import com.tfjt.pay.external.unionpay.constants.TradeResultConstant;
import com.tfjt.pay.external.unionpay.constants.UnionPayTradeResultCodeConstant;
import com.tfjt.pay.external.unionpay.dto.resp.LoanOrderDetailsRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.LoanOrderUnifiedorderResqDTO;
import com.tfjt.pay.external.unionpay.entity.LoanOrderDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;
import com.tfjt.pay.external.unionpay.entity.LoanRequestApplicationRecordEntity;
import com.tfjt.pay.external.unionpay.entity.PayApplicationCallbackUrlEntity;
import com.tfjt.pay.external.unionpay.service.LoanOrderDetailsService;
import com.tfjt.pay.external.unionpay.service.LoanRequestApplicationRecordService;
import com.tfjt.pay.external.unionpay.service.PayApplicationCallbackUrlService;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author songx
 * @date 2023-08-18 18:18
 * @email 598482054@qq.com
 */
@Slf4j
@Component
public class PayApplicationCallbackBizImpl implements PayApplicationCallbackBiz {

    @Resource
    private PayApplicationCallbackUrlService payApplicationCallbackUrlService;

    @Resource
    private LoanOrderDetailsService loanOrderDetailsService;

    @Resource
    private LoanRequestApplicationRecordService recordService;

    @Override
    public boolean noticeShop(LoanOrderEntity orderEntity, String tradeResultCode, String noticeUrl) {
        if (StringUtil.isBlank(noticeUrl)) {
            LambdaQueryWrapper<PayApplicationCallbackUrlEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PayApplicationCallbackUrlEntity::getAppId, orderEntity.getAppId())
                    .eq(PayApplicationCallbackUrlEntity::getType, Integer.valueOf(tradeResultCode));
            PayApplicationCallbackUrlEntity one = this.payApplicationCallbackUrlService.getOne(wrapper);
            if (one == null) {
                throw new TfException(ExceptionCodeEnum.FAIL);
            }
            noticeUrl = one.getUrl();
        }

        LoanOrderUnifiedorderResqDTO loanOrderUnifiedorderResqDTO = new LoanOrderUnifiedorderResqDTO();
        BeanUtil.copyProperties(orderEntity, loanOrderUnifiedorderResqDTO);
        loanOrderUnifiedorderResqDTO.setStatus(TradeResultConstant.UNIONPAY_SUCCEEDED.equals(orderEntity.getStatus())?TradeResultConstant.PAY_SUCCESS:TradeResultConstant.PAY_FAILED);
        loanOrderUnifiedorderResqDTO.setTransactionId(orderEntity.getCombinedGuaranteePaymentId());
        LambdaQueryWrapper<LoanOrderDetailsEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoanOrderDetailsEntity::getOrderId, orderEntity.getId());
        List<LoanOrderDetailsEntity> list = loanOrderDetailsService.list(queryWrapper);
        List<LoanOrderDetailsRespDTO> detailsRespDTOS = new ArrayList<>();
        for (LoanOrderDetailsEntity orderDetailsEntity : list) {
            LoanOrderDetailsRespDTO loanOrderDetailsRespDTO = new LoanOrderDetailsRespDTO();
            BeanUtil.copyProperties(orderDetailsEntity, loanOrderDetailsRespDTO);
            detailsRespDTOS.add(loanOrderDetailsRespDTO);
        }
        loanOrderUnifiedorderResqDTO.setDetailsDTOList(detailsRespDTOS);
        LoanRequestApplicationRecordEntity record = new LoanRequestApplicationRecordEntity();
        record.setAppId(orderEntity.getAppId());
        String parameter = JSONObject.toJSONString(loanOrderUnifiedorderResqDTO);
        record.setRequestParam(parameter);
        record.setTradeOrderNo(orderEntity.getTradeOrderNo());
        record.setCreateTime(new Date());
        long start = System.currentTimeMillis();
        String result = "";
        try {
            log.info("发送商品交易通知>>>>>>>>>>>>>:{},请求参数:{}",noticeUrl,parameter);
            result = HttpUtil.post(noticeUrl, parameter);
            log.info("接受商品交易通知<<<<<<<<<<:{}",result);
            record.setResponseParam(result);
        } catch (Exception e) {
            record.setResponseParam(e.getMessage());
        }
        long end = System.currentTimeMillis();
        record.setResponseTime((int)(end-start));
        recordService.asyncSave(record);
        return "success".equalsIgnoreCase(result);
    }
}
