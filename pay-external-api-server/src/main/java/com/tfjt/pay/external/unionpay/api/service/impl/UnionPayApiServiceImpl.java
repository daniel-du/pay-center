package com.tfjt.pay.external.unionpay.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.tfjt.pay.external.unionpay.api.dto.req.*;
import com.tfjt.pay.external.unionpay.api.dto.resp.*;
import com.tfjt.pay.external.unionpay.api.service.UnionPayApiService;
import com.tfjt.pay.external.unionpay.biz.LoanOrderBiz;
import com.tfjt.pay.external.unionpay.biz.PayBalanceDivideBiz;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.*;
import com.tfjt.pay.external.unionpay.dto.req.BalanceDivideReqDTO;
import com.tfjt.pay.external.unionpay.dto.ExtraDTO;
import com.tfjt.pay.external.unionpay.dto.GuaranteePaymentDTO;
import com.tfjt.pay.external.unionpay.dto.UnionPayProduct;
import com.tfjt.pay.external.unionpay.dto.req.*;
import com.tfjt.pay.external.unionpay.dto.resp.*;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.pay.external.unionpay.enums.UnionPayBusinessTypeEnum;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.utils.*;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import com.tfjt.tfcommon.dto.response.Result;
import com.tfjt.tfcommon.core.util.InstructIdUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    @Resource
    private TfAccountConfig accountConfig;
    @Resource
    private UnionPayService unionPayService;
    @Resource
    LoanBalanceAcctService loanBalanceAcctService;
    @Resource
    CustBankInfoService custBankInfoService;

    @Resource
    LoanWithdrawalOrderService withdrawalOrderService;
    @Resource
    private LoanUnionpayCheckBillService loanUnionpayCheckBillService;

    @Value("${unionPayLoans.encodedPub}")
    private String encodedPub;

    @Resource
    private LoanOrderBiz loanOrderBiz;

    @Resource
    private PayBalanceDivideBiz payBalanceDivideBiz;

    @Autowired
    private LoanUserService loanUserService;
    @Resource
    RedisCache redisCache;

    @Value("${unionPay.loan.notifyUrl}")
    private String notifyUrl;
    private final static String WITHDRAWAL_IDEMPOTENT_KEY = "idempotent:withdrawal";

    @Lock4j(keys = "#payTransferDTO.businessOrderNo", expire = 5000)
    @Override
    public Result<String> transfer(UnionPayTransferRespDTO payTransferDTO) {
        log.info("转账接收参数:{}", JSONObject.toJSONString(payTransferDTO));
        try{
            ValidatorUtils.validateEntity(payTransferDTO);
            //1.判断单号是否存在
            if (loanOrderBiz.checkExistBusinessOrderNo(payTransferDTO.getBusinessOrderNo(), payTransferDTO.getAppId())) {
                log.error("业务单号已存在:{},appId:{}", payTransferDTO.getBusinessOrderNo(), payTransferDTO.getAppId());
                throw new TfException(PayExceptionCodeEnum.TREAD_ORDER_NO_REPEAT);
            }
            checkLoanAccount(payTransferDTO.getOutBalanceAcctId(), payTransferDTO.getAmount(),payTransferDTO.getOutBalanceAcctName());
            //2.保存订单信息
            String tradeOrderNo = InstructIdUtil.getInstructId(CommonConstants.TRANSACTION_TYPE_TB, new Date(), UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_60, redisCache);
            LoanTransferRespDTO loanTransferRespDTO = new LoanTransferRespDTO();
            BeanUtil.copyProperties(payTransferDTO, loanTransferRespDTO);
            loanOrderBiz.transferSaveOrder(loanTransferRespDTO, tradeOrderNo);
            //3.调用银联信息
            ConsumerPoliciesReqDTO consumerPoliciesReqDTO = buildTransferUnionPayParam(payTransferDTO, tradeOrderNo);
            Result<ConsumerPoliciesRespDTO> result = unionPayService.mergeConsumerPolicies(consumerPoliciesReqDTO);
            if (result.getCode() == ExceptionCodeEnum.FAIL.getCode()) {
                log.error("调用银联接口失败");
                throw new TfException(result.getMsg());
            }
            this.loanOrderBiz.saveMergeConsumerResult(result, payTransferDTO.getAppId());
            return Result.ok(result.getData().getStatus());
        }catch (TfException e){
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }
    }

    @Override
    public Result<Integer> currentBalance() {
        BalanceAcctRespDTO balanceAcctDTOByAccountId = getBalanceAcctDTOByAccountId(accountConfig.getBalanceAcctId());
        if (Objects.isNull(balanceAcctDTOByAccountId)) {
            throw new TfException(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
        }
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
        log.info("批量查询电子账户参数信息:{}", JSONObject.toJSONString(balanceAcctIds));
        if (CollectionUtil.isEmpty(balanceAcctIds)) {
            return Result.failed("电子账簿id不能为空");
        }
        Map<String, BalanceAcctRespDTO> result = new HashMap<>(balanceAcctIds.size());
        for (String balanceAcctId : balanceAcctIds) {
            BalanceAcctRespDTO balanceAcctDTOByAccountId = getBalanceAcctDTOByAccountId(balanceAcctId);
            result.put(balanceAcctId, balanceAcctDTOByAccountId);
        }
        log.info("批量查询电子账户返回信息:{}", JSONObject.toJSONString(result));
        return Result.ok(result);
    }

    @Lock4j(keys = {"#balanceDivideReq.businessOrderNo"}, expire = 10000, acquireTimeout = 3000)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<Map<String, SubBalanceDivideRespDTO>> balanceDivide(UnionPayBalanceDivideReqDTO balanceDivideReq) {
        log.info("请求分账参数<<<<<<<<<<<<<<<<:{}", JSONObject.toJSONString(balanceDivideReq));
        String tradeOrderNo = InstructIdUtil.getInstructId(CommonConstants.TRANSACTION_TYPE_DB,new Date(),UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_51,redisCache);

        String businessOrderNo = balanceDivideReq.getBusinessOrderNo();
        //1.检验单号是否存在
        this.payBalanceDivideBiz.checkExistBusinessOrderNo(businessOrderNo);


        List<LoanBalanceDivideDetailsEntity> saveList = new ArrayList<>();
        BalanceDivideReqDTO balanceDivideReqDTO = new BalanceDivideReqDTO();
        BeanUtil.copyProperties(balanceDivideReq, balanceDivideReqDTO);
        payBalanceDivideBiz.saveDivide(tradeOrderNo, saveList, balanceDivideReqDTO);
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
            this.payBalanceDivideBiz.updateByUnionPayDivideReqDTO(result.getData(), balanceDivideReq.getAppId());
            //6.解析返回数据响应给业务系统
            return Result.ok(parseUnionPayDivideReqDTOToMap(result.getData()));
        } catch (TfException e) {
            //交易失败的状态
            return Result.failed(e.getMessage());
        } catch (Exception e) {
            log.error("调用银联异常分账接口异常:{}", e.getMessage());
            //7.查询交易结果信息,防止请求发出未收到响应重复支付
            //Result<LoanQueryOrderRespDTO> loanQueryOrderRespDTOResult = orderQuery(unionPayDivideReqDTO.getOutOrderNo(), balanceDivideReq.getAppId());

        }
        return Result.ok();
    }


    /**
     * 提现
     *
     * @param withdrawalReqDTO 提现参数
     * @return 提现结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<WithdrawalRespDTO> withdrawalCreation(WithdrawalReqDTO withdrawalReqDTO) {
        LoanUserEntity loanUser = loanUserService.getLoanUserByBusIdAndType(withdrawalReqDTO.getBusId(), withdrawalReqDTO.getType());
        if (loanUser == null) {
            return Result.failed("未找到贷款用户");
        }
        String outOrderNo = InstructIdUtil.getInstructId(CommonConstants.LOAN_REQ_NO_PREFIX, new Date(), UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_74, redisCache);
        String md5Str =withdrawalReqDTO.getBusId()+":"+ withdrawalReqDTO.getType() + ":" + withdrawalReqDTO.getAmount();
        log.info("放重复提交加密后的M5d值为：{}", md5Str);
        String idempotentMd5 = MD5Util.getMD5String(md5Str);
        String isIdempotent = redisCache.getCacheString(WITHDRAWAL_IDEMPOTENT_KEY);
        log.info("放重复提交加密后的M5d值为：{}", idempotentMd5);
        if (StringUtils.isEmpty(isIdempotent)) {
            redisCache.setCacheString(WITHDRAWAL_IDEMPOTENT_KEY, idempotentMd5, 60, TimeUnit.MINUTES);
        } else {
            return Result.failed("请勿重复提现");
        }
        LoanBalanceAcctEntity accountBook = loanBalanceAcctService.getAccountBookByLoanUserId(loanUser.getId());
        CustBankInfoEntity bankInfo = custBankInfoService.getById(withdrawalReqDTO.getBankInfoId());
        WithdrawalCreateReqDTO withdrawalCreateReqDTO = new WithdrawalCreateReqDTO();
        withdrawalCreateReqDTO.setOutOrderNo(outOrderNo);
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
    public Result<LoanQueryOrderRespDTO> orderQuery(String businessOrderNo, String appId) {
        log.info("查询交易结果信息:{}", businessOrderNo);

        LoanOrderEntity one = this.loanOrderBiz.getByBusinessAndAppId(businessOrderNo, appId);
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
        if (TradeResultConstant.UNIONPAY_UNKNOWN.equals(one.getStatus())) {
            Result<ConsumerPoliciesRespDTO> consumerPoliciesRespDTOResult = unionPayService.queryPlatformOrderStatus(one.getTradeOrderNo());
            int code = consumerPoliciesRespDTOResult.getCode();
            if (code == NumberConstant.ONE) {
                ConsumerPoliciesRespDTO data = consumerPoliciesRespDTOResult.getData();
                loanQueryOrderRespDTO.setResult_code(TradeResultConstant.UNIONPAY_SUCCEEDED.equals(data.getStatus()) ? TradeResultConstant.PAY_SUCCESS : TradeResultConstant.PAY_FAILED);
            } else {
                return Result.failed();
            }
        } else {
            loanQueryOrderRespDTO.setResult_code(TradeResultConstant.UNIONPAY_SUCCEEDED.equals(one.getStatus()) ? TradeResultConstant.PAY_SUCCESS : TradeResultConstant.PAY_FAILED);
        }
        return Result.ok(loanQueryOrderRespDTO);
    }

    @Lock4j(keys = "#loanOrderUnifiedorderDTO.businessOrderNo", expire = 10000)
    @Override
    public Result<String> unifiedorder(UnionPayLoanOrderUnifiedorderReqDTO loanOrderUnifiedorderDTO) {
        log.info("下单参数:{}",JSONObject.toJSONString(loanOrderUnifiedorderDTO));
        try{
            //1.判断单号是否存在
            if (this.loanOrderBiz.checkExistBusinessOrderNo(loanOrderUnifiedorderDTO.getBusinessOrderNo(), loanOrderUnifiedorderDTO.getAppId())) {
                log.error("业务单号已存在:{},appId:{}", loanOrderUnifiedorderDTO.getBusinessOrderNo(), loanOrderUnifiedorderDTO.getAppId());
                throw new TfException(ExceptionCodeEnum.ILLEGAL_ARGUMENT);
            }
            List<UnionPayLoanOrderDetailsReqDTO> detailsDTOList = loanOrderUnifiedorderDTO.getDetailsDTOList();
            int totalAmount = detailsDTOList.stream().mapToInt(UnionPayLoanOrderDetailsReqDTO::getAmount).sum();
            checkLoanAccount(loanOrderUnifiedorderDTO.getPayBalanceAcctId(), totalAmount,loanOrderUnifiedorderDTO.getPayBalanceAcctName());
            //2.保存订单信息
            LoanOrderUnifiedorderReqDTO loanOrderUnifiedorderReqDTO = new LoanOrderUnifiedorderReqDTO();
            BeanUtil.copyProperties(loanOrderUnifiedorderDTO, loanOrderUnifiedorderReqDTO);
            ConsumerPoliciesReqDTO consumerPoliciesReqDTO = this.loanOrderBiz.unifiedorderSaveOrderAndBuildUnionPayParam(loanOrderUnifiedorderReqDTO,notifyUrl);
            Result<ConsumerPoliciesRespDTO> result = unionPayService.mergeConsumerPolicies(consumerPoliciesReqDTO);
            if (result.getCode() == ExceptionCodeEnum.FAIL.getCode()) {
                log.error("调用银联接口失败");
                return Result.failed(result.getMsg());
            }
            this.loanOrderBiz.saveMergeConsumerResult(result, loanOrderUnifiedorderDTO.getAppId());
            return Result.ok(result.getData().getStatus());
        }catch (TfException e){
            e.printStackTrace();
            log.error("转账异常");
            return Result.failed(e.getMessage());
        }
    }

    @Override
    public Result<String> downloadCheckBill(UnionPayCheckBillReqDTO date) {
        LoanUnionpayCheckBillEntity byDateAndAccountId = loanUnionpayCheckBillService.getByDateAndAccountId(date.getDate(), accountConfig.getBalanceAcctId());
        if (byDateAndAccountId.getBalanceAcctId() != null) {
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
        if (!balanceAcctId.equals(accountConfig.getBalanceAcctId())) {
            LoanUserEntity user = loanUserService.getByBalanceAcctId(balanceAcctId);
            if (!Objects.isNull(user)) {
                balanceAcctDTO.setBalanceAcctName(user.getName());
                balanceAcctDTO.setType(user.getType());
            }
        }
        return balanceAcctDTO;
    }

    /**
     * 检查用户状态是否正常
     *
     * @param balanceAcctId  电子账簿信息
     * @param amount         转账金额
     * @param balanceAccName 电子账簿名称
     */
    private void checkLoanAccount(String balanceAcctId, Integer amount,String balanceAccName) {
        LoanAccountDTO loanAccountDTO = unionPayService.getLoanAccount(balanceAcctId);
        if (Objects.isNull(loanAccountDTO)) {
            log.error("电子账簿信息不存在:{}", balanceAcctId);
            throw new TfException(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
        }

        if (loanAccountDTO.isFrozen()) {
            throw new TfException(PayExceptionCodeEnum.BALANCE_ACCOUNT_FREEZE);
        }
        if (amount != null && loanAccountDTO.getSettledAmount() < amount) {
            log.error("账户余额不足");
            throw new TfException(PayExceptionCodeEnum.BALANCE_NOT_ENOUTH);
        }
        LoanUserEntity user = loanUserService.getByBalanceAcctId(balanceAcctId);
        if(Objects.isNull(user)){
            log.error("用户不存在");
            throw new TfException(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
        }
        if(!balanceAccName.equals(user.getName())){
            throw new TfException(PayExceptionCodeEnum.BALANCE_ACCOUNT_NAME_ERROR);
        }
    }


    /**
     * x
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
}
