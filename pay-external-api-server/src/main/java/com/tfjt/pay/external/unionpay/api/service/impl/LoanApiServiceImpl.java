package com.tfjt.pay.external.unionpay.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayIncomingDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.BalanceAcctRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.BankInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.CustBankInfoRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.LoanTransferToTfRespDTO;
import com.tfjt.pay.external.unionpay.api.service.LoanApiService;
import com.tfjt.pay.external.unionpay.biz.LoanUserBizService;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansBizService;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dao.LoanUserDao;
import com.tfjt.pay.external.unionpay.dto.BankInfoDTO;
import com.tfjt.pay.external.unionpay.dto.resp.LoanAccountDTO;
import com.tfjt.pay.external.unionpay.dto.resp.LoanBalanceAcctRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayLoanUserRespDTO;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import com.tfjt.tfcommon.dto.response.Result;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Lzh
 * @version 1.0
 * @title 进件是否完成
 * @description
 * @Date 2023/8/11 14:44
 */
@Slf4j
@DubboService
public class LoanApiServiceImpl  implements LoanApiService {

    @Resource
    private LoanUserBizService loanUserBizService;


    @Autowired
    private LoanUserService loanUserService;


    @Resource
    UnionPayLoansBizService unionPayLoansBizService;


    @Override
    public Result<LoanTransferToTfRespDTO> getBalanceAcctId(String type, String bid) {
        return loanUserBizService.getBalanceAcctId(type,bid);
    }

    @Override
    public Result<Map<String, Object>> incomingIsFinish(String type, String bid) {
        return loanUserBizService.incomingIsFinish(type,bid);
    }

    @Override
    public Result<Map<String, Object>> listIncomingIsFinish(List<UnionPayIncomingDTO> list) {
        return loanUserBizService.listIncomingIsFinish(list);
    }

    /**
     * 获取银行卡
     *
     * @param type
     * @param bid  类型1商家2供应商
     * @return
     */
    @Override
    public Result<List<CustBankInfoRespDTO>> getCustBankInfoList(Integer type, String bid) {
        return loanUserBizService.getCustBankInfoList(type,bid);
    }


    @Override
    public Result<BalanceAcctRespDTO> getAccountInfoByBusId(String type, String busId) {
        return loanUserBizService.getAccountInfoByBusId(type,busId);

    }

    @Override
    public Result<List<BalanceAcctRespDTO>> listAccountInfoByBusId(String type, List<String> busIds) {
        return loanUserBizService.listAccountInfoByBusId(type,busIds);
    }

    @Override
    public Result<String> unbindSettleAcct(BankInfoReqDTO bankInfoReqDTO) {
        unionPayLoansBizService.unbindSettleAcct(bankInfoReqDTO);
        return Result.ok(PayExceptionCodeEnum.BIND_BANK_CARD_FAILED.getMsg());
    }

    @Override
    public Result<String> bindSettleAcct(BankInfoReqDTO bankInfoReqDTO) {
        boolean boundSettleAcct = unionPayLoansBizService.bindSettleAcct(bankInfoReqDTO);
        if (boundSettleAcct) {
            return Result.ok(PayExceptionCodeEnum.BIND_BANK_CARD_SUCCESS.getMsg());
        } else {
            return Result.failed(PayExceptionCodeEnum.BIND_BANK_CARD_FAILED.getMsg());
        }
    }

}
