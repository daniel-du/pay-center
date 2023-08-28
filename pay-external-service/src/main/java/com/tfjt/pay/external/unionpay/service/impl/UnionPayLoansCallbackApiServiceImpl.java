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
import org.springframework.transaction.annotation.Propagation;
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
     * 打款验证通知 settleAcctId 进件新增之后 修改不变
     * @param eventData
     * @param unionPayLoansBaseCallBackDTO
     * @return
     */
    public Long settleAcctsValidateCallBack(AcctValidationParamDTO eventData, String settleAcctId, String outRequestNo) {
        log.info("打款验证通知-平台号{},账号ID{}", outRequestNo, settleAcctId);
        log.info("打款验证通知-回调参数{}", JSONObject.toJSONString(eventData));
        LoanUserEntity tfLoanUserEntity = tfLoanUserService.getOne(new LambdaQueryWrapper<LoanUserEntity>().eq(LoanUserEntity::getOutRequestNo, outRequestNo));
        if(tfLoanUserEntity != null && StringUtils.isNotBlank(settleAcctId)){
            if(StringUtils.isBlank(tfLoanUserEntity.getSettleAcctId())){
                tfLoanUserEntity.setSettleAcctId(settleAcctId);
                tfLoanUserService.updateById(tfLoanUserEntity);
            }

        }
        //修改银行是否打款状态1是
        updateBankCallStatus(tfLoanUserEntity);

        return tfLoanUserEntity.getId();

    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateBankCallStatus(LoanUserEntity tfLoanUserEntity ) {
        tfLoanUserEntity.setBankCallStatus(1);
        tfLoanUserService.updateById(tfLoanUserEntity);
    }


    @Lock4j(keys = {"#unionPayLoansBaseCallBackDTO.eventId"}, expire = 3000, acquireTimeout = 2000)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long unionPayLoansBaseCallBack(UnionPayLoansBaseCallBackDTO unionPayLoansBaseCallBackDTO) {

        Long id = null;
        //1二级进件回调
        if(Objects.equals("mch_application_finished", unionPayLoansBaseCallBackDTO.getEventType())){
            TwoIncomingEventDataDTO twoIncomingEventDataDTO  = JSON.parseObject(JSONObject.toJSONString(unionPayLoansBaseCallBackDTO.getEventData()), TwoIncomingEventDataDTO.class);
            id = twoIncomingCallBack(twoIncomingEventDataDTO);
        }
        //2打款验证
        if(Objects.equals("settle_acct_pay_amount_validation", unionPayLoansBaseCallBackDTO.getEventType())){
            SettleAcctsEventDataDTO eventDataDTO  = JSON.parseObject(JSONObject.toJSONString(unionPayLoansBaseCallBackDTO.getEventData()), SettleAcctsEventDataDTO.class);
            id = settleAcctsValidateCallBack(eventDataDTO.getAcctValidationParam(), eventDataDTO.getSettleAcctId(), eventDataDTO.getOutRequestNo());
        }
        return id;
    }

    public Long twoIncomingCallBack(TwoIncomingEventDataDTO twoIncomingEventDataDTO ){
        log.info("二级商户进件回调结果通知-回调参数{}", JSONObject.toJSONString(twoIncomingEventDataDTO));
        LoanUserEntity tfLoanUserEntity = verifyIncomingCallBack(twoIncomingEventDataDTO);
        //修改货款商户
        updatTfLoanUserEntity(twoIncomingEventDataDTO, tfLoanUserEntity);

        if(Objects.equals("succeeded",twoIncomingEventDataDTO.getApplicationStatus())){
            //添加电子账单
            addTfLoanBalanceAcct(twoIncomingEventDataDTO.getRelAcctNo(),  twoIncomingEventDataDTO.getBalanceAcctId(), tfLoanUserEntity.getId());
        }

        //同步业务用户表
        saveBusData(tfLoanUserEntity);

        return tfLoanUserEntity.getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void saveBusData(LoanUserEntity tfLoanUserEntity) {
        TfLoanUserEntityDTO tfLoanUserEntityDTO =new TfLoanUserEntityDTO();
        BeanUtil.copyProperties(tfLoanUserEntity, tfLoanUserEntityDTO);
        List<TfLoanUserEntityDTO> tfLoanUserEntityDTOList = new ArrayList<>();
        tfLoanUserEntityDTOList.add(tfLoanUserEntityDTO);
        tfLoanUserRpcService.updateBatch(tfLoanUserEntityDTOList);
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

        LoanBalanceAcctEntity one = tfLoanBalanceAcctService.getOne(new LambdaQueryWrapper<LoanBalanceAcctEntity>().eq(LoanBalanceAcctEntity::getLoanUserId, loanUserId));
        if(one == null){
            LoanBalanceAcctEntity tfLoanBalanceAcctEntity = new LoanBalanceAcctEntity();
            tfLoanBalanceAcctEntity.setLoanUserId(Integer.valueOf(String.valueOf(loanUserId)));
            if (StringUtils.isNotBlank(relAcctNo)) {
                tfLoanBalanceAcctEntity.setRelAcctNo(relAcctNo);
            }
            if (StringUtils.isNotBlank(balanceAcctId)) {
                tfLoanBalanceAcctEntity.setBalanceAcctId(balanceAcctId);
            }
            tfLoanBalanceAcctService.save(tfLoanBalanceAcctEntity);
        }else {
            if (StringUtils.isNotBlank(relAcctNo)) {
                one.setRelAcctNo(relAcctNo);
            }
            if (StringUtils.isNotBlank(balanceAcctId)) {
                one.setBalanceAcctId(balanceAcctId);
            }
            tfLoanBalanceAcctService.updateById(one);
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
            tfLoanUserEntity.setBankCallStatus(1);
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

