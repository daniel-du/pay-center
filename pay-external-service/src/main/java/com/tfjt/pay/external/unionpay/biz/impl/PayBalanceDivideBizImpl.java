package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayBalanceDivideReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.SubBalanceDivideRespDTO;
import com.tfjt.pay.external.unionpay.biz.PayBalanceDivideBiz;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.CommonConstants;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.UnionPayTradeResultCodeConstant;
import com.tfjt.pay.external.unionpay.dto.UnionPayProduct;
import com.tfjt.pay.external.unionpay.dto.req.BalanceDivideReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.SubBalanceDivideReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayDivideReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayDivideSubReq;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayDivideRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayDivideRespDetailDTO;
import com.tfjt.pay.external.unionpay.entity.LoadBalanceDivideEntity;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceDivideDetailsEntity;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.LoanBalanceDivideDetailsService;
import com.tfjt.pay.external.unionpay.service.LoanBalanceDivideService;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.util.InstructIdUtil;
import com.tfjt.tfcommon.core.util.SpringContextUtils;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

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
    private LoanUserService userService;

    @Resource
    private UnionPayService unionPayService;

    @Resource
    private RedisCache redisCache;

    @Value("${unionPay.loan.notifyUrl}")
    private String notifyUrl;

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
        payBalanceDivideEntity.setShopAppId(balanceDivideReqDTO.getShopAppId());
        payBalanceDivideEntity.setFmsAppId(balanceDivideReqDTO.getFmsAppId());
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
        wrapper.eq(LoadBalanceDivideEntity::getFmsAppId,appId)
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

    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#balanceDivideReq.businessOrderNo"}, expire = 10000, acquireTimeout = 3000)
    @Override
    public Result<Map<String, SubBalanceDivideRespDTO>> balanceDivide(UnionPayBalanceDivideReqDTO balanceDivideReq) {
        log.info("请求分账参数<<<<<<<<<<<<<<<<:{}", JSONObject.toJSONString(balanceDivideReq));
        ValidatorUtils.validateEntity(balanceDivideReq);
        String tradeOrderNo = InstructIdUtil.getInstructId(CommonConstants.TRANSACTION_TYPE_DB, new Date(), UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_51, redisCache);
        String businessOrderNo = balanceDivideReq.getBusinessOrderNo();
        //1.检验单号是否存在
        this.checkExistBusinessOrderNo(businessOrderNo);
        List<LoanBalanceDivideDetailsEntity> saveList = new ArrayList<>();
        BalanceDivideReqDTO balanceDivideReqDTO = new BalanceDivideReqDTO();
        BeanUtil.copyProperties(balanceDivideReq, balanceDivideReqDTO);
        PayBalanceDivideBizImpl bean = SpringContextUtils.getBean(this.getClass());
        bean.saveDivide(tradeOrderNo, saveList, balanceDivideReqDTO);
        UnionPayDivideReqDTO unionPayDivideReqDTO = buildBalanceDivideUnionPayParam(saveList, tradeOrderNo);
        //5.调用银联接口
        log.info("调用银联分账信息发送信息>>>>>>>>>>>>>>>:{}", JSONObject.toJSONString(unionPayDivideReqDTO));
        Result<UnionPayDivideRespDTO> result = unionPayService.balanceDivide(unionPayDivideReqDTO);
        log.info("调用银联分账信息返回信息<<<<<<<<<<<<<<<:{}", JSONObject.toJSONString(result));
        if (result.getCode() != NumberConstant.ZERO) {
            log.error("调用银联分账接口失败");
            return Result.failed(result.getMsg());
        }
        this.updateByUnionPayDivideReqDTO(result.getData(), balanceDivideReq.getFmsAppId());
        //6.解析返回数据响应给业务系统
        return Result.ok(parseUnionPayDivideReqDTOToMap(result.getData()));
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
        String tradeOrderNo = InstructIdUtil.getInstructId(CommonConstants.TRANSACTION_TYPE_DBS,new Date(), UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_51,redisCache);
        payBalanceDivideDetailsEntity.setSubTradeOrderNo(tradeOrderNo);
        payBalanceDivideDetailsEntity.setCreateTime(date);
        payBalanceDivideDetailsEntity.setLoanUserId(userService.getLoanUserIdByBalanceAccId(subBalanceDivideReqDTO.getRecvBalanceAcctId()));
        return payBalanceDivideDetailsEntity;
    }

    /**
     * 生成银联分账参数
     *
     * @param saveList     分账详情
     * @param tradeOrderNo 分账交易订单号
     */
    private UnionPayDivideReqDTO buildBalanceDivideUnionPayParam(List<LoanBalanceDivideDetailsEntity> saveList, String tradeOrderNo) {
        UnionPayDivideReqDTO unionPayDivideReqDTO = new UnionPayDivideReqDTO();
        unionPayDivideReqDTO.setPayBalanceAcctId(accountConfig.getBalanceAcctId());
        unionPayDivideReqDTO.setOutOrderNo(tradeOrderNo);
        List<UnionPayDivideSubReq> transferParams = new ArrayList<>(saveList.size());
        for (LoanBalanceDivideDetailsEntity payBalanceDivideEntity : saveList) {
            UnionPayDivideSubReq unionPayDivideSubReq = new UnionPayDivideSubReq();
            BeanUtil.copyProperties(payBalanceDivideEntity, unionPayDivideSubReq);
            UnionPayProduct unionPayProduct = new UnionPayProduct();
            unionPayProduct.setOrderAmount(payBalanceDivideEntity.getAmount());
            unionPayProduct.setProductName(payBalanceDivideEntity.getRecvBalanceAcctName() + "分账");
            unionPayProduct.setOrderNo(payBalanceDivideEntity.getSubBusinessOrderNo());
            unionPayProduct.setProductCount(NumberConstant.ONE);
            List<UnionPayProduct> list = new ArrayList<>(NumberConstant.ONE);
            list.add(unionPayProduct);
            HashMap<String, Object> extra = new HashMap<>();
            extra.put("productInfos", list);
            unionPayDivideSubReq.setExtra(extra);
            transferParams.add(unionPayDivideSubReq);
        }
        unionPayDivideReqDTO.setTransferParams(transferParams);
        HashMap<String, Object> extra = new HashMap<>();
        extra.put("notifyUrl", notifyUrl);
        unionPayDivideReqDTO.setExtra(extra);
        return unionPayDivideReqDTO;
    }

    /**
     * 解析银联返回数据给业务系统
     *
     * @param data 解析银联返回数据给业务系统
     * @return
     */
    private Map<String, SubBalanceDivideRespDTO> parseUnionPayDivideReqDTOToMap(UnionPayDivideRespDTO data) {
        Map<String, SubBalanceDivideRespDTO> map = new HashMap<>();
        for (UnionPayDivideRespDetailDTO transferResult : data.getTransferResults()) {
            SubBalanceDivideRespDTO subBalanceDivideReqDTO = new SubBalanceDivideRespDTO();
            BeanUtil.copyProperties(transferResult, subBalanceDivideReqDTO);
            map.put(transferResult.getRecvBalanceAcctId(), subBalanceDivideReqDTO);
        }
        return map;
    }


}
