package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.api.dto.UserTypeDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.PaymentPasswordReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayIncomingDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.*;
import com.tfjt.pay.external.unionpay.biz.LoanUserBizService;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.CommonConstants;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.UnionPayTradeResultCodeConstant;
import com.tfjt.pay.external.unionpay.dto.BankInfoDTO;
import com.tfjt.pay.external.unionpay.dto.SettleAcctsMxDTO;
import com.tfjt.pay.external.unionpay.dto.req.DepositExtraReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.DepositReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.ProductInfoReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.LoanAccountDTO;
import com.tfjt.pay.external.unionpay.dto.resp.LoanBalanceAcctRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayLoanUserRespDTO;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.entity.PaymentPasswordEntity;
import com.tfjt.pay.external.unionpay.enums.DepositTypeEnum;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.util.InstructIdUtil;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoanUserBizServiceImpl implements LoanUserBizService {

    @Autowired
    private LoanUserService loanUserService;

    @Resource
    private PaymentPasswordService paymentPasswordService;

    @Resource
    private TfAccountConfig accountConfig;

    @Resource
    private LoanBalanceAcctService loanBalanceAcctService;

    @Resource
    private UnionPayService unionPayService;

    @Resource
    private CustBankInfoService custBankInfoService;

    @Resource
    private UnionPayLoansApiService unionPayLoansApiService;

    @Resource
    RedisCache redisCache;

    private final static String DEPOSIT_IDEMPOTENT_KEY = "idempotent:deposit";
    private final static String TF_PAY = "TF_PAY";

    @Override
    public void applicationStatusUpdateJob(String jobParam) {
        loanUserService.applicationStatusUpdateJob(jobParam);
    }

    @Override
    @Lock4j(keys = {"#paymentPasswordDTO.busId", "#paymentPasswordDTO.type", "#paymentPasswordDTO.password"}, expire = 3000, acquireTimeout = 4000)
    public Result<String> savePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        checkLoanUser(paymentPasswordDTO.getBusId(), paymentPasswordDTO.getType());
        try {
            ValidatorUtils.validateEntity(paymentPasswordDTO);
            log.info("电子账簿-设置密码参数paymentPasswordDTO：{}", paymentPasswordDTO);
            PaymentPasswordEntity paymentPassword = new PaymentPasswordEntity();
            BeanUtils.copyProperties(paymentPasswordDTO, paymentPassword);
            //验证此用户是否有支付密码
            PaymentPasswordEntity ppeEntity = paymentPasswordService.getOne(new LambdaQueryWrapper<PaymentPasswordEntity>()
                    .eq(PaymentPasswordEntity::getType, paymentPasswordDTO.getType())
                    .eq(PaymentPasswordEntity::getBusId, paymentPasswordDTO.getBusId()));
            if (ppeEntity == null) {
                paymentPasswordService.save(paymentPassword);
            } else {
                this.updatePaymentPassword(paymentPasswordDTO);
            }
        } catch (Exception ex) {
            if (ex instanceof DuplicateKeyException) {
                return Result.failed(PayExceptionCodeEnum.REPEAT_OPERATION.getMsg());
            }
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok();
    }

    @Override
    @Lock4j(keys = {"#paymentPasswordDTO.busId", "#paymentPasswordDTO.type", "#paymentPasswordDTO.password"}, expire = 3000, acquireTimeout = 4000)
    public Result<String> updatePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        checkLoanUser(paymentPasswordDTO.getBusId(), paymentPasswordDTO.getType());
        try {

            UpdateWrapper<PaymentPasswordEntity> updateWrapper = Wrappers.update();
            updateWrapper.lambda().eq(PaymentPasswordEntity::getType, paymentPasswordDTO.getType()).eq(PaymentPasswordEntity::getBusId, paymentPasswordDTO.getBusId());
            PaymentPasswordEntity paymentPasswordEntity = new PaymentPasswordEntity();
            paymentPasswordEntity.setPassword(paymentPasswordDTO.getPassword());
            paymentPasswordService.update(paymentPasswordEntity, updateWrapper);
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok();
    }

    @Override
    public Result<String> getSalt(UserTypeDTO userType) {
        String salt = null;
        checkLoanUser(userType.getBusId(), userType.getType());
        try {
            PaymentPasswordEntity paymentPassword = paymentPasswordService.getOne(Wrappers.lambdaQuery(PaymentPasswordEntity.class).eq(PaymentPasswordEntity::getType, userType.getType()).eq(PaymentPasswordEntity::getBusId, userType.getBusId()));
            if (ObjectUtils.isNotEmpty(paymentPassword)) {
                salt = paymentPassword.getSalt();
            }
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok(salt);
    }

    @Override
    public Result<Boolean> isExist(UserTypeDTO userType) {
        checkLoanUser(userType.getBusId(), userType.getType());
        boolean result;
        try {
            result = paymentPasswordService.count(Wrappers.lambdaQuery(PaymentPasswordEntity.class).eq(PaymentPasswordEntity::getType, userType.getType()).eq(PaymentPasswordEntity::getBusId, userType.getBusId())) > 0;
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok(result);
    }

    @Override
    public Result<Boolean> verifyPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        checkLoanUser(paymentPasswordDTO.getBusId(), paymentPasswordDTO.getType());
        boolean result = false;
        try {
            PaymentPasswordEntity paymentPassword = paymentPasswordService.getOne(Wrappers.lambdaQuery(PaymentPasswordEntity.class).eq(PaymentPasswordEntity::getType, paymentPasswordDTO.getType()).eq(PaymentPasswordEntity::getBusId, paymentPasswordDTO.getBusId()));
            if (ObjectUtils.isNotEmpty(paymentPassword)) {
                result = Objects.equals(paymentPasswordDTO.getPassword(), paymentPassword.getPassword());
                return Result.ok(result);
            }
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok(result);
    }

    @Override
    public Result<Integer> currentBalance() {
        BalanceAcctRespDTO balanceAcctDTOByAccountId = loanUserService.getBalanceAcctDTOByAccountId(accountConfig.getBalanceAcctId());
        if (Objects.isNull(balanceAcctDTOByAccountId)) {
            throw new TfException(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
        }
        log.debug("查询母账户交易余额返回:{}", balanceAcctDTOByAccountId.getSettledAmount());
        return Result.ok(balanceAcctDTOByAccountId.getSettledAmount());
    }

    @Override
    public Result<ParentBalanceRespDTO> currentBalanceInfo() {

        BalanceAcctRespDTO balanceAcctDTOByAccountId = loanUserService.getBalanceAcctDTOByAccountId(accountConfig.getBalanceAcctId());
        if (Objects.isNull(balanceAcctDTOByAccountId)) {
            throw new TfException(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
        }
        log.debug("查询母账户交易余额返回:{}", balanceAcctDTOByAccountId.getSettledAmount());
        ParentBalanceRespDTO dto = new ParentBalanceRespDTO();
        dto.setAmount(new BigDecimal(balanceAcctDTOByAccountId.getSettledAmount()).divide(new BigDecimal("100"), NumberConstant.TWO, RoundingMode.HALF_UP));
        dto.setAccountNo(accountConfig.getBalanceAcctNo());
        dto.setAccountId(accountConfig.getBalanceAcctId());
        SettleAcctsMxDTO settleAcctsMxDTO = unionPayLoansApiService.querySettleAcct(-1);
        if (Objects.isNull(settleAcctsMxDTO)) {
            throw new TfException(PayExceptionCodeEnum.SETTLE_ACCOUNT_NULL);
        }
        dto.setBankCardNo(settleAcctsMxDTO.getBankAcctNo());
        return Result.ok(dto);
    }

    @Override
    public Result<BalanceAcctRespDTO> getBalanceByAccountId(String balanceAcctId) {
        log.debug("查询电子账簿id:{}", balanceAcctId);
        if (StringUtil.isBlank(balanceAcctId)) {
            return Result.failed(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
        }
        BalanceAcctRespDTO balanceAcctDTO = loanUserService.getBalanceAcctDTOByAccountId(balanceAcctId);
        if (Objects.isNull(balanceAcctDTO)) {
            String message = String.format("[%s]电子账簿信息不存在", balanceAcctId);
            log.error(String.format(message));
            return Result.failed(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
        }
        return Result.ok(balanceAcctDTO);
    }

    @Override
    public Result<Map<String, BalanceAcctRespDTO>> listBalanceByAccountIds(List<String> balanceAcctIds) {
        log.info("批量查询电子账户参数信息:{}", JSONObject.toJSONString(balanceAcctIds));
        if (CollectionUtil.isEmpty(balanceAcctIds)) {
            return Result.failed(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
        }
        Map<String, BalanceAcctRespDTO> result = new HashMap<>(balanceAcctIds.size());
        for (String balanceAcctId : balanceAcctIds) {
            BalanceAcctRespDTO balanceAcctDTOByAccountId = loanUserService.getBalanceAcctDTOByAccountId(balanceAcctId);
            result.put(balanceAcctId, balanceAcctDTOByAccountId);
        }
        log.info("批量查询电子账户返回信息:{}", JSONObject.toJSONString(result));
        return Result.ok(result);
    }

    @Override
    public Result<LoanTransferToTfRespDTO> getBalanceAcctId(String type, String bid) {
        if (StringUtil.isBlank(type) || StringUtil.isBlank(bid)) {
            return Result.failed(PayExceptionCodeEnum.TREAD_PARAMETER_ILLEGAL);
        }
        LoanTransferToTfRespDTO loanTransferToTfDTO = new LoanTransferToTfRespDTO();
        loanTransferToTfDTO.setTfBalanceAcctId(accountConfig.getBalanceAcctId());
        loanTransferToTfDTO.setTfBalanceAcctName(accountConfig.getBalanceAcctName());
        LoanBalanceAcctRespDTO balanceAcc = loanBalanceAcctService.getBalanceAcctIdByBidAndType(bid, type);
        if (Objects.isNull(balanceAcc)) {
            throw new TfException(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
        }
        loanTransferToTfDTO.setBalanceAcctId(balanceAcc.getBalanceAcctId());
        loanTransferToTfDTO.setBalanceAcctName(balanceAcc.getBalanceAcctName());
        return Result.ok(loanTransferToTfDTO);
    }

    @Override
    public Result<Map<String, Object>> incomingIsFinish(String type, String bid) {
        if (StringUtil.isBlank(type) || StringUtil.isBlank(bid)) {
            return Result.failed(PayExceptionCodeEnum.TREAD_PARAMETER_ILLEGAL);
        }
        Map<String, Object> result = new HashMap<>();
        BigDecimal balance = new BigDecimal("0");
        LoanUserEntity loanUser = this.loanUserService.getOne(new QueryWrapper<LoanUserEntity>()
                .eq("type", type).eq("bus_id", bid).eq("application_status", "succeeded"));
        if (org.apache.commons.lang3.ObjectUtils.isNotEmpty(loanUser)) {
            //进件完成，查询余额信息
            LoanBalanceAcctRespDTO balanceAcc = loanBalanceAcctService.getBalanceAcctIdByBidAndType(bid, type);
            if (Objects.isNull(balanceAcc)) {
                return Result.failed(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
            }
            LoanAccountDTO loanAccountDTO = unionPayService.getLoanAccount(balanceAcc.getBalanceAcctId());
            if (org.apache.commons.lang3.ObjectUtils.isNotEmpty(loanAccountDTO)) {
                Long settledAmount = loanAccountDTO.getSettledAmount() == null ? 0 : loanAccountDTO.getSettledAmount();
                BigDecimal bigDecimal = new BigDecimal(100);
                balance = new BigDecimal(settledAmount).divide(bigDecimal);
                result.put("isFrozen", loanAccountDTO.isFrozen());
            }
            result.put("isIncoming", true);
            result.put("settledAmount", balance);
//            result.put("isFrozen", loanAccountDTO.isFrozen());
            return Result.ok(result);
        }
        result.put("isIncoming", false);
        result.put("settledAmount", balance);
        result.put("isFrozen", true);
        return Result.ok(result);
    }

    @Override
    public Result<Map<String, Object>> listIncomingIsFinish(List<UnionPayIncomingDTO> list) {
        log.info("listIncomingIsFinish 入参:{}", JSONObject.toJSONString(list));
        if (CollectionUtil.isEmpty(list)) {
            return Result.failed(PayExceptionCodeEnum.TREAD_PARAMETER_ILLEGAL);
        }
        try {
            Map<String, List<UnionPayIncomingDTO>> collect = list.stream().collect(Collectors.groupingBy(UnionPayIncomingDTO::getType));
            List<UnionPayIncomingDTO> shops = collect.get(NumberConstant.ONE.toString());
            List<UnionPayIncomingDTO> dealers = collect.get(NumberConstant.TWO.toString());
            if (CollectionUtil.isEmpty(shops)) {
                return Result.failed(PayExceptionCodeEnum.PAYER_NOT_FOUND);
            }
            if (shops.size() > NumberConstant.ONE) {
                return Result.failed(PayExceptionCodeEnum.PAYER_TOO_MUCH);
            }
            if (CollectionUtil.isEmpty(dealers)) {
                return Result.failed(PayExceptionCodeEnum.PAYEE_NOT_FOUND);
            }
            UnionPayIncomingDTO unionPayIncomingDTO = shops.get(NumberConstant.ZERO);
            Map<String, Object> returnMap = new HashMap<>();
            Result<Map<String, Object>> map = incomingIsFinish(unionPayIncomingDTO.getType(), unionPayIncomingDTO.getBid());
            if (map.getCode() != NumberConstant.ZERO) {
                return Result.failed(map.getMsg());
            }
            returnMap.put("supplierFrozen", false);
            returnMap.put("supplierIncoming", true);
            returnMap.put("shopFrozen", map.getData().get("isFrozen"));
            returnMap.put("shopIncoming", map.getData().get("isIncoming"));
            returnMap.put("shopSettledAmount", map.getData().get("settledAmount"));
            for (UnionPayIncomingDTO unionPayIncoming : dealers) {
                Result<Map<String, Object>> mapResult = incomingIsFinish(unionPayIncoming.getType(), unionPayIncoming.getBid());
                if (mapResult.getCode() == NumberConstant.ZERO) {
                    Map<String, Object> data = mapResult.getData();
                    if (!Boolean.parseBoolean(data.get("isIncoming").toString())) {
                        returnMap.put("supplierIncoming", false);
                        return Result.ok(returnMap);
                    }
                    if (Boolean.parseBoolean(data.get("isFrozen").toString())) {
                        returnMap.put("supplierFrozen", true);
                        return Result.ok(returnMap);
                    }
                } else {
                    return Result.failed(mapResult.getMsg());
                }
            }
            log.info("listIncomingIsFinish 出参:{}", JSONObject.toJSONString(returnMap));
            return Result.ok(returnMap);
        } catch (TfException e) {
            log.error("批量判断进件是否完成tfException:{}", e.getMessage());
            return Result.failed(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.failed(PayExceptionCodeEnum.BALANCE_ACCOUNT_NAME_ERROR);
    }

    @Override
    public Result<List<CustBankInfoRespDTO>> getCustBankInfoList(Integer type, String bid) {
        try {
            log.info("参数：bid={},type={}", bid, type);
            LoanUserEntity loanUser = loanUserService.getLoanUserByBusIdAndType(bid, type);
            if (org.apache.commons.lang3.ObjectUtils.isEmpty(loanUser)) {
                return Result.failed(PayExceptionCodeEnum.NO_LOAN_USER.getMsg());
            }
            List<BankInfoDTO> bankInfoByBus = custBankInfoService.getBankInfoByBus(loanUser.getId());
            List<CustBankInfoRespDTO> custBankInfoResp = com.tfjt.tfcommon.core.util.BeanUtils.copyList2Other(CustBankInfoRespDTO.class, bankInfoByBus);
            return Result.ok(custBankInfoResp);
        } catch (TfException ex) {
            return Result.failed(ex.getMessage());
        }
    }

    @Override
    public Result<BalanceAcctRespDTO> getAccountInfoByBusId(String type, String busId) {
        Result<List<BalanceAcctRespDTO>> listResult = this.listAccountInfoByBusId(type, Collections.singletonList(busId));
        List<BalanceAcctRespDTO> data = listResult.getData();
        if (CollectionUtil.isEmpty(data)) {
            throw new TfException(ExceptionCodeEnum.ILLEGAL_ARGUMENT);
        }
        return Result.ok(data.get(0));
    }

    @Override
    public Result<List<BalanceAcctRespDTO>> listAccountInfoByBusId(String type, List<String> busIds) {
        List<UnionPayLoanUserRespDTO> unionPayLoanUserRespDTOS = loanUserService.listLoanUserByBusId(type, busIds);
        if (CollectionUtil.isEmpty(unionPayLoanUserRespDTOS)) {
            return Result.ok(new ArrayList<>());
        }
        List<BalanceAcctRespDTO> list = com.tfjt.tfcommon.core.util.BeanUtils.copyList2Other(BalanceAcctRespDTO.class, unionPayLoanUserRespDTOS);
        return Result.ok(list);
    }

    @Override
    public LoanUserEntity getById(Long id) {
        return loanUserService.getById(id);
    }

    @Override
    public Result<DepositRespDTO> deposit(Long amount, String orderNo) {
        String isIdempotent = redisCache.getCacheString(DEPOSIT_IDEMPOTENT_KEY);
        log.info("防重复提交的订单号为：{}", isIdempotent);
        if (!orderNo.equals(isIdempotent)) {
            redisCache.setCacheString(DEPOSIT_IDEMPOTENT_KEY, orderNo, 60, TimeUnit.MINUTES);
            DepositReqDTO depositReqDTO = new DepositReqDTO();
            depositReqDTO.setOutOrderNo(orderNo);
            depositReqDTO.setSentAt(DateUtil.getByRFC3339(new Date()));
            depositReqDTO.setTotalAmount(amount);
            depositReqDTO.setAmount(amount);
            depositReqDTO.setDiscountAmount(0L);
            depositReqDTO.setDepositType(DepositTypeEnum.DEPOSIT.getCode());
            depositReqDTO.setPaymentType(TF_PAY);
            depositReqDTO.setBalanceAcctId(accountConfig.getBalanceAcctId());
            String tradeNo = InstructIdUtil.getInstructId(CommonConstants.LOAN_DEPOSIT_NO_PREFIX, new Date(), UnionPayTradeResultCodeConstant.TRADE_RESULT_CODE_20, redisCache);
            depositReqDTO.setPaymentTradeNo(tradeNo);
            depositReqDTO.setPaymentSucceededAt(DateUtil.getByRFC3339(new Date()));
            DepositExtraReqDTO extraReqDTO = new DepositExtraReqDTO();
            extraReqDTO.setNotifyUrl(accountConfig.getNotifyUrl());
            ProductInfoReqDTO productInfoReqDTO = new ProductInfoReqDTO();
            productInfoReqDTO.setProductCount(1);
            productInfoReqDTO.setOrderAmount(amount);
            productInfoReqDTO.setOrderNo(tradeNo);
            productInfoReqDTO.setProductName("充值");
            List<ProductInfoReqDTO> productInfoReqDTOList = new ArrayList<>();
            productInfoReqDTOList.add(productInfoReqDTO);
            extraReqDTO.setProductInfos(productInfoReqDTOList);
            depositReqDTO.setExtra(extraReqDTO);
            Result<DepositRespDTO> depositResult = unionPayService.deposit(depositReqDTO);
            if(depositResult.getCode() == NumberConstant.ZERO){
                return Result.ok(depositResult.getData());
            }else{
                return Result.failed(depositResult.getMsg());
            }
        } else {
            return Result.failed(PayExceptionCodeEnum.REPEAT_OPERATION);
        }
    }


    private void checkLoanUser(String busId, Integer type) {
        LoanUserEntity loanUser = loanUserService.getLoanUserByBusIdAndType(busId, type);
        if (loanUser == null) {
            throw new TfException(PayExceptionCodeEnum.NO_LOAN_USER);
        }
    }
}
