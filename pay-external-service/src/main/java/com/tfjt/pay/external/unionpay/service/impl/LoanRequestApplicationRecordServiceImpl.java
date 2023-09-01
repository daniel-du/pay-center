package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.TradeResultConstant;
import com.tfjt.pay.external.unionpay.dao.LoanOrderDetailsDao;
import com.tfjt.pay.external.unionpay.dao.PayApplicationCallbackUrlDao;
import com.tfjt.pay.external.unionpay.dto.resp.LoanOrderDetailsRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.LoanOrderUnifiedorderResqDTO;
import com.tfjt.pay.external.unionpay.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.LoanRequestApplicationRecordDao;
import com.tfjt.pay.external.unionpay.service.LoanRequestApplicationRecordService;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service("loanRequestApplicationRecordService")
public class LoanRequestApplicationRecordServiceImpl extends ServiceImpl<LoanRequestApplicationRecordDao, LoanRequestApplicationRecordEntity> implements LoanRequestApplicationRecordService {
    @Value("${unionPay.loan.fms.app-id}")
    private String fmsAppId;

    @Value("${unionPay.loan.shop.app-id}")
    private String shopAppId;

    @Resource
    private LoanOrderDetailsDao loanOrderDetailsDao;


    @Resource
    private PayApplicationCallbackUrlDao payApplicationCallbackUrlDao;




    @Async
    @Override
    public void asyncSave(LoanRequestApplicationRecordEntity record) {
        this.save(record);
    }

    @Override
    public List<LoanRequestApplicationRecordEntity> listError() {
        DateTime dateTime = DateUtil.beginOfDay(DateUtil.date());
        return this.getBaseMapper().listError(dateTime);
    }



    @Async
    @Override
    public void noticeShop(LoanOrderEntity orderEntity, String eventType, Long callbackId) {
        //构建发送shop服务的参数信息
        LoanOrderUnifiedorderResqDTO loanOrderUnifiedorderResqDTO = new LoanOrderUnifiedorderResqDTO();
        BeanUtil.copyProperties(orderEntity, loanOrderUnifiedorderResqDTO);
        loanOrderUnifiedorderResqDTO.setResultCode(TradeResultConstant.UNIONPAY_SUCCEEDED.equals(orderEntity.getStatus()) ? TradeResultConstant.PAY_SUCCESS : TradeResultConstant.PAY_FAILED);
        loanOrderUnifiedorderResqDTO.setTransactionId(orderEntity.getCombinedGuaranteePaymentId());
        LambdaQueryWrapper<LoanOrderDetailsEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoanOrderDetailsEntity::getOrderId, orderEntity.getId());
        List<LoanOrderDetailsEntity> list = loanOrderDetailsDao.selectList(queryWrapper);
        List<LoanOrderDetailsRespDTO> detailsRespDTOS = new ArrayList<>();
        for (LoanOrderDetailsEntity orderDetailsEntity : list) {
            LoanOrderDetailsRespDTO loanOrderDetailsRespDTO = new LoanOrderDetailsRespDTO();
            BeanUtil.copyProperties(orderDetailsEntity, loanOrderDetailsRespDTO);
            detailsRespDTOS.add(loanOrderDetailsRespDTO);
        }
        loanOrderUnifiedorderResqDTO.setDetailsDTOList(detailsRespDTOS);
        loanOrderUnifiedorderResqDTO.setTotalFee(orderEntity.getAmount());
        loanOrderUnifiedorderResqDTO.setOutTradeNo(orderEntity.getBusinessOrderNo());
        loanOrderUnifiedorderResqDTO.setTradeType("loanPay");
        String parameter = JSONObject.toJSONString(loanOrderUnifiedorderResqDTO);
        sendRequest(orderEntity.getAppId(), parameter, orderEntity.getTradeOrderNo(), eventType, callbackId);
    }

    /**
     * 提现回掉通知业务
     *
     * @param withdrawalOrderEntity
     * @param id
     * @return
     */
    @Override
    public void noticeWithdrawalNotice(LoanWithdrawalOrderEntity withdrawalOrderEntity, String eventType, Long id) {
        log.info("提现回调");
        Map<String, Object> params = new HashMap<>();
        params.put("withdrawalOrderNo", withdrawalOrderEntity.getWithdrawalOrderNo());
        params.put("status", withdrawalOrderEntity.getStatus());
        sendRequest(withdrawalOrderEntity.getAppId(), JSONObject.toJSONString(params), withdrawalOrderEntity.getWithdrawalOrderNo(), eventType, id);
    }


    private boolean sendRequest(String appId, String parameter, String tradeOrderNo, String eventType, Long callbackId) {
        LambdaQueryWrapper<PayApplicationCallbackUrlEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayApplicationCallbackUrlEntity::getAppId,appId)
                .eq(PayApplicationCallbackUrlEntity::getType,eventType);
        PayApplicationCallbackUrlEntity payApplicationCallbackUrlEntity = payApplicationCallbackUrlDao.selectOne(queryWrapper);
        String callBackUrl = null;
        if (Objects.nonNull(payApplicationCallbackUrlEntity)){
            callBackUrl = payApplicationCallbackUrlEntity.getUrl();
        }
        return sendRequest(appId, parameter, tradeOrderNo, eventType, callbackId, callBackUrl);
    }

    /**
     * 发送请求并记录请求日志信息
     *
     * @param appId        请求的APPid
     * @param parameter    参数
     * @param tradeOrderNo 交易单号
     * @param eventType    通知类型
     * @param callbackId   关联银联回调记录表id
     */
    private boolean sendRequest(String appId, String parameter, String tradeOrderNo, String eventType, Long callbackId, String callBackUrl) {

        LoanRequestApplicationRecordEntity record = builderRecord(appId, parameter, tradeOrderNo, callbackId, callBackUrl, eventType);
        long start = System.currentTimeMillis();
        String result = "";
        try {
            log.info("应用服务发送交易通知>>>>>>>>>>>>>:{},请求参数:{},appId:{}", callBackUrl, parameter, appId);
            HttpResponse execute = HttpRequest.post(callBackUrl).timeout(5000).body(parameter).execute();
            result = execute.body();
            log.info("接受应用服务发送交易通知<<<<<<<<<<:{}",result);
            record.setResponseParam(result);
            record.setResponseCode(execute.getStatus());
        } catch (Exception e) {
            log.error("应用服务发送交易异常<<<<<<<<<<<<<<<:{},请求参数:{},appId:{},e:{}", callBackUrl, parameter, appId, e.getMessage());
            record.setResponseParam(e.getMessage());
            record.setResponseCode(HttpStatus.HTTP_INTERNAL_ERROR);
        }
        long end = System.currentTimeMillis();
        record.setResponseTime((int) (end - start));
        //异步记录请求日志
        boolean b = "success".equalsIgnoreCase(result);
        record.setCallbackStatus(b ? NumberConstant.ONE : NumberConstant.ZERO);
        this.asyncSave(record);
        return b;
    }

    /**
     * 构造通知的记录信息
     *
     * @param appId        应用APPid
     * @param parameter    发送参数
     * @param tradeOrderNo 业务id
     * @param callbackId   银联通知表id
     * @param callBackUrl  请求地址
     * @param eventType    事件类型 同银联回调类型
     * @return 通知记录信息
     */
    private LoanRequestApplicationRecordEntity builderRecord(String appId, String parameter, String tradeOrderNo, Long callbackId, String callBackUrl, String eventType) {
        LoanRequestApplicationRecordEntity record = new LoanRequestApplicationRecordEntity();
        record.setAppId(appId);
        record.setTradeType(eventType);
        record.setRequestParam(parameter);
        record.setTradeOrderNo(tradeOrderNo);
        record.setCreateTime(new Date());
        record.setCallbackId(callbackId);
        record.setRequestUrl(callBackUrl);
        return record;
    }
}