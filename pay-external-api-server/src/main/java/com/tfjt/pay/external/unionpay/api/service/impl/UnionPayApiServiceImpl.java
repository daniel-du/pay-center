package com.tfjt.pay.external.unionpay.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.tfjt.pay.external.unionpay.api.dto.req.BalanceDivideReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.SubBalanceDivideReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.BalanceAcctDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.UnionPayTransferDTO;
import com.tfjt.pay.external.unionpay.api.service.UnionPayApiService;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.TransactionTypeConstants;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayDivideReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.UnionPayDivideSubReq;
import com.tfjt.pay.external.unionpay.dto.resp.LoanAccountDTO;
import com.tfjt.pay.external.unionpay.entity.PayBalanceDivideEntity;
import com.tfjt.pay.external.unionpay.service.PayBalanceDivideService;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.pay.external.unionpay.utils.OrderNumberUtil;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 银联接口服务实现类
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
    private PayBalanceDivideService payBalanceDivideService;

    @Autowired
    private OrderNumberUtil orderNumberUtil;
    @Override
    public Result<String> transfer(UnionPayTransferDTO payTransferDTO) {
        return null;
    }

    @Override
    public Result<Integer> currentBalance() {
        BalanceAcctDTO balanceAcctDTOByAccountId = getBalanceAcctDTOByAccountId(accountConfig.getBalanceAcctId());
        log.debug("查询母账户交易余额返回:{}",balanceAcctDTOByAccountId.getSettledAmount());
        return Result.ok(balanceAcctDTOByAccountId.getSettledAmount());
    }

    @Override
    public Result<BalanceAcctDTO> getBalanceByAccountId(String balanceAcctId) {
        log.debug("查询电子账簿id:{}",balanceAcctId);
        if(StringUtil.isBlank(balanceAcctId)){
            return Result.failed("电子账簿id不能为空");
        }
        BalanceAcctDTO balanceAcctDTO = getBalanceAcctDTOByAccountId(balanceAcctId);
        if (Objects.isNull(balanceAcctDTO)){
            String message = String.format("[%s]电子账簿信息不存在", balanceAcctId);
            log.error(String.format(message));
            return Result.failed(message);
        }
        return Result.ok(balanceAcctDTO);
    }

    @Override
    public Result<Map<String,BalanceAcctDTO>> listBalanceByAccountIds(List<String> balanceAcctIds) {
        log.debug("批量查询电子账户参数信息:{}",JSONObject.toJSONString(balanceAcctIds));
        if (CollectionUtil.isEmpty(balanceAcctIds)){
            return Result.failed("电子账簿id不能为空");
        }
        Map<String,BalanceAcctDTO> result = new HashMap<>(balanceAcctIds.size());
        for (String balanceAcctId : balanceAcctIds) {
            BalanceAcctDTO balanceAcctDTOByAccountId = getBalanceAcctDTOByAccountId(balanceAcctId);
            if (!Objects.isNull(balanceAcctDTOByAccountId)){
                result.put(balanceAcctId,balanceAcctDTOByAccountId);
            }
        }
        log.debug("批量查询电子账户返回信息:{}",JSONObject.toJSONString(result));
        return Result.ok(result);
    }

    @Lock4j(keys = {"#balanceDivideReq.businessOrderNo"},expire = 10000, acquireTimeout = 3000)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result balanceDivide(BalanceDivideReqDTO balanceDivideReq) {
        log.debug("请求分账参数<<<<<<<<<<<<<<<<:{}",JSONObject.toJSONString(balanceDivideReq));
        //检验订单号是否存在
        String businessOrderNo = balanceDivideReq.getBusinessOrderNo();
        if(this.payBalanceDivideService.checkExistBusinessOrderNo(businessOrderNo)){
            String message = String.format("业务订单号[%s]已经存在", businessOrderNo);
            log.error(message);
            return Result.failed(message);
        }
        //保存分账信息
        List<SubBalanceDivideReqDTO> list = balanceDivideReq.getList();
        List<PayBalanceDivideEntity> saveList = new ArrayList<>(list.size());
        Date date = new Date();
        //主交易单号,银联交互使用
        String tradeOrderNo = orderNumberUtil.generateOrderNumber(TransactionTypeConstants.TRANSACTION_TYPE_DB);
        for (SubBalanceDivideReqDTO subBalanceDivideReqDTO : list) {
            saveList.add(buildPayBalanceDivideEntity(subBalanceDivideReqDTO,balanceDivideReq.getBusinessSystemId(),balanceDivideReq.getUserId(),businessOrderNo,date,tradeOrderNo));
        }
        if(!this.payBalanceDivideService.saveBatch(saveList)){
            log.error("保存分账信息失败:{}",JSONObject.toJSONString(saveList));
            return Result.failed("分账失败,保存分账信息失败");
        }
        //生成银联分账参数
        UnionPayDivideReqDTO unionPayDivideReqDTO = builderBalacneDivideUnionPayParam(saveList, tradeOrderNo);
        //调用银联接口
        unionPayService.balanceDivide(unionPayDivideReqDTO);
        return null;
    }

    /**
     * 生成银联分账参数
     *
     * @param saveList
     * @param tradeOrderNo
     */
    private UnionPayDivideReqDTO builderBalacneDivideUnionPayParam(List<PayBalanceDivideEntity> saveList, String tradeOrderNo) {
        UnionPayDivideReqDTO unionPayDivideReqDTO = new UnionPayDivideReqDTO();
        unionPayDivideReqDTO.setPayBalanceAcctId(accountConfig.getBalanceAcctId());
        unionPayDivideReqDTO.setOutOrderNo(tradeOrderNo);
        List<UnionPayDivideSubReq> transferParams = new ArrayList<>(saveList.size());
        for (PayBalanceDivideEntity payBalanceDivideEntity : saveList) {
            UnionPayDivideSubReq unionPayDivideSubReq = new UnionPayDivideSubReq();
            BeanUtil.copyProperties(payBalanceDivideEntity,unionPayDivideSubReq);
            transferParams.add(unionPayDivideSubReq);
        }
        unionPayDivideReqDTO.setTransferParams(transferParams);
        return unionPayDivideReqDTO;
    }

    /**
     * 创建分账信息
     *
     * @param subBalanceDivideReqDTO 分账信息
     * @param businessSystemId       交易系统id
     * @param userId                 业务系统id
     * @param businessOrderNo        分账订单号不能为空
     * @param date                   创建时间
     * @param tradeOrderNo           主交易单号
     */
    private PayBalanceDivideEntity buildPayBalanceDivideEntity(SubBalanceDivideReqDTO subBalanceDivideReqDTO, String businessSystemId, Long userId, String businessOrderNo, Date date, String tradeOrderNo) {
        PayBalanceDivideEntity payBalanceDivideEntity = new PayBalanceDivideEntity();
        BeanUtil.copyProperties(payBalanceDivideEntity,subBalanceDivideReqDTO);
        payBalanceDivideEntity.setBusinessSystemId(businessSystemId);
        payBalanceDivideEntity.setUserId(userId);
        payBalanceDivideEntity.setBusinessOrderNo(businessOrderNo);
        payBalanceDivideEntity.setCreateTime(date);
        payBalanceDivideEntity.setTradeOrderNo(tradeOrderNo);
        payBalanceDivideEntity.setSubBusinessOrderNo(orderNumberUtil.generateOrderNumber(TransactionTypeConstants.TRANSACTION_TYPE_DB_SUB));
        return payBalanceDivideEntity;
    }

    /**
     * 获取指定电子账簿的账户信息
     * @param balanceAcctId 账户账户id
     * @return 电子账户信息
     */
    private BalanceAcctDTO getBalanceAcctDTOByAccountId(String balanceAcctId) {
        LoanAccountDTO loanAccountDTO = unionPayService.getLoanAccount(balanceAcctId);
        if (Objects.isNull(loanAccountDTO)){
            return null;
        }
        BalanceAcctDTO balanceAcctDTO = new BalanceAcctDTO();
        BeanUtil.copyProperties(loanAccountDTO,balanceAcctDTO);
        return balanceAcctDTO;
    }


}
