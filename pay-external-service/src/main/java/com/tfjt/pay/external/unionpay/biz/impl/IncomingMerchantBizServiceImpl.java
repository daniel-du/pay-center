package com.tfjt.pay.external.unionpay.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.biz.IncomingMerchantBizService;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dto.req.IncomingMerchantReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingMerchantRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIdcardInfoEntity;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.tfjt.pay.external.unionpay.entity.TfIncomingMerchantInfoEntity;
import com.tfjt.pay.external.unionpay.service.TfIdcardInfoService;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.tfjt.pay.external.unionpay.service.TfIncomingMerchantInfoService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.util.BeanUtils;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/11 16:11
 * @description 进件-商户身份信息服务
 */
@Slf4j
@Service
public class IncomingMerchantBizServiceImpl implements IncomingMerchantBizService {

    /**
     * 入网信息服务
     */
    @Autowired
    private TfIncomingInfoService tfIncomingInfoService;

    /**
     * 入网商户身份信息服务
     */
    @Autowired
    private TfIncomingMerchantInfoService tfIncomingMerchantInfoService;

    /**
     * 证件信息服务
     */
    @Autowired
    private TfIdcardInfoService tfIdcardInfoService;



    /**
     * 根据id查询商户身份信息
     * @param id
     * @return
     */
    @Override
    public Result<IncomingMerchantRespDTO> getById(Long id) {
        try {
            IncomingMerchantRespDTO incomingMerchantRespDTO = tfIncomingMerchantInfoService.queryMerchantById(id);
            return Result.ok(incomingMerchantRespDTO);
        } catch (TfException e) {
            log.error("平安进件-查询商户身份信息 发生 RenException:", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("平安进件-查询商户身份信息 发生 Exception:", e);
            return Result.failed(ExceptionCodeEnum.FAIL);
        }
    }

    /**
     * 保存商户身份信息
     * @param incomingMerchantReqDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = {TfException.class, Exception.class})
    public Result save(IncomingMerchantReqDTO incomingMerchantReqDTO) {
        try {
            //保存进件主表信息
            TfIncomingInfoEntity tfIncomingInfoEntity = new TfIncomingInfoEntity();
            BeanUtils.copyProperties(incomingMerchantReqDTO, tfIncomingInfoEntity);
            if (!tfIncomingInfoService.save(tfIncomingInfoEntity)) {
                log.error("保存进件主表信息失败:{}", JSONObject.toJSONString(tfIncomingInfoEntity));
                throw new TfException(ExceptionCodeEnum.FAIL);
            }
            //保存商户身份信息
            TfIncomingMerchantInfoEntity tfIncomingMerchantInfoEntity = new TfIncomingMerchantInfoEntity();
            tfIncomingMerchantInfoEntity.setIncomingId(tfIncomingInfoEntity.getId());
            //保存商户身份-法人信息
            TfIdcardInfoEntity legalIdcardInfoEntity = saveLegal(incomingMerchantReqDTO);
            tfIncomingMerchantInfoEntity.setLegalIdCard(legalIdcardInfoEntity.getId());
            //判断经办人信息是否同法人，如不同则单独处理
            if (NumberConstant.ZERO.equals(incomingMerchantReqDTO.getAgentIsLegal())) {
                //保存商户身份-经办人信息
                TfIdcardInfoEntity agentIdcardInfoEntity = saveAgent(incomingMerchantReqDTO);
                tfIncomingMerchantInfoEntity.setAgentIdCard(agentIdcardInfoEntity.getId());
            } else {
                tfIncomingMerchantInfoEntity.setAgentIdCard(legalIdcardInfoEntity.getId());
            }
            if (!tfIncomingMerchantInfoService.save(tfIncomingMerchantInfoEntity)) {
                log.error("保存商户身份信息失败:{}", JSONObject.toJSONString(tfIncomingMerchantInfoEntity));
                throw new TfException(ExceptionCodeEnum.FAIL);
            }
            return Result.ok();
        } catch (TfException e) {
            log.error("平安进件-保存商户身份信息 发生 RenException:", e);
            throw new TfException(ExceptionCodeEnum.FAIL);
        } catch (Exception e) {
            log.error("平安进件-保存商户身份信息 发生 Exception:", e);
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
    }

    /**
     * 修改商户身份信息
     * @param incomingMerchantReqDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = {TfException.class, Exception.class})
    public Result update(IncomingMerchantReqDTO incomingMerchantReqDTO) {
        TfIncomingMerchantInfoEntity originMerchantInfoEntity = tfIncomingMerchantInfoService.getById(incomingMerchantReqDTO.getId());
        //保存商户身份信息
        TfIncomingMerchantInfoEntity tfIncomingMerchantInfoEntity = TfIncomingMerchantInfoEntity.builder().
                id(incomingMerchantReqDTO.getId()).shopShortName(incomingMerchantReqDTO.getShopShortName()).
                agentIsLegal(incomingMerchantReqDTO.getAgentIsLegal()).
                legalMobile(incomingMerchantReqDTO.getLegalMobile()).
                agentMobile(incomingMerchantReqDTO.getAgentMobile()).build();
        if (!tfIncomingMerchantInfoService.updateById(tfIncomingMerchantInfoEntity)) {
            log.error("修改商户身份信息失败:{}", JSONObject.toJSONString(tfIncomingMerchantInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        //保存商户身份-法人信息
        TfIdcardInfoEntity legalIdcardInfoEntity = saveLegal(incomingMerchantReqDTO);
        //原经办人与变更后经办人都同法人
        if (NumberConstant.ONE.equals(originMerchantInfoEntity.getAgentIsLegal()) && NumberConstant.ONE.equals(incomingMerchantReqDTO.getAgentIsLegal())) {
            return Result.ok();
        }
        //原经办人与变更后经办人都不同法人
        if (NumberConstant.ZERO.equals(originMerchantInfoEntity.getAgentIsLegal()) && NumberConstant.ZERO.equals(incomingMerchantReqDTO.getAgentIsLegal())) {
            TfIdcardInfoEntity agentIdcardInfoEntity = updateAgent(incomingMerchantReqDTO);
            return Result.ok();
        }
        //原经办人同法人，变更后经办人不同法人
        if (NumberConstant.ONE.equals(originMerchantInfoEntity.getAgentIsLegal()) && NumberConstant.ZERO.equals(incomingMerchantReqDTO.getAgentIsLegal())) {
            TfIdcardInfoEntity agentIdcardInfoEntity = saveAgent(incomingMerchantReqDTO);
            return Result.ok();
        }
        return Result.ok();
    }

    /**
     * 保存法人身份信息
     * @param incomingMerchantReqDTO
     * @return
     */
    private TfIdcardInfoEntity saveLegal(IncomingMerchantReqDTO incomingMerchantReqDTO) {
        TfIdcardInfoEntity legalIdcardInfoEntity = TfIdcardInfoEntity.builder().id(incomingMerchantReqDTO.getLegalIdCard()).
                idNo(incomingMerchantReqDTO.getAgentIdNo()).name(incomingMerchantReqDTO.getLegalName()).
                sex(incomingMerchantReqDTO.getLegalSex()).nationality(incomingMerchantReqDTO.getLegalNationality()).
                frontIdCardUrl(incomingMerchantReqDTO.getLegalFrontIdCardUrl()).
                backIdCardUrl(incomingMerchantReqDTO.getLegalBackIdCardUrl()).
                holdIdCardUrl(incomingMerchantReqDTO.getLegalHoldIdCardUrl()).
                idEffectiveDate(incomingMerchantReqDTO.getLegalIdEffectiveDate()).
                idExpiryDate(incomingMerchantReqDTO.getLegalIdExpiryDate()).
                isLongTerm(incomingMerchantReqDTO.getLegalIdIsLongTerm()).build();
        if (!tfIdcardInfoService.saveOrUpdate(legalIdcardInfoEntity)) {
            log.error("保存法人身份信息失败:{}", JSONObject.toJSONString(legalIdcardInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        return legalIdcardInfoEntity;
    }

    /**
     * 保存经办人身份信息
     * @param incomingMerchantReqDTO
     * @return
     */
    private TfIdcardInfoEntity saveAgent(IncomingMerchantReqDTO incomingMerchantReqDTO) {
        TfIdcardInfoEntity agentIdcardInfoEntity = TfIdcardInfoEntity.builder().
                idNo(incomingMerchantReqDTO.getAgentIdNo()).name(incomingMerchantReqDTO.getAgentName()).
                idEffectiveDate(incomingMerchantReqDTO.getAgentIdEffectiveDate()).
                idExpiryDate(incomingMerchantReqDTO.getAgentIdExpiryDate()).
                isLongTerm(incomingMerchantReqDTO.getAgentIdIsLongTerm()).build();
        if (!tfIdcardInfoService.save(agentIdcardInfoEntity)) {
            log.error("保存经办人身份信息失败:{}", JSONObject.toJSONString(agentIdcardInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        return agentIdcardInfoEntity;
    }

    /**
     * 修改经办人身份信息
     * @param incomingMerchantReqDTO
     * @return
     */
    private TfIdcardInfoEntity updateAgent(IncomingMerchantReqDTO incomingMerchantReqDTO) {
        TfIdcardInfoEntity agentIdcardInfoEntity = TfIdcardInfoEntity.builder().id(incomingMerchantReqDTO.getAgentIdCard()).
                idNo(incomingMerchantReqDTO.getAgentIdNo()).name(incomingMerchantReqDTO.getAgentName()).
                idEffectiveDate(incomingMerchantReqDTO.getAgentIdEffectiveDate()).
                idExpiryDate(incomingMerchantReqDTO.getAgentIdExpiryDate()).
                isLongTerm(incomingMerchantReqDTO.getAgentIdIsLongTerm()).build();
        if (!tfIdcardInfoService.updateById(agentIdcardInfoEntity)) {
            log.error("修改经办人身份信息失败:{}", JSONObject.toJSONString(agentIdcardInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        return agentIdcardInfoEntity;
    }




}
