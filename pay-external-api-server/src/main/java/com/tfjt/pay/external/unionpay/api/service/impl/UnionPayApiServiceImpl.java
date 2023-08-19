package com.tfjt.pay.external.unionpay.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tfjt.pay.external.unionpay.api.dto.req.*;
import com.tfjt.pay.external.unionpay.api.dto.resp.*;
import com.tfjt.pay.external.unionpay.api.service.UnionPayApiService;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;

import com.tfjt.pay.external.unionpay.constants.TradeResultConstant;
import com.tfjt.pay.external.unionpay.constants.TransactionTypeConstants;
import com.tfjt.pay.external.unionpay.dto.ExtraDTO;
import com.tfjt.pay.external.unionpay.dto.GuaranteePaymentDTO;
import com.tfjt.pay.external.unionpay.dto.UnionPayProduct;
import com.tfjt.pay.external.unionpay.dto.req.ConsumerPoliciesReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayDivideReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayDivideSubReq;
import com.tfjt.pay.external.unionpay.dto.req.WithdrawalCreateReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.*;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.service.LoanBalanceDivideDetailsService;
import com.tfjt.pay.external.unionpay.service.LoanBalanceDivideService;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.pay.external.unionpay.enums.UnionPayBusinessTypeEnum;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.pay.external.unionpay.utils.OrderNumberUtil;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.pay.external.unionpay.utils.UnionPaySignUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

/**
 * 银联接口服务实现类
 *
 * @author songx
 * @date 2023-08-10 17:02
 * @email 598482054@qq.com
 */
@Slf4j
@DubboService
public class UnionPayApiServiceImpl implements UnionPayApiService {
    @Autowired
    private TfAccountConfig accountConfig;

    @Autowired
    private UnionPayService unionPayService;

    @Autowired
    private LoanBalanceDivideService payBalanceDivideService;

    @Autowired
    private LoanBalanceDivideDetailsService payBalanceDivideDetailsService;

    @Autowired
    private OrderNumberUtil orderNumberUtil;

    @Resource
    LoanBalanceAcctService loanBalanceAcctService;

    @Resource
    CustBankInfoService custBankInfoService;


    @Autowired
    private LoanOrderService orderService;

    @Autowired
    private LoanOrderGoodsService loanOrderGoodsService;

    @Autowired
    private LoanOrderDetailsService loanOrderDetailsService;

    @Resource
    LoanWithdrawalOrderService withdrawalOrderService;

    @Resource
    private LoanUnionpayCheckBillService loanUnionpayCheckBillService;


    @Value("${unionPayLoans.encodedPub}")
    private String encodedPub;

    //@Value("backcall")
    private String notifyUrl = "http://60.204.170.215:9001/tf-pay-external/unionPay/notice/commonCallback";


    @Lock4j(keys = "#payTransferDTO.businessOrderNo", expire = 5000)
    @Transactional(rollbackFor = {TfException.class, Exception.class})
    @Override
    public Result<String> transfer(UnionPayTransferRespDTO payTransferDTO) {
        log.info("转账接收参数:{}", JSONObject.toJSONString(payTransferDTO));
        ValidatorUtils.validateEntity(payTransferDTO);
        //1.判断单号是否存在
        if (orderService.checkExistBusinessOrderNo(payTransferDTO.getBusinessOrderNo(), payTransferDTO.getAppId())) {
            log.error("业务单号已存在:{},appId:{}", payTransferDTO.getBusinessOrderNo(), payTransferDTO.getAppId());
            throw new TfException(ExceptionCodeEnum.ILLEGAL_ARGUMENT);
        }
        checkLoanAccount(payTransferDTO.getOutBalanceAcctId(), payTransferDTO.getOutBalanceAcctName(), payTransferDTO.getAmount());
        //2.保存订单信息
        String tradeOrderNo = orderNumberUtil.generateOrderNumber(TransactionTypeConstants.TRANSACTION_TYPE_TB);
        transferSaveOrder(payTransferDTO, tradeOrderNo);

        //3.调用银联信息
        ConsumerPoliciesReqDTO consumerPoliciesReqDTO = buildTransferUnionPayParam(payTransferDTO, tradeOrderNo);
        Result<ConsumerPoliciesRespDTO> result = unionPayService.mergeConsumerPolicies(consumerPoliciesReqDTO);
        if (result.getCode() == ExceptionCodeEnum.FAIL.getCode()) {
            log.error("调用银联接口失败");
            return Result.failed(result.getMsg());
        }
        saveMergeConsumerResult(result);
        return Result.ok(result.getData().getStatus());
    }

    /**
     * 修改订单状态
     * @param result
     */
    public void saveMergeConsumerResult(Result<ConsumerPoliciesRespDTO> result) {
        ConsumerPoliciesRespDTO data = result.getData();
        String status = data.getStatus();
        Date finshDate = null;
        if(StringUtil.isNotBlank(data.getFinishedAt())){
            try {
                finshDate = DateUtil.parseDate(data.getFinishedAt(),DateUtil.YYYY_MM_DD_T_HH_MM_SS_SSSXXX);
            } catch (ParseException e) {
                log.error("解析完成时间异常:{}",data.getFinishedAt());
            }
        }
        //修改订单状态
        LoanOrderEntity loanOrderEntity = new LoanOrderEntity();
        loanOrderEntity.setCombinedGuaranteePaymentId(data.getCombinedGuaranteePaymentId());
        loanOrderEntity.setStatus(status);
        loanOrderEntity.setFinishedAt(finshDate);
        LambdaUpdateWrapper<LoanOrderEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LoanOrderEntity::getTradeOrderNo,data.getCombinedOutOrderNo());
        if(this.orderService.update(loanOrderEntity,updateWrapper)){
            log.error("更新订单状态失败:{},交易订单号:{}",JSONObject.toJSONString(loanOrderEntity),data.getCombinedOutOrderNo());
            return;
        }
        List<GuaranteePaymentDTO> guaranteePaymentResults = data.getGuaranteePaymentResults();
        for (GuaranteePaymentDTO guaranteePaymentResult : guaranteePaymentResults) {
            LoanOrderDetailsEntity orderDetailsEntity = new LoanOrderDetailsEntity();
            BeanUtil.copyProperties(guaranteePaymentResult,orderDetailsEntity);
            orderDetailsEntity.setFinishedAt(finshDate);
            LambdaUpdateWrapper<LoanOrderDetailsEntity> detailsUpdateWrapper = new LambdaUpdateWrapper<>();
            detailsUpdateWrapper.eq(LoanOrderDetailsEntity::getSubBusinessOrderNo,guaranteePaymentResult.getOutOrderNo());
            if(this.loanOrderDetailsService.update(orderDetailsEntity,detailsUpdateWrapper)){
                log.error("更新订单详细信息失败:{},交易订单号:{}",JSONObject.toJSONString(orderDetailsEntity),guaranteePaymentResult.getOutOrderNo());
                throw new TfException(ExceptionCodeEnum.FAIL);
            }
        }
    }

    /**
     * 构建转账交易参数
     *
     * @param payTransferDTO
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

    @Override
    public Result<Integer> currentBalance() {
        BalanceAcctRespDTO balanceAcctDTOByAccountId = getBalanceAcctDTOByAccountId(accountConfig.getBalanceAcctId());
        log.debug("查询母账户交易余额返回:{}", balanceAcctDTOByAccountId.getSettledAmount());
        return Result.ok(balanceAcctDTOByAccountId.getSettledAmount());
    }

    @Override
    public Result<BalanceAcctRespDTO> getBalanceByAccountId(String balanceAcctId) {
        log.debug("查询电子账簿id:{}", balanceAcctId);
        if (StringUtil.isBlank(balanceAcctId)) {
            return Result.failed("电子账簿id不能为空");
        }
        BalanceAcctRespDTO balanceAcctDTO = getBalanceAcctDTOByAccountId(balanceAcctId);
        if (Objects.isNull(balanceAcctDTO)) {
            String message = String.format("[%s]电子账簿信息不存在", balanceAcctId);
            log.error(String.format(message));
            return Result.failed(message);
        }
        return Result.ok(balanceAcctDTO);
    }

    @Override
    public Result<Map<String, BalanceAcctRespDTO>> listBalanceByAccountIds(List<String> balanceAcctIds) {
        log.debug("批量查询电子账户参数信息:{}", JSONObject.toJSONString(balanceAcctIds));
        if (CollectionUtil.isEmpty(balanceAcctIds)) {
            return Result.failed("电子账簿id不能为空");
        }
        Map<String, BalanceAcctRespDTO> result = new HashMap<>(balanceAcctIds.size());
        for (String balanceAcctId : balanceAcctIds) {
            BalanceAcctRespDTO balanceAcctDTOByAccountId = getBalanceAcctDTOByAccountId(balanceAcctId);
            if (!Objects.isNull(balanceAcctDTOByAccountId)) {
                result.put(balanceAcctId, balanceAcctDTOByAccountId);
            }
        }
        log.debug("批量查询电子账户返回信息:{}", JSONObject.toJSONString(result));
        return Result.ok(result);
    }

    @Lock4j(keys = {"#balanceDivideReq.businessOrderNo"}, expire = 10000, acquireTimeout = 3000)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<Map<String, SubBalanceDivideRespDTO>> balanceDivide(BalanceDivideReqDTO balanceDivideReq) {
        log.info("请求分账参数<<<<<<<<<<<<<<<<:{}", JSONObject.toJSONString(balanceDivideReq));
        String tradeOrderNo = orderNumberUtil.generateOrderNumber(TransactionTypeConstants.TRANSACTION_TYPE_DB);
        List<LoanBalanceDivideDetailsEntity> saveList = new ArrayList<>();
        try {
            //1.检验订单号是否存在
            String businessOrderNo = balanceDivideReq.getBusinessOrderNo();
            if (this.payBalanceDivideService.checkExistBusinessOrderNo(businessOrderNo)) {
                String message = String.format("业务订单号[%s]已经存在", businessOrderNo);
                log.error(message);
                return Result.failed(message);
            }
            Date date = new Date();
            //2.保存分账主信息信息
            //主交易单号,银联交互使用

            LoadBalanceDivideEntity payBalanceDivideEntity = new LoadBalanceDivideEntity();
            payBalanceDivideEntity.setPayBalanceAcctId(accountConfig.getBalanceAcctId());
            payBalanceDivideEntity.setTradeOrderNo(tradeOrderNo);
            payBalanceDivideEntity.setBusinessOrderNo(balanceDivideReq.getBusinessOrderNo());
            payBalanceDivideEntity.setAppId(balanceDivideReq.getAppId());
            payBalanceDivideEntity.setCreateAt(date);
            if (!payBalanceDivideService.save(payBalanceDivideEntity)) {
                log.error("保存分账主信息失败:{}", JSONObject.toJSONString(payBalanceDivideEntity));
                return Result.failed("保存分账主信息失败");
            }
            //3.保存子分账信息
            List<SubBalanceDivideReqDTO> list = balanceDivideReq.getList();

            for (SubBalanceDivideReqDTO subBalanceDivideReqDTO : list) {
                saveList.add(buildPayBalanceDivideDetailsEntity(subBalanceDivideReqDTO, date, payBalanceDivideEntity.getId()));
            }
            if (!this.payBalanceDivideDetailsService.saveBatch(saveList)) {
                log.error("保存分账信息失败:{}", JSONObject.toJSONString(saveList));
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.failed("分账失败,保存分账信息失败");
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw new TfException("");
        }
        //4.生成银联分账参数
        UnionPayDivideReqDTO unionPayDivideReqDTO = buildBalanceDivideUnionPayParam(saveList, tradeOrderNo);
        try {
            //5.调用银联接口
            log.info("调用银联分账信息发送信息>>>>>>>>>>>>>>>:{}", JSONObject.toJSONString(unionPayDivideReqDTO));
            Result<UnionPayDivideRespDTO> result = unionPayService.balanceDivide(unionPayDivideReqDTO);
            log.info("调用银联分账信息返回信息<<<<<<<<<<<<<<<:{}", JSONObject.toJSONString(result));
            if (result.getCode() != NumberConstant.ZERO) {
                log.error("调用银联分账接口失败");
                return Result.failed(result.getMsg());
            }
            //6.解析返回数据响应给业务系统
            return Result.ok(parseUnionPayDivideReqDTOToMap(result.getData()));
        } catch (Exception e) {
            log.error("调用银联异常分账接口异常:{}", e);
            //7.查询交易结果信息,防止请求发出未收到响应重复支付
            //payResult();
        }
        return Result.ok();
    }

    /**
     * 提现
     *
     * @param withdrawalReqDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<WithdrawalRespDTO> withdrawalCreation(WithdrawalReqDTO withdrawalReqDTO) {
        LoanBalanceAcctEntity accountBook = loanBalanceAcctService.getAccountBookByLoanUserId(withdrawalReqDTO.getLoanUserId());
        CustBankInfoEntity bankInfo = custBankInfoService.getById(withdrawalReqDTO.getBankInfoId());
        WithdrawalCreateReqDTO withdrawalCreateReqDTO = new WithdrawalCreateReqDTO();
        //todo 统一生成NO
        withdrawalCreateReqDTO.setOutOrderNo("2008349494890702348");
        withdrawalCreateReqDTO.setSentAt(DateUtil.getNowByRFC3339());
        withdrawalCreateReqDTO.setAmount(withdrawalReqDTO.getAmount());
        withdrawalCreateReqDTO.setServiceFee(null);
        withdrawalCreateReqDTO.setBalanceAcctId(accountBook.getBalanceAcctId());//电子账簿ID
        withdrawalCreateReqDTO.setBusinessType(UnionPayBusinessTypeEnum.WITHDRAWAL.getCode());
        withdrawalCreateReqDTO.setBankAcctNo(UnionPaySignUtil.SM2(encodedPub, bankInfo.getBankCardNo()));//提现目标银行账号 提现目标银行账号需要加密处理  6228480639353401873
        withdrawalCreateReqDTO.setMobileNumber(UnionPaySignUtil.SM2(encodedPub, bankInfo.getPhone())); //手机号 需要加密处理
        withdrawalCreateReqDTO.setRemark("");
        Map<String, Object> map = new HashMap<>();
        map.put("notifyUrl", notifyUrl);
        withdrawalCreateReqDTO.setExtra(map);
        //插入业务表
        LoanWithdrawalOrderEntity loanWithdrawalOrderEntity = BeanUtil.copyProperties(withdrawalCreateReqDTO, LoanWithdrawalOrderEntity.class);
        loanWithdrawalOrderEntity.setBankAcctNo(bankInfo.getBankCardNo());
        loanWithdrawalOrderEntity.setMobileNumber(bankInfo.getPhone());
        loanWithdrawalOrderEntity.setAppId(withdrawalReqDTO.getAppId());
        log.info("银联提现参数插入业务表:{}", JSON.toJSONString(loanWithdrawalOrderEntity));
        withdrawalOrderService.save(loanWithdrawalOrderEntity);
        log.info("银联提现参数:{}", JSON.toJSONString(withdrawalCreateReqDTO));
        Result<WithdrawalCreateRespDTO> withdrawalCreateResp = unionPayService.withdrawalCreation(withdrawalCreateReqDTO);
        WithdrawalRespDTO withdrawalRespDTO = BeanUtil.copyProperties(withdrawalCreateResp, WithdrawalRespDTO.class);
        if (withdrawalCreateResp.getCode() != NumberConstant.ZERO) {
            return Result.failed(withdrawalCreateResp.getMsg());
        } else {
            //更新状态
            loanWithdrawalOrderEntity.setStatus(withdrawalRespDTO.getStatus());
            withdrawalOrderService.updateById(loanWithdrawalOrderEntity);
            return Result.ok(withdrawalRespDTO);
        }

    }

    @Override
    public Result<LoanQueryOrderRespDTO> orderQuery(String businessOrderNo,String appId) {
        log.info("查询交易结果信息:{}",businessOrderNo);
        LambdaQueryWrapper<LoanOrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoanOrderEntity::getBusinessOrderNo,businessOrderNo)
                .eq(LoanOrderEntity::getAppId,appId);
        LoanOrderEntity one = this.orderService.getOne(queryWrapper);
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
        if(TradeResultConstant.UNIONPAY_UNKNOWN.equals(one.getStatus())){
            Result<ConsumerPoliciesRespDTO> consumerPoliciesRespDTOResult = unionPayService.queryPlatformOrderStatus(one.getTradeOrderNo());
            int code = consumerPoliciesRespDTOResult.getCode();
            if(code==NumberConstant.ONE){
                ConsumerPoliciesRespDTO data = consumerPoliciesRespDTOResult.getData();
                loanQueryOrderRespDTO.setResult_code(TradeResultConstant.UNIONPAY_SUCCEEDED.equals(data.getStatus())?TradeResultConstant.PAY_SUCCESS:TradeResultConstant.PAY_FAILED);
            }else{
                return Result.failed();
            }
        }else{
            loanQueryOrderRespDTO.setResult_code(TradeResultConstant.UNIONPAY_SUCCEEDED.equals(one.getStatus())?TradeResultConstant.PAY_SUCCESS:TradeResultConstant.PAY_FAILED);
        }
        return Result.ok(loanQueryOrderRespDTO);
    }

    @Lock4j(keys = "#loanOrderUnifiedorderDTO.businessOrderNo",expire = 10000)
    @Override
    public Result<String> unifiedorder(LoanOrderUnifiedorderReqDTO loanOrderUnifiedorderDTO) {
        //1.判断单号是否存在
        if (orderService.checkExistBusinessOrderNo(loanOrderUnifiedorderDTO.getBusinessOrderNo(), loanOrderUnifiedorderDTO.getAppId())) {
            log.error("业务单号已存在:{},appId:{}", loanOrderUnifiedorderDTO.getBusinessOrderNo(), loanOrderUnifiedorderDTO.getAppId());
            throw new TfException(ExceptionCodeEnum.ILLEGAL_ARGUMENT);
        }
        List<LoanOrderDetailsReqDTO> detailsDTOList = loanOrderUnifiedorderDTO.getDetailsDTOList();
        int totalAmount = detailsDTOList.stream().mapToInt(LoanOrderDetailsReqDTO::getAmount).sum();
        checkLoanAccount(loanOrderUnifiedorderDTO.getPayBalanceAcctId(), loanOrderUnifiedorderDTO.getPayBalanceAcctName(), totalAmount);
        //2.保存订单信息
        ConsumerPoliciesReqDTO consumerPoliciesReqDTO = unifiedorderSaveOrderAndBuildUionpayParam(loanOrderUnifiedorderDTO);
        Result<ConsumerPoliciesRespDTO> result = unionPayService.mergeConsumerPolicies(consumerPoliciesReqDTO);
        if (result.getCode() == ExceptionCodeEnum.FAIL.getCode()) {
            log.error("调用银联接口失败");
            return Result.failed(result.getMsg());
        }
        saveMergeConsumerResult(result);
        return Result.ok(result.getData().getStatus());
    }
    @Override
    public Result<String> downloadCheckBill(String date) {
        LoanUnionpayCheckBillEntity byDateAndAccountId = loanUnionpayCheckBillService.getByDateAndAccountId(date, accountConfig.getBalanceAcctId());
        if(byDateAndAccountId.getBalanceAcctId()!=null){
            return Result.ok(byDateAndAccountId.getUrl());
        }
        return Result.failed("下载对账单失败");
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

    /**
     * 修改银联信息
     *
     * @param unionPayDivideRespDTO
     */
    private void updateByUnionPayDivideReqDTO(UnionPayDivideRespDTO unionPayDivideRespDTO) {
        LoadBalanceDivideEntity update = new LoadBalanceDivideEntity();
        BeanUtil.copyProperties(unionPayDivideRespDTO, update);
        LambdaUpdateWrapper<LoadBalanceDivideEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LoadBalanceDivideEntity::getTradeOrderNo, unionPayDivideRespDTO.getOutOrderNo());
        if (!this.payBalanceDivideService.updateById(update)) {
            log.error("更新分账主单据信息失败:{}", JSONObject.toJSONString(update));
        }
        List<UnionPayDivideRespDetailDTO> transferResults = unionPayDivideRespDTO.getTransferResults();
        for (UnionPayDivideRespDetailDTO transferResult : transferResults) {
            LoanBalanceDivideDetailsEntity payBalanceDivideDetailsEntity = new LoanBalanceDivideDetailsEntity();
            BeanUtil.copyProperties(transferResult, payBalanceDivideDetailsEntity);
            LambdaUpdateWrapper<LoanBalanceDivideDetailsEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(LoanBalanceDivideDetailsEntity::getRecvBalanceAcctId, transferResult.getRecvBalanceAcctId())
                    .eq(LoanBalanceDivideDetailsEntity::getAmount, transferResult.getAmount());
            if (this.payBalanceDivideDetailsService.update(payBalanceDivideDetailsEntity, lambdaUpdateWrapper)) {
                log.error("更新子交易记录失败:{},该记录银联返回信息",
                        JSONObject.toJSONString(payBalanceDivideDetailsEntity), JSONObject.toJSONString(transferResult));
            }
        }

    }

    /**
     * 生成银联分账参数
     *
     * @param saveList
     * @param tradeOrderNo
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

    /**
     * 获取指定电子账簿的账户信息
     *
     * @param balanceAcctId 账户账户id
     * @return 电子账户信息
     */
    private BalanceAcctRespDTO getBalanceAcctDTOByAccountId(String balanceAcctId) {
        LoanAccountDTO loanAccountDTO = unionPayService.getLoanAccount(balanceAcctId);
        if (Objects.isNull(loanAccountDTO)) {
            return null;
        }
        BalanceAcctRespDTO balanceAcctDTO = new BalanceAcctRespDTO();
        BeanUtil.copyProperties(loanAccountDTO, balanceAcctDTO);
        return balanceAcctDTO;
    }

    /**
     * 检查用户状态是否正常
     *
     * @param balanceAcctId
     * @param balanceAcctName
     * @param amount
     * @return
     */
    private void checkLoanAccount(String balanceAcctId, String balanceAcctName, Integer amount) {
        LoanAccountDTO loanAccountDTO = unionPayService.getLoanAccount(balanceAcctId);
        if (Objects.isNull(loanAccountDTO)) {
            log.error("电子账簿信息不存在:{}", balanceAcctId);
            throw new TfException(ExceptionCodeEnum.ILLEGAL_ARGUMENT);
        }
        if (loanAccountDTO.isFrozen()) {
            throw new TfException(String.format("电子账簿[%s]已冻结", balanceAcctId));
        }
        if (amount != null && loanAccountDTO.getSettledAmount() < amount) {
            log.error("账户余额不足");
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }

    /**
     * 保存转账订单信息
     *
     * @param payTransferDTO 转账参数
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transferSaveOrder(UnionPayTransferRespDTO payTransferDTO, String tradeOrderNo) {
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
        if (!this.loanOrderGoodsService.save(orderGoodsEntity)) {
            log.error("保存转账商品详情失败:{}", JSONObject.toJSONString(orderEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }

    /**
     * 保存下单商品信息
     * 并生成调用银联下单接口参数
     * @param loanOrderUnifiedorderDTO 商品订单信息
     * @return  调用银联参数
     */
    public ConsumerPoliciesReqDTO unifiedorderSaveOrderAndBuildUionpayParam(LoanOrderUnifiedorderReqDTO loanOrderUnifiedorderDTO) {
        String generatedOrderNumber = orderNumberUtil.generateOrderNumber("FK");
        ConsumerPoliciesReqDTO consumerPoliciesReqDTO = new ConsumerPoliciesReqDTO();
        consumerPoliciesReqDTO.setPayBalanceAcctId(loanOrderUnifiedorderDTO.getPayBalanceAcctId());
        consumerPoliciesReqDTO.setCombinedOutOrderNo(generatedOrderNumber);
        Date date = new Date();
        LoanOrderEntity orderEntity = new LoanOrderEntity();
        BeanUtil.copyProperties(loanOrderUnifiedorderDTO,orderEntity);
        orderEntity.setBusinessType(Integer.valueOf(UnionPayBusinessTypeEnum.UNIFIEDORDER.getCode()));
        orderEntity.setCreateAt(date);
        orderEntity.setTradeOrderNo(generatedOrderNumber);
        if(!this.orderService.save(orderEntity)){
            log.error("保存贷款订单信息失败:{}",JSONObject.toJSONString(orderEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        List<GuaranteePaymentDTO> list = new ArrayList<>();
        List<LoanOrderDetailsReqDTO> detailsDTOList = loanOrderUnifiedorderDTO.getDetailsDTOList();
        for (LoanOrderDetailsReqDTO loanOrderDetailsReqDTO : detailsDTOList) {
            LoanOrderDetailsEntity orderDetailsEntity = new LoanOrderDetailsEntity();
            BeanUtil.copyProperties(loanOrderDetailsReqDTO,orderDetailsEntity);
            orderDetailsEntity.setOrderId(orderEntity.getId());
            orderDetailsEntity.setTradeOrderNo(orderNumberUtil.generateOrderNumber("SFK"));
            orderDetailsEntity.setPayBalanceAcctId(orderDetailsEntity.getPayBalanceAcctId());
            orderDetailsEntity.setAppId(orderDetailsEntity.getAppId());
            orderDetailsEntity.setCreatedAt(date);
            if(this.loanOrderDetailsService.save(orderDetailsEntity)){
                log.error("保存贷款订单详情信息失败:{}",JSONObject.toJSONString(orderDetailsEntity));
                throw new TfException(ExceptionCodeEnum.FAIL);
            }
            GuaranteePaymentDTO guaranteePaymentDTO = new GuaranteePaymentDTO();
            guaranteePaymentDTO.setAmount(loanOrderDetailsReqDTO.getAmount());
            guaranteePaymentDTO.setPayBalanceAcctId(orderDetailsEntity.getPayBalanceAcctId());
            guaranteePaymentDTO.setOutOrderNo(orderDetailsEntity.getTradeOrderNo());
            guaranteePaymentDTO.setRecvBalanceAcctId(guaranteePaymentDTO.getRecvBalanceAcctId());
            List<ExtraDTO> listGoods = new ArrayList<>();

            List<LoanOrderGoodsReqDTO> goodsDTOList = loanOrderDetailsReqDTO.getGoodsDTOList();
            for (LoanOrderGoodsReqDTO loanOrderGoodsReqDTO : goodsDTOList) {
                LoanOrderGoodsEntity orderGoodsEntity = new LoanOrderGoodsEntity();
                BeanUtil.copyProperties(loanOrderGoodsReqDTO,orderGoodsEntity);
                orderGoodsEntity.setDetailsId(orderDetailsEntity.getId());
                orderGoodsEntity.setOrderBusinessOrderNo(loanOrderGoodsReqDTO.getOrderNo());
                orderGoodsEntity.setAppId(loanOrderUnifiedorderDTO.getAppId());
                orderGoodsEntity.setPayBalanceAcctId(loanOrderUnifiedorderDTO.getPayBalanceAcctId());
                orderGoodsEntity.setRecvBalanceAcctId(loanOrderDetailsReqDTO.getRecvBalanceAcctId());
                orderGoodsEntity.setCreateAt(date);

                ExtraDTO extraDTO = new ExtraDTO();
                extraDTO.setOrderNo(orderGoodsEntity.getOrderBusinessOrderNo());
                extraDTO.setOrderAmount(String.valueOf(orderGoodsEntity.getProductAmount()));
                extraDTO.setProductName(orderGoodsEntity.getProductName());
                extraDTO.setProductCount(String.valueOf(orderGoodsEntity.getProductCount()));
                listGoods.add(extraDTO);
                if(!this.loanOrderGoodsService.save(orderGoodsEntity)){
                    log.error("保存贷款订单商品信息失败:{}",JSONObject.toJSONString(orderGoodsEntity));
                    throw new TfException(ExceptionCodeEnum.FAIL);
                }
            }
            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            stringObjectHashMap.put("productInfos",listGoods);
            guaranteePaymentDTO.setExtra(stringObjectHashMap);
            list.add(guaranteePaymentDTO);
        }
        consumerPoliciesReqDTO.setGuaranteePaymentParams(list);
        return consumerPoliciesReqDTO;
    }


}
