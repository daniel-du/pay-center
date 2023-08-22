package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tfjt.pay.external.unionpay.biz.PayBalanceDivideBiz;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.TransactionTypeConstants;
import com.tfjt.pay.external.unionpay.dto.req.BalanceDivideReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayDivideRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayDivideRespDetailDTO;
import com.tfjt.pay.external.unionpay.entity.LoadBalanceDivideEntity;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceDivideDetailsEntity;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.LoanBalanceDivideDetailsService;
import com.tfjt.pay.external.unionpay.service.LoanBalanceDivideService;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.pay.external.unionpay.utils.OrderNumberUtil;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author songx
 * @date 2023-08-22 09:09
 * @email 598482054@qq.com
 */
@Slf4j
@Component
public class PayBalanceDivideBizImpl implements PayBalanceDivideBiz {

    @Resource
    private TfAccountConfig accountConfig;

    @Resource
    private LoanBalanceDivideService payBalanceDivideService;
    @Resource
    private LoanBalanceDivideDetailsService payBalanceDivideDetailsService;

    @Resource
    private OrderNumberUtil orderNumberUtil;
    @Transactional(rollbackFor = {TfException.class,Exception.class})
    @Override
    public void saveDivide(String tradeOrderNo, List<LoanBalanceDivideDetailsEntity> saveList, BalanceDivideReqDTO balanceDivideReqDTO) {

        Date date = new Date();
        //1.保存分账主信息信息
        //主交易单号,银联交互使用
        LoadBalanceDivideEntity payBalanceDivideEntity = new LoadBalanceDivideEntity();
        payBalanceDivideEntity.setPayBalanceAcctId(accountConfig.getBalanceAcctId());
        payBalanceDivideEntity.setTradeOrderNo(tradeOrderNo);
        payBalanceDivideEntity.setBusinessOrderNo(balanceDivideReqDTO.getBusinessOrderNo());
        payBalanceDivideEntity.setAppId(balanceDivideReqDTO.getAppId());
        payBalanceDivideEntity.setCreateAt(date);
        payBalanceDivideEntity.setPayBalanceAcctName(accountConfig.getBalanceAcctName());
        if (!payBalanceDivideService.save(payBalanceDivideEntity)) {
            log.error("保存分账主信息失败:{}", JSONObject.toJSONString(payBalanceDivideEntity));
            throw new TfException(PayExceptionCodeEnum.DATABASE_SAVE_FAIL);
        }
        //3.保存子分账信息
        List<SubBalanceDivideReqDTO> list = balanceDivideReqDTO.getList();


        for (SubBalanceDivideReqDTO subBalanceDivideReqDTO : list) {
            saveList.add(buildPayBalanceDivideDetailsEntity(subBalanceDivideReqDTO, date, payBalanceDivideEntity.getId()));
        }
        if (!this.payBalanceDivideDetailsService.saveBatch(saveList)) {
            log.error("保存分账信息失败:{}", JSONObject.toJSONString(saveList));
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new TfException(PayExceptionCodeEnum.DATABASE_SAVE_FAIL);
        }
    }

    @Override
    public void checkExistBusinessOrderNo(String businessOrderNo) {
        if (this.payBalanceDivideService.checkExistBusinessOrderNo(businessOrderNo)) {
            String message = String.format("业务订单号[%s]已经存在", businessOrderNo);
            log.error(message);
            throw new TfException(PayExceptionCodeEnum.TREAD_ORDER_NO_REPEAT);
        }
    }

    /**
     * 修改银联信息 DB2023082209500517906
     *
     * @param unionPayDivideRespDTO 分支银联返回数据
     * @param appId
     */
    @Async
    @Override
    public void updateByUnionPayDivideReqDTO(UnionPayDivideRespDTO unionPayDivideRespDTO, String appId) {
        LambdaQueryWrapper<LoadBalanceDivideEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoadBalanceDivideEntity::getAppId,appId)
                .eq(LoadBalanceDivideEntity::getTradeOrderNo,unionPayDivideRespDTO.getOutOrderNo());
        LoadBalanceDivideEntity one = this.payBalanceDivideService.getOne(wrapper);
        LoadBalanceDivideEntity update = new LoadBalanceDivideEntity();
        BeanUtil.copyProperties(unionPayDivideRespDTO, update);
        try {
            update.setFinishedAt(StringUtil.isNoneBlank(unionPayDivideRespDTO.getFinishedAt())? DateUtil.parseDate(unionPayDivideRespDTO.getFinishedAt(),DateUtil.YYYY_MM_DD_T_HH_MM_SS_SSSXXX):null);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        update.setId(one.getId());
        if (!this.payBalanceDivideService.updateById(update)) {
            log.error("更新分账主单据信息失败:{}", JSONObject.toJSONString(update));
        }
        List<UnionPayDivideRespDetailDTO> transferResults = unionPayDivideRespDTO.getTransferResults();
        for (UnionPayDivideRespDetailDTO transferResult : transferResults) {
            LoanBalanceDivideDetailsEntity payBalanceDivideDetailsEntity = new LoanBalanceDivideDetailsEntity();
            BeanUtil.copyProperties(transferResult, payBalanceDivideDetailsEntity);
            LambdaUpdateWrapper<LoanBalanceDivideDetailsEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(LoanBalanceDivideDetailsEntity::getRecvBalanceAcctId, transferResult.getRecvBalanceAcctId())
                    .eq(LoanBalanceDivideDetailsEntity::getDivideId,update.getId())
                    .eq(LoanBalanceDivideDetailsEntity::getAmount, transferResult.getAmount());
            if (!this.payBalanceDivideDetailsService.update(payBalanceDivideDetailsEntity, lambdaUpdateWrapper)) {
                log.error("更新子交易记录失败:{},该记录银联返回信息:{}",
                        JSONObject.toJSONString(payBalanceDivideDetailsEntity), JSONObject.toJSONString(transferResult));
            }
        }

    }

    /**
     * 创建分账信息
     *
     * @param subBalanceDivideReqDTO 分账信息
     * @param date                   创建时间
     */
    private LoanBalanceDivideDetailsEntity buildPayBalanceDivideDetailsEntity(SubBalanceDivideReqDTO subBalanceDivideReqDTO, Date date, Long divideId) {
        LoanBalanceDivideDetailsEntity payBalanceDivideDetailsEntity = new LoanBalanceDivideDetailsEntity();
        BeanUtil.copyProperties(subBalanceDivideReqDTO, payBalanceDivideDetailsEntity);
        payBalanceDivideDetailsEntity.setDivideId(divideId);
        payBalanceDivideDetailsEntity.setSubTradeOrderNo(orderNumberUtil.generateOrderNumber(TransactionTypeConstants.TRANSACTION_TYPE_DB_SUB));
        payBalanceDivideDetailsEntity.setCreateTime(date);
        return payBalanceDivideDetailsEntity;
    }


}
