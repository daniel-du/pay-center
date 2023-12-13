package com.tfjt.pay.external.unionpay.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.biz.IncomingSettleBizService;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSettleReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingSettleRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfBankCardInfoEntity;
import com.tfjt.pay.external.unionpay.entity.TfIncomingSettleInfoEntity;
import com.tfjt.pay.external.unionpay.enums.ExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.TfBankCardInfoService;
import com.tfjt.pay.external.unionpay.service.TfIncomingSettleInfoService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.util.BeanUtils;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.core.validator.group.UpdateGroup;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/12 15:27
 * @description
 */
@Slf4j
@Service
public class IncomingSettleBizServiceImpl implements IncomingSettleBizService {

    @Autowired
    private TfIncomingSettleInfoService tfIncomingSettleInfoService;

    @Autowired
    private TfBankCardInfoService tfBankCardInfoService;

    @Override
    public Result<IncomingSettleRespDTO> getById(Long id) {
        log.info("IncomingSettleBizServiceImpl---getById, id:{}", id);
        try {
            IncomingSettleRespDTO incomingSettleRespDTO = tfIncomingSettleInfoService.querySettleById(id);
            log.info("IncomingSettleBizServiceImpl---getById, incomingSettleRespDTO:{}", JSONObject.toJSONString(incomingSettleRespDTO));
            return Result.ok(incomingSettleRespDTO);
        } catch (TfException e) {
            log.error("平安进件-查询商户结算信息 发生 RenException:", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("平安进件-查询商户结算信息 发生 Exception:", e);
            return Result.failed(ExceptionCodeEnum.FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = {TfException.class, Exception.class})
    public Result save(IncomingSettleReqDTO incomingSettleReqDTO) {
        log.info("IncomingSettleBizServiceImpl---save, incomingSettleReqDTO:{}", JSONObject.toJSONString(incomingSettleReqDTO));
        ValidatorUtils.validateEntity(incomingSettleReqDTO, AddGroup.class);
        TfBankCardInfoEntity tfBankCardInfoEntity = new TfBankCardInfoEntity();
        BeanUtils.copyProperties(incomingSettleReqDTO, tfBankCardInfoEntity);
        if (!tfBankCardInfoService.save(tfBankCardInfoEntity)) {
            log.error("保存结算银行卡信息失败:{}", JSONObject.toJSONString(tfBankCardInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        TfIncomingSettleInfoEntity tfIncomingSettleInfoEntity = TfIncomingSettleInfoEntity.builder().
                incomingId(incomingSettleReqDTO.getIncomingId()).
                settlementAccountType(incomingSettleReqDTO.getSettlementAccountType()).
                bankCardId(tfBankCardInfoEntity.getId()).
                occupation(incomingSettleReqDTO.getOccupation()).build();
        if (!tfIncomingSettleInfoService.save(tfIncomingSettleInfoEntity)) {
            log.error("保存结算信息失败:{}", JSONObject.toJSONString(tfIncomingSettleInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = {TfException.class, Exception.class})
    public Result update(IncomingSettleReqDTO incomingSettleReqDTO) {
        log.info("IncomingSettleBizServiceImpl---save, incomingSettleReqDTO:{}", JSONObject.toJSONString(incomingSettleReqDTO));
        ValidatorUtils.validateEntity(incomingSettleReqDTO, UpdateGroup.class);
        TfBankCardInfoEntity tfBankCardInfoEntity = new TfBankCardInfoEntity();
        BeanUtils.copyProperties(incomingSettleReqDTO, tfBankCardInfoEntity);
        tfBankCardInfoEntity.setId(incomingSettleReqDTO.getBankCardId());
        if (!tfBankCardInfoService.updateById(tfBankCardInfoEntity)) {
            log.error("修改结算银行卡信息失败:{}", JSONObject.toJSONString(tfBankCardInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        TfIncomingSettleInfoEntity tfIncomingSettleInfoEntity = TfIncomingSettleInfoEntity.builder().
                id(incomingSettleReqDTO.getId()).
                settlementAccountType(incomingSettleReqDTO.getSettlementAccountType()).
                occupation(incomingSettleReqDTO.getOccupation()).build();
        if (!tfIncomingSettleInfoService.updateById(tfIncomingSettleInfoEntity)) {
            log.error("修改结算信息失败:{}", JSONObject.toJSONString(tfIncomingSettleInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        return Result.ok();
    }
}
