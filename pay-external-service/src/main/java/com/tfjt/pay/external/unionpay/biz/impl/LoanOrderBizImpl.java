package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tfjt.pay.external.unionpay.biz.LoanOrderBiz;
import com.tfjt.pay.external.unionpay.constants.CommonConstants;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.UnionPayTradeResultCodeConstant;
import com.tfjt.pay.external.unionpay.dto.ExtraDTO;
import com.tfjt.pay.external.unionpay.dto.GuaranteePaymentDTO;
import com.tfjt.pay.external.unionpay.dto.req.*;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesRespDTO;
import com.tfjt.pay.external.unionpay.entity.LoanOrderDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;
import com.tfjt.pay.external.unionpay.entity.LoanOrderGoodsEntity;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.enums.UnionPayBusinessTypeEnum;
import com.tfjt.pay.external.unionpay.service.LoanOrderDetailsService;
import com.tfjt.pay.external.unionpay.service.LoanOrderGoodsService;
import com.tfjt.pay.external.unionpay.service.LoanOrderService;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.util.InstructIdUtil;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author songx
 * @date 2023-08-21 18:49
 * @email 598482054@qq.com
 */
@Slf4j
@Service
public class LoanOrderBizImpl implements LoanOrderBiz {

    @Resource
    private LoanOrderService orderService;
    @Resource
    private LoanOrderGoodsService loanOrderGoodsService;
    @Resource
    private LoanOrderDetailsService loanOrderDetailsService;

    @Resource
    private LoanUserService userService;

    @Resource
    private RedisCache redisCache;


    @Transactional(rollbackFor = {TfException.class, Exception.class})
    @Override
    public void transferSaveOrder(LoanTransferRespDTO payTransferDTO, String tradeOrderNo) {
        Date date = new Date();
        //保存订单 order
        LoanOrderEntity orderEntity = new LoanOrderEntity();
        orderEntity.setTradeOrderNo(tradeOrderNo);
        orderEntity.setBusinessOrderNo(payTransferDTO.getBusinessOrderNo());
        orderEntity.setPayBalanceAcctId(payTransferDTO.getOutBalanceAcctId());
        orderEntity.setPayBalanceAcctName(payTransferDTO.getOutBalanceAcctName());
        orderEntity.setCreateAt(date);
        orderEntity.setAppId(payTransferDTO.getAppId());
        orderEntity.setBusinessType(Integer.valueOf(UnionPayBusinessTypeEnum.TRANSFER.getCode()));
        orderEntity.setLoanUserId(userService.getLoanUserIdByBalanceAccId(payTransferDTO.getOutBalanceAcctId()));
        orderEntity.setAmount(payTransferDTO.getAmount());
        if (!this.orderService.save(orderEntity)) {
            log.error("保存转账订单信息失败:{}", JSONObject.toJSONString(orderEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        //保存订单详情 order_details
        LoanOrderDetailsEntity orderDetailsEntity = new LoanOrderDetailsEntity();
        orderDetailsEntity.setAmount(payTransferDTO.getAmount());
        orderDetailsEntity.setRecvBalanceAcctId(payTransferDTO.getInBalanceAcctId());
        orderDetailsEntity.setRecvBalanceAcctName(payTransferDTO.getInBalanceAcctName());
        orderDetailsEntity.setRemark("转账");
        orderDetailsEntity.setOrderId(orderEntity.getId());
        orderDetailsEntity.setSubBusinessOrderNo(payTransferDTO.getBusinessOrderNo());
        orderDetailsEntity.setCreatedAt(date);
        orderDetailsEntity.setAppId(payTransferDTO.getAppId());
        orderDetailsEntity.setPayBalanceAcctId(payTransferDTO.getOutBalanceAcctId());
        orderDetailsEntity.setTradeOrderNo(tradeOrderNo);
        orderDetailsEntity.setRecvLoanUserId(userService.getLoanUserIdByBalanceAccId(payTransferDTO.getInBalanceAcctId()));
        orderDetailsEntity.setPayLoanUserId(orderEntity.getLoanUserId());

        if (!this.loanOrderDetailsService.save(orderDetailsEntity)) {
            log.error("保存转账订单收款详情失败:{}", JSONObject.toJSONString(orderEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        //保存订单详情商品信息 order_goods
        LoanOrderGoodsEntity orderGoodsEntity = new LoanOrderGoodsEntity();
        orderGoodsEntity.setAppId(payTransferDTO.getAppId());
        orderGoodsEntity.setPayBalanceAcctId(payTransferDTO.getOutBalanceAcctId());
        orderGoodsEntity.setRecvBalanceAcctId(payTransferDTO.getInBalanceAcctId());
        orderGoodsEntity.setCreateAt(date);
        orderGoodsEntity.setProductName("转账");
        orderGoodsEntity.setOrderBusinessOrderNo(payTransferDTO.getBusinessOrderNo());
        orderGoodsEntity.setProductCount(NumberConstant.ONE);
        orderGoodsEntity.setProductAmount(payTransferDTO.getAmount());
        orderGoodsEntity.setDetailsId(orderDetailsEntity.getId());
        orderGoodsEntity.setPayLoanUserId(orderDetailsEntity.getPayLoanUserId());
        orderGoodsEntity.setRecvLoanUserId(orderDetailsEntity.getRecvLoanUserId());
        if (!this.loanOrderGoodsService.save(orderGoodsEntity)) {
            log.error("保存转账商品详情失败:{}", JSONObject.toJSONString(orderEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }

    @Override
    public boolean checkExistBusinessOrderNo(String businessOrderNo, String appId) {
        return orderService.checkExistBusinessOrderNo(businessOrderNo,appId);
    }

    @Override
    public LoanOrderEntity getByBusinessAndAppId(String businessOrderNo, String appId) {
        LambdaQueryWrapper<LoanOrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoanOrderEntity::getBusinessOrderNo, businessOrderNo)
                .eq(LoanOrderEntity::getAppId, appId);
        return this.orderService.getOne(queryWrapper);
    }

    /**
     * 保存下单商品信息
     * 并生成调用银联下单接口参数
     *
     * @param loanOrderUnifiedorderDTO 商品订单信息
     * @param notifyUrl
     * @return 调用银联参数
     */
    @Override
    @Transactional(rollbackFor = {TfException.class, Exception.class}, propagation = Propagation.REQUIRES_NEW)
    public ConsumerPoliciesReqDTO unifiedorderSaveOrderAndBuildUnionPayParam(LoanOrderUnifiedorderReqDTO loanOrderUnifiedorderDTO, String notifyUrl) {
        String generatedOrderNumber = InstructIdUtil.getInstructId(CommonConstants.TRANSACTION_TYPE_MK_ORDER,new Date(), UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_60,redisCache);

        ConsumerPoliciesReqDTO consumerPoliciesReqDTO = new ConsumerPoliciesReqDTO();
        consumerPoliciesReqDTO.setPayBalanceAcctId(loanOrderUnifiedorderDTO.getPayBalanceAcctId());
        consumerPoliciesReqDTO.setCombinedOutOrderNo(generatedOrderNumber);
        Date date = new Date();
        LoanOrderEntity orderEntity = new LoanOrderEntity();
        BeanUtil.copyProperties(loanOrderUnifiedorderDTO, orderEntity);
        orderEntity.setBusinessType(Integer.valueOf(UnionPayBusinessTypeEnum.UNIFIEDORDER.getCode()));
        orderEntity.setCreateAt(date);
        orderEntity.setTradeOrderNo(generatedOrderNumber);
        orderEntity.setLoanUserId(userService.getLoanUserIdByBalanceAccId(orderEntity.getPayBalanceAcctId()));
        if (!this.orderService.save(orderEntity)) {
            log.error("保存贷款订单信息失败:{}", JSONObject.toJSONString(orderEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        List<GuaranteePaymentDTO> list = new ArrayList<>();
        List<LoanOrderDetailsReqDTO> detailsDTOList = loanOrderUnifiedorderDTO.getDetailsDTOList();
        for (LoanOrderDetailsReqDTO loanOrderDetailsReqDTO : detailsDTOList) {
            LoanOrderDetailsEntity orderDetailsEntity = new LoanOrderDetailsEntity();
            BeanUtil.copyProperties(loanOrderDetailsReqDTO, orderDetailsEntity);
            orderDetailsEntity.setOrderId(orderEntity.getId());
            String orderNo = InstructIdUtil.getInstructId(CommonConstants.TRANSACTION_TYPE_MK_ORDER_SUB,new Date(), UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_60,redisCache);
            orderDetailsEntity.setTradeOrderNo(orderNo);
            orderDetailsEntity.setPayBalanceAcctId(orderEntity.getPayBalanceAcctId());
            orderDetailsEntity.setAppId(orderDetailsEntity.getAppId());
            orderDetailsEntity.setCreatedAt(date);
            orderDetailsEntity.setPayLoanUserId(orderEntity.getLoanUserId());
            orderDetailsEntity.setRecvLoanUserId(userService.getLoanUserIdByBalanceAccId(orderDetailsEntity.getRecvBalanceAcctId()));
            if (!this.loanOrderDetailsService.save(orderDetailsEntity)) {
                log.error("保存贷款订单详情信息失败:{}", JSONObject.toJSONString(orderDetailsEntity));
                throw new TfException(ExceptionCodeEnum.FAIL);
            }
            GuaranteePaymentDTO guaranteePaymentDTO = new GuaranteePaymentDTO();
            guaranteePaymentDTO.setAmount(loanOrderDetailsReqDTO.getAmount());
            //guaranteePaymentDTO.setPayBalanceAcctId(orderDetailsEntity.getPayBalanceAcctId());
            guaranteePaymentDTO.setRecvBalanceAcctId(orderDetailsEntity.getRecvBalanceAcctId());
            guaranteePaymentDTO.setOutOrderNo(orderDetailsEntity.getTradeOrderNo());
            guaranteePaymentDTO.setRecvBalanceAcctId(guaranteePaymentDTO.getRecvBalanceAcctId());
            List<ExtraDTO> listGoods = new ArrayList<>();

            List<LoanOrderGoodsReqDTO> goodsDTOList = loanOrderDetailsReqDTO.getGoodsDTOList();
            for (LoanOrderGoodsReqDTO loanOrderGoodsReqDTO : goodsDTOList) {
                LoanOrderGoodsEntity orderGoodsEntity = new LoanOrderGoodsEntity();
                BeanUtil.copyProperties(loanOrderGoodsReqDTO, orderGoodsEntity);
                orderGoodsEntity.setDetailsId(orderDetailsEntity.getId());
                orderGoodsEntity.setOrderBusinessOrderNo(loanOrderGoodsReqDTO.getOrderNo());
                orderGoodsEntity.setAppId(loanOrderUnifiedorderDTO.getAppId());
                orderGoodsEntity.setPayBalanceAcctId(loanOrderUnifiedorderDTO.getPayBalanceAcctId());
                orderGoodsEntity.setRecvBalanceAcctId(loanOrderDetailsReqDTO.getRecvBalanceAcctId());
                orderGoodsEntity.setPayLoanUserId(orderDetailsEntity.getPayLoanUserId());
                orderGoodsEntity.setRecvLoanUserId(orderDetailsEntity.getRecvLoanUserId());
                orderGoodsEntity.setCreateAt(date);

                ExtraDTO extraDTO = new ExtraDTO();
                extraDTO.setOrderNo(orderGoodsEntity.getOrderBusinessOrderNo());
                extraDTO.setOrderAmount(String.valueOf(orderGoodsEntity.getProductAmount()));
                extraDTO.setProductName(orderGoodsEntity.getProductName());
                extraDTO.setProductCount(String.valueOf(orderGoodsEntity.getProductCount()));
                listGoods.add(extraDTO);
                if (!this.loanOrderGoodsService.save(orderGoodsEntity)) {
                    log.error("保存贷款订单商品信息失败:{}", JSONObject.toJSONString(orderGoodsEntity));
                    throw new TfException(ExceptionCodeEnum.FAIL);
                }
            }
            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            stringObjectHashMap.put("productInfos", listGoods);
            guaranteePaymentDTO.setExtra(stringObjectHashMap);
            list.add(guaranteePaymentDTO);
        }
        consumerPoliciesReqDTO.setGuaranteePaymentParams(list);
        HashMap<String, Object> extra = new HashMap<>();
        extra.put("notifyUrl", notifyUrl);
        consumerPoliciesReqDTO.setExtra(extra);
        return consumerPoliciesReqDTO;
    }

    @Transactional(rollbackFor = {TfException.class, Exception.class})
    public void saveMergeConsumerResult(Result<ConsumerPoliciesRespDTO> result,String appId) {
        log.debug("执行修改订的线程信息:{}", Thread.currentThread().getName());
        ConsumerPoliciesRespDTO data = result.getData();
        String status = data.getStatus();
        Date finshDate = null;
        if (StringUtil.isNotBlank(data.getFinishedAt())) {
            try {
                finshDate = DateUtil.parseDate(data.getFinishedAt(), DateUtil.YYYY_MM_DD_T_HH_MM_SS_SSSXXX);
            } catch (ParseException e) {
                log.error("解析完成时间异常:{}", data.getFinishedAt());
            }
        }
        LambdaQueryWrapper<LoanOrderEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoanOrderEntity::getTradeOrderNo, data.getCombinedOutOrderNo())
                .eq(LoanOrderEntity::getAppId,appId);
        LoanOrderEntity one = this.orderService.getOne(wrapper);
        //修改订单状态
        LoanOrderEntity loanOrderEntity = new LoanOrderEntity();
        loanOrderEntity.setCombinedGuaranteePaymentId(data.getCombinedGuaranteePaymentId());
        loanOrderEntity.setStatus(status);
        loanOrderEntity.setFinishedAt(finshDate);
        loanOrderEntity.setId(one.getId());

        if (!this.orderService.updateById(loanOrderEntity)) {
            log.error("更新订单状态失败:{},交易订单号:{}", JSONObject.toJSONString(loanOrderEntity), data.getCombinedOutOrderNo());
            return;
        }
        List<GuaranteePaymentDTO> guaranteePaymentResults = data.getGuaranteePaymentResults();
        for (GuaranteePaymentDTO guaranteePaymentResult : guaranteePaymentResults) {
            LoanOrderDetailsEntity orderDetailsEntity = new LoanOrderDetailsEntity();
            BeanUtil.copyProperties(guaranteePaymentResult, orderDetailsEntity);
            orderDetailsEntity.setFinishedAt(finshDate);
            LambdaUpdateWrapper<LoanOrderDetailsEntity> detailsUpdateWrapper = new LambdaUpdateWrapper<>();
            detailsUpdateWrapper.eq(LoanOrderDetailsEntity::getTradeOrderNo, guaranteePaymentResult.getOutOrderNo())
                    .eq(LoanOrderDetailsEntity::getOrderId,one.getId());
            if (!this.loanOrderDetailsService.update(orderDetailsEntity, detailsUpdateWrapper)) {
                log.error("更新订单详细信息失败:{},交易订单号:{}", JSONObject.toJSONString(orderDetailsEntity), guaranteePaymentResult.getOutOrderNo());
                throw new TfException(PayExceptionCodeEnum.DATABASE_UPDATE_FAIL);
            }
        }
    }
}
