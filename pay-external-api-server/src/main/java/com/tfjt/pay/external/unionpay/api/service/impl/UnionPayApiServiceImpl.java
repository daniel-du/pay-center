package com.tfjt.pay.external.unionpay.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tfjt.pay.external.unionpay.api.dto.req.BalanceDivideReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.LoanOrderUnifiedorderReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.SubBalanceDivideReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.WithdrawalReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.BalanceAcctRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.SubBalanceDivideRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.UnionPayTransferRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.WithdrawalRespDTO;
import com.tfjt.pay.external.unionpay.api.service.UnionPayApiService;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.TransactionTypeConstants;
import com.tfjt.pay.external.unionpay.dto.UnionPayProduct;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayDivideReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayDivideSubReq;
import com.tfjt.pay.external.unionpay.dto.req.WithdrawalCreateReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.LoanAccountDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayDivideRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayDivideRespDetailDTO;
import com.tfjt.pay.external.unionpay.dto.resp.WithdrawalCreateRespDTO;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.enums.UnionPayBusinessTypeEnum;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.pay.external.unionpay.utils.OrderNumberUtil;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.pay.external.unionpay.utils.UnionPaySignUtil;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
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

    @Resource
    LoanWithdrawalOrderService withdrawalOrderService;

    @Value("${unionPayLoans.encodedPub}")
    private String encodedPub;

    @Value("backcall")
    private String notifyUrl;

    @Override
    public Result<String> transfer(UnionPayTransferRespDTO payTransferDTO) {
        return null;
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
        log.debug("请求分账参数<<<<<<<<<<<<<<<<:{}", JSONObject.toJSONString(balanceDivideReq));
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
        String tradeOrderNo = orderNumberUtil.generateOrderNumber(TransactionTypeConstants.TRANSACTION_TYPE_DB);
        LoadBalanceDivideEntity payBalanceDivideEntity = new LoadBalanceDivideEntity();
        payBalanceDivideEntity.setPayBalanceAcctId(accountConfig.getBalanceAcctId());
        payBalanceDivideEntity.setTradeOrderNo(tradeOrderNo);
        payBalanceDivideEntity.setBusinessOrderNo(balanceDivideReq.getBusinessOrderNo());
        payBalanceDivideEntity.setBusinessSystemId(balanceDivideReq.getBusinessSystemId());
        payBalanceDivideEntity.setCreateAt(date);
        if (!payBalanceDivideService.save(payBalanceDivideEntity)) {
            log.error("保存分账主信息失败:{}", JSONObject.toJSONString(payBalanceDivideEntity));
            return Result.failed("保存分账主信息失败");
        }
        //3.保存子分账信息
        List<SubBalanceDivideReqDTO> list = balanceDivideReq.getList();
        List<LoanBalanceDivideDetailsEntity> saveList = new ArrayList<>(list.size());

        for (SubBalanceDivideReqDTO subBalanceDivideReqDTO : list) {
            saveList.add(buildPayBalanceDivideDetailsEntity(subBalanceDivideReqDTO, date, payBalanceDivideEntity.getId()));
        }
        if (!this.payBalanceDivideDetailsService.saveBatch(saveList)) {
            log.error("保存分账信息失败:{}", JSONObject.toJSONString(saveList));
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed("分账失败,保存分账信息失败");
        }
        //4.生成银联分账参数
        UnionPayDivideReqDTO unionPayDivideReqDTO = builderBalanceDivideUnionPayParam(saveList, tradeOrderNo);
        //5.调用银联接口
        log.info("调用银联分账信息发送信息>>>>>>>>>>>>>>>:{}", JSONObject.toJSONString(unionPayDivideReqDTO));
        Result<UnionPayDivideRespDTO> result = unionPayService.balanceDivide(unionPayDivideReqDTO);
        log.info("调用银联分账信息返回信息<<<<<<<<<<<<<<<:{}", JSONObject.toJSONString(result));
        if (result.getCode() != NumberConstant.ZERO) {
            log.error("调用银联分账接口失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(result.getMsg());
        }
        //6.解析返回数据响应给业务系统
        return Result.ok(parseUnionPayDivideReqDTOToMap(result.getData()));
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
        Result<WithdrawalCreateRespDTO> withdrawalCreateResp= unionPayService.withdrawalCreation(withdrawalCreateReqDTO);
        WithdrawalRespDTO withdrawalRespDTO = BeanUtil.copyProperties(withdrawalCreateResp, WithdrawalRespDTO.class);
        if (withdrawalCreateResp.getCode() != NumberConstant.ZERO) {
            return Result.failed(withdrawalCreateResp.getMsg());
        }else{
            //更新状态
            loanWithdrawalOrderEntity.setStatus(withdrawalRespDTO.getStatus());
            withdrawalOrderService.updateById(loanWithdrawalOrderEntity);
            return Result.ok(withdrawalRespDTO);
        }

    }

    @Override
    public Result unifiedorder(LoanOrderUnifiedorderReqDTO loanOrderUnifiedorderDTO) {
        return null;
    }

    @Override
    public Result downloadCheckBill(String date, Long userId) {

        return null;
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
    private UnionPayDivideReqDTO builderBalanceDivideUnionPayParam(List<LoanBalanceDivideDetailsEntity> saveList, String tradeOrderNo) {
        UnionPayDivideReqDTO unionPayDivideReqDTO = new UnionPayDivideReqDTO();
        unionPayDivideReqDTO.setPayBalanceAcctId(accountConfig.getBalanceAcctId());
        unionPayDivideReqDTO.setOutOrderNo(tradeOrderNo);
        List<UnionPayDivideSubReq> transferParams = new ArrayList<>(saveList.size());
        for (LoanBalanceDivideDetailsEntity payBalanceDivideEntity : saveList) {
            UnionPayDivideSubReq unionPayDivideSubReq = new UnionPayDivideSubReq();
            BeanUtil.copyProperties(payBalanceDivideEntity, unionPayDivideSubReq);
            UnionPayProduct unionPayProduct = new UnionPayProduct();
            unionPayProduct.setOrderAmount(payBalanceDivideEntity.getAmount());
            unionPayProduct.setProductName(payBalanceDivideEntity.getRecvBalanceAcctName()+"分账");
            unionPayProduct.setOrderNo(payBalanceDivideEntity.getSubBusinessOrderNo());
            unionPayProduct.setProductCount(NumberConstant.ONE);
            List<UnionPayProduct> list = new ArrayList<>(NumberConstant.ONE);
            list.add(unionPayProduct);
            HashMap<String, List<UnionPayProduct>> extra = new HashMap<>();
            extra.put("productInfos",list);
            unionPayDivideSubReq.setExtra(extra);
            transferParams.add(unionPayDivideSubReq);
        }
        unionPayDivideReqDTO.setTransferParams(transferParams);
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


}
