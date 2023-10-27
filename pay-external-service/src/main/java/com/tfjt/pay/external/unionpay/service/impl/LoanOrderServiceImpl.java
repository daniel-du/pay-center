package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tfjt.pay.external.unionpay.dao.LoanOrderDetailsDao;
import com.tfjt.pay.external.unionpay.dao.LoanOrderGoodsDao;
import com.tfjt.pay.external.unionpay.dto.EventDataDTO;
import com.tfjt.pay.external.unionpay.entity.LoanOrderDetailsEntity;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.LoanOrderDao;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;
import com.tfjt.pay.external.unionpay.service.LoanOrderService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

@Slf4j
@Service("payLoanOrderService")
public class LoanOrderServiceImpl extends ServiceImpl<LoanOrderDao, LoanOrderEntity> implements LoanOrderService {
    @Resource
    private LoanOrderDetailsDao loanOrderDetailsDao;

    @Resource
    private LoanOrderGoodsDao loanOrderGoodsDao;

    @Resource
    private LoanOrderDao loanOrderDao;

    @Override
    public boolean checkExistBusinessOrderNo(String businessOrderNo, String appId) {
        LambdaQueryWrapper<LoanOrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoanOrderEntity::getAppId, appId)
                .eq(LoanOrderEntity::getBusinessOrderNo, businessOrderNo)
                .last("limit 1");
        return this.count(queryWrapper) > 0;
    }

    @Transactional(rollbackFor = {TfException.class,Exception.class})
    @Override
    public LoanOrderEntity treadResult(EventDataDTO eventDataDTO) {
        LambdaQueryWrapper<LoanOrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoanOrderEntity::getTradeOrderNo,eventDataDTO.getOutOrderNo());
        LoanOrderEntity orderEntity = this.getOne(queryWrapper);
        String status = eventDataDTO.getStatus();
        orderEntity.setStatus(status);
        try {
            Date finshDate = DateUtil.parseDate(eventDataDTO.getFinishedAt(),DateUtil.YYYY_MM_DD_T_HH_MM_SS_SSSXXX);
            orderEntity.setFinishedAt(finshDate);
            if(!this.updateById(orderEntity)){
                log.error("更新订单信息失败:{}", JSONObject.toJSONString(orderEntity));
                throw new TfException(ExceptionCodeEnum.FAIL);
            }
            LoanOrderDetailsEntity orderDetailsEntity = new LoanOrderDetailsEntity();
            orderDetailsEntity.setFinishedAt(finshDate);
            orderDetailsEntity.setStatus(status);
            LambdaUpdateWrapper<LoanOrderDetailsEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(LoanOrderDetailsEntity::getOrderId,orderEntity.getId());
            if(loanOrderDetailsDao.update(orderDetailsEntity,lambdaUpdateWrapper)<=0){
                log.error("更新订单详细信息失败:{},订单id:{}", JSONObject.toJSONString(orderDetailsEntity),orderEntity.getId());
                throw new TfException(ExceptionCodeEnum.FAIL);
            }
            return orderEntity;
        } catch (ParseException e) {
            log.error("解析银联日期失败:{}",eventDataDTO.getFinishedAt());
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }

    @Override
    public List<LoanOrderEntity> listNotConfirmOrder() {
        DateTime dateTime = cn.hutool.core.date.DateUtil.beginOfDay(cn.hutool.core.date.DateUtil.date());
        return this.getBaseMapper().listNotConfirmOrder(dateTime);
    }

    @Override
    public LoanOrderEntity getServiceFeeOrder(String outOrderNo) {
        return loanOrderDao.getServiceFeeOrder(outOrderNo);
    }

}
