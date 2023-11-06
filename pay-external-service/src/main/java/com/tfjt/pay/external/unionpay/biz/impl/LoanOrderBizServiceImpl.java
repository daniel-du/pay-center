package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayLoanOrderDetailsReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayLoanOrderUnifiedorderReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.LoanOrderDetailsRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.LoanQueryOrderRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.MergeConsumerRepDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.UnionPayTransferRespDTO;
import com.tfjt.pay.external.unionpay.biz.LoanOrderBizService;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.*;
import com.tfjt.pay.external.unionpay.dto.ExtraDTO;
import com.tfjt.pay.external.unionpay.dto.GuaranteePaymentDTO;
import com.tfjt.pay.external.unionpay.dto.req.*;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesRespDTO;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.enums.TransactionStatusEnum;
import com.tfjt.pay.external.unionpay.enums.UnionPayBusinessTypeEnum;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.util.InstructIdUtil;
import com.tfjt.tfcommon.core.util.SpringContextUtils;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author songx
 * @date 2023-08-21 18:49
 * @email 598482054@qq.com
 */
@Slf4j
@Component
public class LoanOrderBizServiceImpl implements LoanOrderBizService {

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

    @Resource
    private TfAccountConfig accountConfig;


    @Value("${unionPay.loan.notifyUrl}")
    private String notifyUrl;

    @Resource
    private UnionPayService unionPayService;


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
        redisCache.setCacheObject(buildRedisKey(orderEntity.getBusinessOrderNo(), orderEntity.getAppId()), orderEntity.getId());
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
        String generatedOrderNumber = InstructIdUtil.getInstructId(CommonConstants.TRANSACTION_TYPE_MK_ORDER, new Date(), UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_60, redisCache);

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
        List<LoanOrderDetailsReqDTO> detailsDTOList = loanOrderUnifiedorderDTO.getDetailsDTOList();
        orderEntity.setAmount(detailsDTOList.stream().mapToInt(LoanOrderDetailsReqDTO::getAmount).sum());
        if (!this.orderService.save(orderEntity)) {
            log.error("保存贷款订单信息失败:{}", JSONObject.toJSONString(orderEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        List<GuaranteePaymentDTO> list = new ArrayList<>();

        for (LoanOrderDetailsReqDTO loanOrderDetailsReqDTO : detailsDTOList) {
            LoanOrderDetailsEntity orderDetailsEntity = new LoanOrderDetailsEntity();
            BeanUtil.copyProperties(loanOrderDetailsReqDTO, orderDetailsEntity);
            orderDetailsEntity.setOrderId(orderEntity.getId());
            String orderNo = InstructIdUtil.getInstructId(CommonConstants.TRANSACTION_TYPE_MK_ORDER_SUB, new Date(), UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_60, redisCache);
            orderDetailsEntity.setTradeOrderNo(orderNo);
            orderDetailsEntity.setPayBalanceAcctId(orderEntity.getPayBalanceAcctId());
            orderDetailsEntity.setAppId(orderDetailsEntity.getAppId());
            orderDetailsEntity.setCreatedAt(date);
            orderDetailsEntity.setPayLoanUserId(orderEntity.getLoanUserId());

            if (orderDetailsEntity.getRecvBalanceAcctId().equals(accountConfig.getBalanceAcctId())) {
                orderDetailsEntity.setRecvBalanceAcctName(accountConfig.getBalanceAcctName());
            } else {
                LoanUserEntity user = userService.getByBalanceAcctId(orderDetailsEntity.getRecvBalanceAcctId());
                if (user == null) {
                    throw new TfException(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
                }
                orderDetailsEntity.setRecvLoanUserId(user.getId());
                orderDetailsEntity.setRecvBalanceAcctName(user.getName());
            }
            if (!this.loanOrderDetailsService.save(orderDetailsEntity)) {
                log.error("保存贷款订单详情信息失败:{}", JSONObject.toJSONString(orderDetailsEntity));
                throw new TfException(ExceptionCodeEnum.FAIL);
            }
            GuaranteePaymentDTO guaranteePaymentDTO = new GuaranteePaymentDTO();
            guaranteePaymentDTO.setAmount(loanOrderDetailsReqDTO.getAmount());
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
                if (orderGoodsEntity.getProductAmount()>NumberConstant.ZERO){
                    ExtraDTO extraDTO = new ExtraDTO();
                    extraDTO.setOrderNo(orderGoodsEntity.getOrderBusinessOrderNo());
                    extraDTO.setOrderAmount(String.valueOf(orderGoodsEntity.getProductAmount()));
                    extraDTO.setProductName(orderGoodsEntity.getProductName());
                    extraDTO.setProductCount(String.valueOf(orderGoodsEntity.getProductCount()));
                    listGoods.add(extraDTO);
                }
                if (!this.loanOrderGoodsService.save(orderGoodsEntity)) {
                    log.error("保存贷款订单商品信息失败:{}", JSONObject.toJSONString(orderGoodsEntity));
                    throw new TfException(ExceptionCodeEnum.FAIL);
                }
            }
            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            stringObjectHashMap.put("productInfos", listGoods);
            guaranteePaymentDTO.setExtra(stringObjectHashMap);
            //金额是0不调用银联
            if(Objects.equals(loanOrderDetailsReqDTO.getAmount(), NumberConstant.ZERO)){
                continue;
            }
            list.add(guaranteePaymentDTO);
        }
        consumerPoliciesReqDTO.setGuaranteePaymentParams(list);
        HashMap<String, Object> extra = new HashMap<>();
        extra.put("notifyUrl", notifyUrl);
        consumerPoliciesReqDTO.setExtra(extra);
        //单号信息缓存在redis中,并保持24个小时
        redisCache.setCacheObject(buildRedisKey(loanOrderUnifiedorderDTO.getBusinessOrderNo(), loanOrderUnifiedorderDTO.getAppId()), orderEntity.getId(), 24, TimeUnit.HOURS);
        return consumerPoliciesReqDTO;
    }

    @Override
    public List<LoanOrderDetailsEntity> listOrderDetailByOrderId(Long id) {
        LambdaQueryWrapper<LoanOrderDetailsEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoanOrderDetailsEntity::getOrderId, id);
        return this.loanOrderDetailsService.list(queryWrapper);
    }

    @Override
    public List<LoanOrderDetailsRespDTO> listLoanOrderDetailsRespDTO(Long id) {
        List<LoanOrderDetailsRespDTO> details_dto_list = new ArrayList<>();
        List<LoanOrderDetailsEntity> loanOrderDetailsEntities = this.listOrderDetailByOrderId(id);
        for (LoanOrderDetailsEntity loanOrderDetailsEntity : loanOrderDetailsEntities) {
            LoanOrderDetailsRespDTO dto = new LoanOrderDetailsRespDTO();
            dto.setMetadata(loanOrderDetailsEntity.getMetadata());
            dto.setRecv_balance_acct_id(loanOrderDetailsEntity.getRecvBalanceAcctId());
            dto.setAmount(loanOrderDetailsEntity.getAmount());
            dto.setSub_business_order_no(loanOrderDetailsEntity.getSubBusinessOrderNo());
            dto.setRecv_balance_acct_name(loanOrderDetailsEntity.getRecvBalanceAcctName());
            details_dto_list.add(dto);
        }
        return details_dto_list;
    }

    /*************调整优化代码开始    *******************/
    @Lock4j(keys = "#loanOrderUnifiedorderDTO.businessOrderNo", expire = 10000)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<MergeConsumerRepDTO> unifiedorder(UnionPayLoanOrderUnifiedorderReqDTO loanOrderUnifiedorderDTO) {
        log.info("下单参数:{}", JSONObject.toJSONString(loanOrderUnifiedorderDTO));
        ValidatorUtils.validateEntity(loanOrderUnifiedorderDTO);
        //1.判断单号是否存在
        if (this.checkExistBusinessOrderNo(loanOrderUnifiedorderDTO.getBusinessOrderNo(), loanOrderUnifiedorderDTO.getAppId())) {
            log.info("业务单号已存在:{},appId:{}", loanOrderUnifiedorderDTO.getBusinessOrderNo(), loanOrderUnifiedorderDTO.getAppId());
            return Result.failed(PayExceptionCodeEnum.TREAD_ORDER_NO_REPEAT);
        }
        //2.判断付款用户金额是否足够
        List<UnionPayLoanOrderDetailsReqDTO> detailsDTOList = loanOrderUnifiedorderDTO.getDetailsDTOList();
        int totalAmount = detailsDTOList.stream().mapToInt(UnionPayLoanOrderDetailsReqDTO::getAmount).sum();
        userService.checkLoanAccount(loanOrderUnifiedorderDTO.getPayBalanceAcctId(), totalAmount, loanOrderUnifiedorderDTO.getPayBalanceAcctName());
        //2.保存订单信息
        LoanOrderUnifiedorderReqDTO loanOrderUnifiedorderReqDTO = new LoanOrderUnifiedorderReqDTO();
        BeanUtil.copyProperties(loanOrderUnifiedorderDTO, loanOrderUnifiedorderReqDTO);
        LoanOrderBizService bean = SpringContextUtils.getBean(this.getClass());
        //3.调用银联接口
        ConsumerPoliciesReqDTO consumerPoliciesReqDTO = bean.unifiedorderSaveOrderAndBuildUnionPayParam(loanOrderUnifiedorderReqDTO, notifyUrl);
        log.info("贷款支付参数{}", JSONObject.toJSONString(consumerPoliciesReqDTO));
        Result<ConsumerPoliciesRespDTO> result = unionPayService.mergeConsumerPolicies(consumerPoliciesReqDTO);
        if (result.getCode() == ExceptionCodeEnum.FAIL.getCode()) {
            log.error("调用银联接口失败:{}", result.getMsg());
            saveMergeConsumerFailResult(consumerPoliciesReqDTO.getCombinedOutOrderNo(), result.getMsg(), loanOrderUnifiedorderDTO.getAppId());
            return Result.failed(result.getMsg());
        }
        //保存银联返回信息
        bean.saveMergeConsumerResult(result, loanOrderUnifiedorderDTO.getAppId());
        MergeConsumerRepDTO mergeConsumerRepDTO = new MergeConsumerRepDTO();
        mergeConsumerRepDTO.setBusinessOrderNo(loanOrderUnifiedorderDTO.getBusinessOrderNo());
        mergeConsumerRepDTO.setStatus(result.getData().getStatus());
        return Result.ok(mergeConsumerRepDTO);
    }

    @Lock4j(keys = "#payTransferDTO.businessOrderNo", expire = 5000)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> transfer(UnionPayTransferRespDTO payTransferDTO) {
        log.info("转账接收参数:{}", JSONObject.toJSONString(payTransferDTO));
        ValidatorUtils.validateEntity(payTransferDTO);
        //1.判断单号是否存在
        if (checkExistBusinessOrderNo(payTransferDTO.getBusinessOrderNo(), payTransferDTO.getAppId())) {
            log.error("业务单号已存在:{},appId:{}", payTransferDTO.getBusinessOrderNo(), payTransferDTO.getAppId());
            return Result.failed(PayExceptionCodeEnum.TREAD_ORDER_NO_REPEAT);
        }
        userService.checkLoanAccount(payTransferDTO.getOutBalanceAcctId(), payTransferDTO.getAmount(), payTransferDTO.getOutBalanceAcctName());
        //2.保存订单信息
        String tradeOrderNo = InstructIdUtil.getInstructId(CommonConstants.TRANSACTION_TYPE_TB, new Date(), UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_60, redisCache);
        LoanTransferRespDTO loanTransferRespDTO = new LoanTransferRespDTO();
        BeanUtil.copyProperties(payTransferDTO, loanTransferRespDTO);
        LoanOrderBizServiceImpl bean = SpringContextUtils.getBean(this.getClass());
        bean.transferSaveOrder(loanTransferRespDTO, tradeOrderNo);
        //3.调用银联信息
        ConsumerPoliciesReqDTO consumerPoliciesReqDTO = buildTransferUnionPayParam(payTransferDTO, tradeOrderNo);
        Result<ConsumerPoliciesRespDTO> result = unionPayService.mergeConsumerPolicies(consumerPoliciesReqDTO);
        if (result.getCode() == ExceptionCodeEnum.FAIL.getCode()) {
            log.error("调用银联接口失败:{}", result.getMsg());
            saveMergeConsumerFailResult(consumerPoliciesReqDTO.getCombinedOutOrderNo(), result.getMsg(), payTransferDTO.getAppId());
            return Result.failed(result.getMsg());
        }
        saveMergeConsumerResult(result, payTransferDTO.getAppId());
        return Result.ok(result.getData().getStatus());
    }

    @Override
    public Result<LoanQueryOrderRespDTO> orderQuery(String businessOrderNo, String appId) {
        log.info("查询交易结果信息:{}", businessOrderNo);
        LoanOrderEntity one = getByBusinessAndAppId(businessOrderNo, appId);
        LoanQueryOrderRespDTO loanQueryOrderRespDTO = new LoanQueryOrderRespDTO();
        if (one == null) {
            loanQueryOrderRespDTO.setResult_code(TradeResultConstant.PAY_FAILED);
            return Result.ok(loanQueryOrderRespDTO);
        }
        loanQueryOrderRespDTO.setBusiness_type(one.getBusinessType());
        loanQueryOrderRespDTO.setOut_trade_no(businessOrderNo);
        loanQueryOrderRespDTO.setTransaction_id(one.getCombinedGuaranteePaymentId());
        loanQueryOrderRespDTO.setPay_balanceAcct_id(one.getPayBalanceAcctId());
        loanQueryOrderRespDTO.setMetadata(one.getMetadata());
        loanQueryOrderRespDTO.setPay_balance_acct_name(one.getPayBalanceAcctName());
        loanQueryOrderRespDTO.setTotal_fee(one.getAmount());
        if (!TradeResultConstant.UNIONPAY_SUCCEEDED.equals(one.getStatus())) {
            Result<ConsumerPoliciesRespDTO> consumerPoliciesRespDTOResult = unionPayService.queryPlatformOrderStatus(one.getTradeOrderNo());
            int code = consumerPoliciesRespDTOResult.getCode();
            if (code == NumberConstant.ZERO){
                ConsumerPoliciesRespDTO data = consumerPoliciesRespDTOResult.getData();
                loanQueryOrderRespDTO.setResult_code(TradeResultConstant.UNIONPAY_SUCCEEDED.equals(data.getStatus()) ? TradeResultConstant.PAY_SUCCESS : TradeResultConstant.PAY_FAILED);

            } else {
                return Result.failed(consumerPoliciesRespDTOResult.getMsg());
            }
        } else {
            loanQueryOrderRespDTO.setResult_code(TradeResultConstant.PAY_SUCCESS);
        }
        List<LoanOrderDetailsRespDTO> details_dto_list = listLoanOrderDetailsRespDTO(one.getId());
        loanQueryOrderRespDTO.setDetails_dto_list(details_dto_list);
        loanQueryOrderRespDTO.setTread_type(PayTypeConstants.PAY_TYPE_LOAN);
        return Result.ok(loanQueryOrderRespDTO);
    }

    @Override
    public Integer unCheckCount(Date date) {
        return orderService.countUnCheckBill(date);
    }

    @Override
    public List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, Integer pageNo, Integer pageSize) {
        return orderService.listUnCheckBill(date,pageNo,pageSize);
    }

    @Override
    public List<LoanUnionpayCheckBillDetailsEntity> listDetailsUnCheckBill(Date date, Integer pageNo, Integer pageSize) {
        return loanOrderDetailsService.listUnCheckBill(date,pageNo,pageSize);
    }

    @Override
    public Integer unDetailsCheckCount(Date date) {
        return loanOrderDetailsService.countUnCheckBill(date);
    }


    /**
     * 构建转账交易参数
     *
     * @param payTransferDTO 转账参数
     */
    private ConsumerPoliciesReqDTO buildTransferUnionPayParam(UnionPayTransferRespDTO payTransferDTO, String tradeOrderNo) {
        ConsumerPoliciesReqDTO consumerPoliciesReqDTO = new ConsumerPoliciesReqDTO();
        consumerPoliciesReqDTO.setCombinedOutOrderNo(tradeOrderNo);
        consumerPoliciesReqDTO.setPayBalanceAcctId(payTransferDTO.getOutBalanceAcctId()); //2008362494748960292
        //担保消费参数
        List<GuaranteePaymentDTO> list = new ArrayList<>();
        GuaranteePaymentDTO guaranteePaymentDTO = new GuaranteePaymentDTO();
        guaranteePaymentDTO.setAmount(payTransferDTO.getAmount());
        guaranteePaymentDTO.setRecvBalanceAcctId(payTransferDTO.getInBalanceAcctId()); //2008349494890702347
        guaranteePaymentDTO.setOutOrderNo(tradeOrderNo);
        //扩展字段集合
        List<ExtraDTO> list2 = new ArrayList<>();
        ExtraDTO extraDTO = new ExtraDTO();
        extraDTO.setOrderNo(tradeOrderNo);
        extraDTO.setOrderAmount(payTransferDTO.getAmount().toString());
        extraDTO.setProductCount(NumberConstant.ONE.toString());
        extraDTO.setProductName("转账");
        Map<String, Object> map = new HashMap<>();
        list2.add(extraDTO);
        map.put("productInfos", list2);
        guaranteePaymentDTO.setExtra(map);
        list.add(guaranteePaymentDTO);
        consumerPoliciesReqDTO.setRemark("转账");
        consumerPoliciesReqDTO.setGuaranteePaymentParams(list);
        Map<String, Object> extra = new HashMap<>();
        extra.put("notifyUrl", notifyUrl);  //回调地址
        consumerPoliciesReqDTO.setExtra(extra);
        return consumerPoliciesReqDTO;
    }

    /**
     * 处理银联调用成功的数据
     *
     * @param result 银联合并下单返回数据
     * @param appId  appId
     */
    public void saveMergeConsumerResult(Result<ConsumerPoliciesRespDTO> result, String appId) {
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
                .eq(LoanOrderEntity::getAppId, appId);
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
                    .eq(LoanOrderDetailsEntity::getOrderId, one.getId());
            if (!this.loanOrderDetailsService.update(orderDetailsEntity, detailsUpdateWrapper)) {
                log.error("更新订单详细信息失败:{},交易订单号:{}", JSONObject.toJSONString(orderDetailsEntity), guaranteePaymentResult.getOutOrderNo());
                throw new TfException(PayExceptionCodeEnum.DATABASE_UPDATE_FAIL);
            }
        }
    }

    /**
     * 处理银联实时返回失败的订单信息
     *
     * @param combinedOutOrderNo 银联交易订单号
     * @param msg                失败信息
     * @param appId              appId
     */
    public void saveMergeConsumerFailResult(String combinedOutOrderNo, String msg, String appId) {
        LambdaQueryWrapper<LoanOrderEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoanOrderEntity::getTradeOrderNo, combinedOutOrderNo)
                .eq(LoanOrderEntity::getAppId, appId);
        LoanOrderEntity one = this.orderService.getOne(wrapper);
        //修改订单状态
        LoanOrderEntity loanOrderEntity = new LoanOrderEntity();
        loanOrderEntity.setStatus(TransactionStatusEnum.FAILED.getCode());
        loanOrderEntity.setId(one.getId());
        if (!this.orderService.updateById(loanOrderEntity)) {
            log.error("更新修改失败订单状态异常,订单id:{}", JSONObject.toJSONString(loanOrderEntity));
            return;
        }
        LambdaUpdateWrapper<LoanOrderDetailsEntity> detailsUpdateWrapper = new LambdaUpdateWrapper<>();
        detailsUpdateWrapper.eq(LoanOrderDetailsEntity::getOrderId, one.getId())
                .set(LoanOrderDetailsEntity::getReason, msg)
                .set(LoanOrderDetailsEntity::getStatus, TransactionStatusEnum.FAILED.getCode());
        if (!this.loanOrderDetailsService.update(detailsUpdateWrapper)) {
            log.error("更新修改失败订单状态异常,主订单id:{}", one.getId());
        }
    }

    /**
     * 检验单号是否使用过
     *
     * @param businessOrderNo 业务单号
     * @param appId           应用id
     * @return true 单号已经使用  false  单号未使用
     */
    @Override
    public boolean checkExistBusinessOrderNo(String businessOrderNo, String appId) {
        Object cacheObject = redisCache.getCacheObject(buildRedisKey(businessOrderNo, appId));
        if (cacheObject != null) {
            return true;
        }
        return orderService.checkExistBusinessOrderNo(businessOrderNo, appId);
    }

    /**
     * 创建保存在redis中的key
     *
     * @param businessOrderNo 交易订单号
     * @param appId           appId
     * @return 缓存在redis中的key
     */
    private String buildRedisKey(String businessOrderNo, String appId) {
        return RedisConstant.PAY_GENERATE_ORDER_NO + ":" + appId + ":" + businessOrderNo;
    }
}
