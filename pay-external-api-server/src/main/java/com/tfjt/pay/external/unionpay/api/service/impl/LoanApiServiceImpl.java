package com.tfjt.pay.external.unionpay.api.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayIncomingDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.*;
import com.tfjt.pay.external.unionpay.api.service.LoanApiService;
import com.tfjt.pay.external.unionpay.biz.LoanUserBizService;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansApiBizService;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansBizService;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.enums.BusinessUserTypeEnum;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Lzh
 * @version 1.0
 * @title 进件是否完成
 * @description
 * @Date 2023/8/11 14:44
 */
@Slf4j
@DubboService
public class LoanApiServiceImpl implements LoanApiService {

    @Resource
    private LoanUserBizService loanUserBizService;


    @Resource
    UnionPayLoansBizService unionPayLoansBizService;


    @Resource
    UnionPayLoansApiBizService unionPayLoansApiBizService;


    @Override
    public Result<LoanTransferToTfRespDTO> getBalanceAcctId(String type, String bid) {
        return loanUserBizService.getBalanceAcctId(type, bid);
    }

    @Override
    public Result<Map<String, Object>> incomingIsFinish(String type, String bid) {
        return loanUserBizService.incomingIsFinish(type, bid);
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
        return loanUserBizService.getCustBankInfoList(type, bid);
    }


    @Override
    public Result<BalanceAcctRespDTO> getAccountInfoByBusId(String type, String busId) {
        return loanUserBizService.getAccountInfoByBusId(type, busId);

    }

    @Override
    public Result<List<BalanceAcctRespDTO>> listAccountInfoByBusId(String type, List<String> busIds) {
        return loanUserBizService.listAccountInfoByBusId(type, busIds);
    }

    @Override
    public Result<String> unbindSettleAcct(BankInfoReqDTO bankInfoReqDTO) {
        unionPayLoansBizService.unbindSettleAcct(bankInfoReqDTO);
        return Result.ok(PayExceptionCodeEnum.UNBIND_BANK_CARD_SUCCESS.getMsg());
    }

    @Override
    public Result<String> bindSettleAcct(BankInfoReqDTO bankInfoReqDTO) {
        String boundSettleAcctId = unionPayLoansBizService.bindSettleAcct(bankInfoReqDTO);
        return Result.ok(boundSettleAcctId);
    }


    @Override
    public Result<String> unbindParentSettleAcct(Long loanUserId) {
        List<CustBankInfoRespDTO> custBankInfoRespDTOList = unionPayLoansBizService.getBankInfoByLoanUserId(-1L);
        if (custBankInfoRespDTOList != null && custBankInfoRespDTOList.size() == 1) {
            throw new TfException(PayExceptionCodeEnum.LAST_ONE_BANK_CARD);
        }
        if (custBankInfoRespDTOList != null) {
            BankInfoReqDTO bankInfoReqDTO = new BankInfoReqDTO();
            CustBankInfoRespDTO custBankInfoRespDTO = custBankInfoRespDTOList.get(0);
            bankInfoReqDTO.setBankCardNo(custBankInfoRespDTO.getBankCardNo());
            bankInfoReqDTO.setSettlementType(String.valueOf(custBankInfoRespDTO.getSettlementType()));
            bankInfoReqDTO.setBankCardNo(bankInfoReqDTO.getBankCardNo());
            bankInfoReqDTO.setBankCode(bankInfoReqDTO.getBankCode());
            bankInfoReqDTO.setBankBranchCode(bankInfoReqDTO.getBankBranchCode());
            bankInfoReqDTO.setType(BusinessUserTypeEnum.SUPPLIER.getCode());
            bankInfoReqDTO.setBusId("0");
            unionPayLoansBizService.unbindSettleAcct(bankInfoReqDTO);
            log.info("{}解绑成功！",custBankInfoRespDTO.getBankCardNo());
        }
        return Result.ok();
    }

    @Override
    public Result<UnionPayLoansSettleAcctDTO> settleAcctsValidate(Long loanUserId, Integer payAmount) {
        return Result.ok(unionPayLoansApiBizService.settleAcctsValidate(loanUserId, payAmount, null));
    }

    @Override
    public Result<UnionPayLoansSettleAcctDTO> settleAcctsValidate(Long loanUserId, Integer payAmount, String settleAcctId) {
        return Result.ok(unionPayLoansApiBizService.settleAcctsValidate(loanUserId, payAmount, settleAcctId));
    }

    @Override
    public Result<String> getAcctValidateStatus(Integer type, String bid) {
        Result<List<CustBankInfoRespDTO>> result = loanUserBizService.getCustBankInfoList(type, bid);
        if (result.getCode() == NumberConstant.ZERO) {
            List<CustBankInfoRespDTO> data = result.getData();
            if (CollUtil.isNotEmpty(data)) {
                boolean bool = data.stream().anyMatch(s -> s.getValidateStatus() == 1 && s.getSettlementType() == 2);
                return Result.ok(String.valueOf(bool));
            } else {
                return Result.failed(PayExceptionCodeEnum.NO_DATA.getMsg());
            }
        } else {
            return Result.failed(PayExceptionCodeEnum.NO_DATA.getMsg());
        }

    }

    @Override
    public Result<DepositRespDTO> deposit(Integer amount, String orderNo) {
        return loanUserBizService.deposit(amount, orderNo);
    }

    @Override
    public Result<List<BankCodeRespDTO>> getBankCodeByName(String bankName) {
        return unionPayLoansApiBizService.getBankCodeByName(bankName);
    }

}
