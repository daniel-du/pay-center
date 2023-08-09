package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.dto.*;
import com.tfjt.pay.external.unionpay.entity.CustBankInfoEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceAcctEntity;
import com.tfjt.pay.external.unionpay.entity.LoanCallbackEntity;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.tfcloud.business.api.TfLoanUserRpcService;
import com.tfjt.tfcloud.business.dto.TfLoanUserEntityDTO;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UnionPayLoansCallbackApiServiceImpl implements UnionPayLoansCallbackApiService {

    @Autowired
    private LoanUserService tfLoanUserService;

    @Autowired
    private LoanBalanceAcctService tfLoanBalanceAcctService;

    @Autowired
    private LoanCallbackService tfLoanCallbackService;

    @Autowired
    private CustBankInfoService custBankInfoService;

    @DubboReference
    private TfLoanUserRpcService tfLoanUserRpcService;

    /**
     * 打款验证通知
     * @param eventData
     * @param unionPayLoansBaseCallBackDTO
     * @return
     */
    public Boolean settleAcctsValidateCallBack(AcctValidationParamDTO eventData, String settleAcctId, UnionPayLoansBaseCallBackDTO unionPayLoansBaseCallBackDTO) {
        log.info("打款验证通知-回调参数{}", JSONObject.toJSONString(eventData));

        //修改验证状态
        LoanUserEntity tfLoanUserEntity = tfLoanUserService.getBySettleAcctId(settleAcctId);
        if(tfLoanUserEntity==null){
            throw new TfException(ExceptionCodeEnum.NOT_NULL.getCode(), tfLoanUserEntity.getSettleAcctId()+"贷款用户信息不存在");
        }
        CustBankInfoEntity custBankInfo = custBankInfoService.getBankInfoByBankCardNoAndLoanUserId(eventData.getDestAcctNo(), tfLoanUserEntity.getId());
        if(custBankInfo== null){
            throw new TfException(ExceptionCodeEnum.NOT_NULL.getCode(), eventData.getDestAcctNo()+"银行卡信息不存在");
        }
        custBankInfo.setVerifyStatus("succeeded");
        custBankInfoService.updateById(custBankInfo);

        //同步业务用户表
        TfLoanUserEntityDTO tfLoanUserEntityDTO =new TfLoanUserEntityDTO();
        BeanUtil.copyProperties(tfLoanUserEntity, tfLoanUserEntityDTO);
        List<TfLoanUserEntityDTO> tfLoanUserEntityDTOList = new ArrayList<>();
        tfLoanUserEntityDTOList.add(tfLoanUserEntityDTO);
        tfLoanUserRpcService.updateBatch(tfLoanUserEntityDTOList);
        //保存日志
        tfLoanCallbackService.saveLog(custBankInfo.getLoanUserId(), unionPayLoansBaseCallBackDTO.getEventId(), unionPayLoansBaseCallBackDTO.getEventType(), JSONObject.toJSONString(eventData),unionPayLoansBaseCallBackDTO.getCreatedAt(), 1, eventData.getDestAcctNo());
        return true;

    }


    @Lock4j(keys = {"#unionPayLoansBaseCallBackDTO.eventId"}, expire = 3000, acquireTimeout = 2000)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean unionPayLoansBaseCallBack(UnionPayLoansBaseCallBackDTO unionPayLoansBaseCallBackDTO) {
        LoanCallbackEntity tfLoanCallbackEntity = tfLoanCallbackService.getOne(new LambdaQueryWrapper<LoanCallbackEntity>().eq(LoanCallbackEntity::getEventId, unionPayLoansBaseCallBackDTO.getEventId()));
        if(tfLoanCallbackEntity!=null){
            log.info("事件回调已添加{}",unionPayLoansBaseCallBackDTO.getEventId());
            return true;
        }

        //1二级进件回调
        if(Objects.equals("mch_application_finished", unionPayLoansBaseCallBackDTO.getEventType())){
            TwoIncomingEventDataDTO twoIncomingEventDataDTO  = JSON.parseObject(JSONObject.toJSONString(unionPayLoansBaseCallBackDTO.getEventData()), TwoIncomingEventDataDTO.class);
            twoIncomingCallBack(twoIncomingEventDataDTO, unionPayLoansBaseCallBackDTO);
        }
        //2贷款回调
        if(Objects.equals("settle_acct_pay_amount_validation", unionPayLoansBaseCallBackDTO.getEventType())){
            SettleAcctsEventDataDTO eventDataDTO  = JSON.parseObject(JSONObject.toJSONString(unionPayLoansBaseCallBackDTO.getEventData()), SettleAcctsEventDataDTO.class);
            settleAcctsValidateCallBack(eventDataDTO.getAcctValidationParam(), eventDataDTO.getSettleAcctId(), unionPayLoansBaseCallBackDTO);
        }
        return true;
    }

    public Boolean twoIncomingCallBack(TwoIncomingEventDataDTO twoIncomingEventDataDTO, UnionPayLoansBaseCallBackDTO unionPayLoansBaseCallBackDTO) {
        log.info("二级商户进件回调结果通知-回调参数{}", JSONObject.toJSONString(twoIncomingEventDataDTO));
        LoanUserEntity tfLoanUserEntity = verifyIncomingCallBack(twoIncomingEventDataDTO);
        //修改货款商户
        updatTfLoanUserEntity(twoIncomingEventDataDTO, tfLoanUserEntity);
        //添加电子账单
        addTfLoanBalanceAcct(twoIncomingEventDataDTO.getRelAcctNo(),  twoIncomingEventDataDTO.getBalanceAcctId(), tfLoanUserEntity.getId());
        //保存记录
        tfLoanCallbackService.saveLog(tfLoanUserEntity.getId(), unionPayLoansBaseCallBackDTO.getEventId(), unionPayLoansBaseCallBackDTO.getEventType(), JSONObject.toJSONString(twoIncomingEventDataDTO),unionPayLoansBaseCallBackDTO.getCreatedAt(), 2, "");
        return true;
    }

    /**
     * 验证参数
     * @param twoIncomingEventDataDTO
     * @return
     */
    private LoanUserEntity verifyIncomingCallBack(TwoIncomingEventDataDTO twoIncomingEventDataDTO) {
        if(twoIncomingEventDataDTO== null || Objects.isNull(twoIncomingEventDataDTO.getMchApplicationId())){
            throw new TfException(ExceptionCodeEnum.NOT_NULL.getCode(), "系统订单号不能为空");
        }

        LoanUserEntity tfLoanUserEntity = tfLoanUserService.getOne(new LambdaQueryWrapper<LoanUserEntity>().eq(LoanUserEntity::getMchApplicationId, twoIncomingEventDataDTO.getMchApplicationId()));
        if(null == tfLoanUserEntity){
            throw new TfException(ExceptionCodeEnum.NOT_NULL.getCode(), "贷款-用户不存在");
        }
        return tfLoanUserEntity;
    }

    private void addTfLoanBalanceAcct(String relAcctNo, String balanceAcctId, Long loanUserId) {

        LoanBalanceAcctEntity old = tfLoanBalanceAcctService.getTfLoanBalanceAcctEntity(relAcctNo, balanceAcctId, loanUserId);
        if(old == null){
            LoanBalanceAcctEntity tfLoanBalanceAcctEntity = new LoanBalanceAcctEntity();
            tfLoanBalanceAcctEntity.setLoanUserId(Integer.valueOf(String.valueOf(loanUserId)));
            if (StringUtils.isNotBlank(relAcctNo)) {
                tfLoanBalanceAcctEntity.setRelAcctNo(relAcctNo);
            }
            if (StringUtils.isNotBlank(balanceAcctId)) {
                tfLoanBalanceAcctEntity.setBalanceAcctId(balanceAcctId);
            }
            tfLoanBalanceAcctService.save(tfLoanBalanceAcctEntity);
        }
    }

    /**
     * 修改用户信息
     * @param twoIncomingEventDataDTO
     */
    private void updatTfLoanUserEntity(TwoIncomingEventDataDTO twoIncomingEventDataDTO, LoanUserEntity tfLoanUserEntity) {
        tfLoanUserEntity.setApplicationStatus(twoIncomingEventDataDTO.getApplicationStatus());
        if (StringUtils.isNotBlank(twoIncomingEventDataDTO.getOutRequestNo())) {
            tfLoanUserEntity.setOutRequestNo(twoIncomingEventDataDTO.getOutRequestNo());
        }

        if(!Objects.isNull(twoIncomingEventDataDTO.getAuditedAt())){
            tfLoanUserEntity.setAuditedAt(twoIncomingEventDataDTO.getAuditedAt());
        }
        if(Objects.equals("succeeded",twoIncomingEventDataDTO.getApplicationStatus() )){
            if(!Objects.isNull(twoIncomingEventDataDTO.getSucceededAt())){
                tfLoanUserEntity.setSucceededAt(twoIncomingEventDataDTO.getSucceededAt());
            }
            if(StringUtils.isNotBlank(twoIncomingEventDataDTO.getMchId())){
                tfLoanUserEntity.setMchId(twoIncomingEventDataDTO.getMchId());
            }
        }

        if(Objects.equals("failed",twoIncomingEventDataDTO.getApplicationStatus() )){
            if(!Objects.isNull(twoIncomingEventDataDTO.getFailedAt())){
                tfLoanUserEntity.setFailedAt(twoIncomingEventDataDTO.getFailedAt());
            }

            if(twoIncomingEventDataDTO.getFailureMsgs()!=null && twoIncomingEventDataDTO.getFailureMsgs().size()>0){
                String param = getParam(twoIncomingEventDataDTO.getFailureMsgs());
                String reason = getReason(twoIncomingEventDataDTO.getFailureMsgs());
                if(StringUtils.isNotBlank(param)){
                    tfLoanUserEntity.setFailureMsgsParam(param);
                }
                if(StringUtils.isNotBlank(reason)){
                    tfLoanUserEntity.setFailureMsgsReason(reason);
                }
            }
        }

        tfLoanUserService.updateById(tfLoanUserEntity);
    }

    private String getParam(List<LwzRespReturn> lwzRespReturnList) {
        String msg = "";
        String names = lwzRespReturnList.stream().map(LwzRespReturn::getReason).collect(Collectors.joining(";"));
        if(StringUtils.isNotBlank(names)){
            return names;
        }
        return msg;
    }

    private String getReason(List<LwzRespReturn> lwzRespReturnList) {
        String msg = "";
        String names = lwzRespReturnList.stream().map(LwzRespReturn::getReason).collect(Collectors.joining(";"));
        if(StringUtils.isNotBlank(names)){
            return names;
        }
        return msg;
    }

    }

