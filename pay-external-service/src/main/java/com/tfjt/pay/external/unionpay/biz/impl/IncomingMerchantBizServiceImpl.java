package com.tfjt.pay.external.unionpay.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.biz.IncomingMerchantBizService;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dto.req.IncomingMerchantReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingMerchantRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIdcardInfoEntity;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.tfjt.pay.external.unionpay.entity.TfIncomingMerchantInfoEntity;
import com.tfjt.pay.external.unionpay.enums.ExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.enums.IdTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessStatusEnum;
import com.tfjt.pay.external.unionpay.service.TfIdcardInfoService;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.tfjt.pay.external.unionpay.service.TfIncomingMerchantInfoService;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.util.BeanUtils;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.core.validator.group.UpdateGroup;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RedisCache redisCache;

    private final static Byte AGENT_IS_LEGAL = 1;

    private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd");



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
            log.info("IncomingMerchantBizServiceImpl---save, incomingMerchantReqDTO:{}", JSONObject.toJSONString(incomingMerchantReqDTO));
            ValidatorUtils.validateEntity(incomingMerchantReqDTO, AddGroup.class);
            validateAgentEntity(incomingMerchantReqDTO);
            //保存进件主表信息
            TfIncomingInfoEntity tfIncomingInfoEntity = new TfIncomingInfoEntity();
            BeanUtils.copyProperties(incomingMerchantReqDTO, tfIncomingInfoEntity);
            tfIncomingInfoEntity.setMemberId(generateMemberId());
            tfIncomingInfoEntity.setAccessStatus(IncomingAccessStatusEnum.MESSAGE_FILL_IN.getCode());
            if (!tfIncomingInfoService.save(tfIncomingInfoEntity)) {
                log.error("保存进件主表信息失败:{}", JSONObject.toJSONString(tfIncomingInfoEntity));
                throw new TfException(ExceptionCodeEnum.FAIL);
            }
            //保存商户身份信息
            TfIncomingMerchantInfoEntity tfIncomingMerchantInfoEntity = new TfIncomingMerchantInfoEntity();
            BeanUtils.copyProperties(incomingMerchantReqDTO, tfIncomingMerchantInfoEntity);
            tfIncomingMerchantInfoEntity.setIncomingId(tfIncomingInfoEntity.getId());
            //保存商户身份-法人信息
            TfIdcardInfoEntity legalIdcardInfoEntity = saveLegal(incomingMerchantReqDTO);
            tfIncomingMerchantInfoEntity.setLegalIdCard(legalIdcardInfoEntity.getId());
            //判断经办人信息是否同法人，如不同则单独处理
            if (!AGENT_IS_LEGAL.equals(incomingMerchantReqDTO.getAgentIsLegal())) {
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
            throw new TfException(e.getMessage());
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
        log.info("IncomingMerchantBizServiceImpl---update, incomingMerchantReqDTO:{}", JSONObject.toJSONString(incomingMerchantReqDTO));
        ValidatorUtils.validateEntity(incomingMerchantReqDTO, UpdateGroup.class);
        validateAgentEntity(incomingMerchantReqDTO);
        TfIncomingMerchantInfoEntity originMerchantInfoEntity = tfIncomingMerchantInfoService.getById(incomingMerchantReqDTO.getId());
        //保存商户身份信息
        TfIncomingMerchantInfoEntity tfIncomingMerchantInfoEntity = TfIncomingMerchantInfoEntity.builder().
                id(incomingMerchantReqDTO.getId()).shopShortName(incomingMerchantReqDTO.getShopShortName()).
                agentIsLegal(incomingMerchantReqDTO.getAgentIsLegal()).
                legalMobile(incomingMerchantReqDTO.getLegalMobile()).
                agentMobile(incomingMerchantReqDTO.getLegalMobile()).build();
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
        TfIdcardInfoEntity legalIdcardInfoEntity = TfIdcardInfoEntity.builder().
                id(incomingMerchantReqDTO.getLegalIdCard()).idType(IdTypeEnum.ID_CARD.getCode()).
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
        TfIdcardInfoEntity agentIdcardInfoEntity = TfIdcardInfoEntity.builder().idType(IdTypeEnum.ID_CARD.getCode()).
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

    /**
     * 信息保存时校验经办人参数
     * @param incomingMerchantReqDTO
     */
    private void validateAgentEntity(IncomingMerchantReqDTO incomingMerchantReqDTO) {
        //入网主体非企业时，不做校验
        if (NumberConstant.ONE.equals(incomingMerchantReqDTO.getAccessMainType())) {
            return;
        }
        if (StringUtils.isBlank(incomingMerchantReqDTO.getAgentName())) {
            throw new TfException(ExceptionCodeEnum.INCOMING_AGENT_NAME_IS_NULL);
        }
        if (StringUtils.isBlank(incomingMerchantReqDTO.getAgentMobile())) {
            throw new TfException(ExceptionCodeEnum.INCOMING_AGENT_MOBILE_IS_NULL);
        }
        if (StringUtils.isBlank(incomingMerchantReqDTO.getAgentIdNo())) {
            throw new TfException(ExceptionCodeEnum.INCOMING_AGENT_ID_NO_IS_NULL);
        }
        if (StringUtils.isBlank(incomingMerchantReqDTO.getAgentIdEffectiveDate())) {
            throw new TfException(ExceptionCodeEnum.INCOMING_AGENT_EFFECTIVE_IS_NULL);
        }
        if (StringUtils.isBlank(incomingMerchantReqDTO.getAgentIdExpiryDate())) {
            throw new TfException(ExceptionCodeEnum.INCOMING_AGENT_EXPIRE_IS_NULL);
        }
        if (incomingMerchantReqDTO.getAgentIdIsLongTerm() == null) {
            throw new TfException(ExceptionCodeEnum.INCOMING_AGENT_IS_LONG_TERM_IS_NULL);
        }
    }

    /**
     * 生成同福会员号
     * @return
     */
    private String generateMemberId() {
        String prefix = "TF" + FORMAT.format(new Date());
        Long incr = redisCache.incr(prefix);
        redisCache.expire(prefix, 1, TimeUnit.DAYS);
        String str = String.format("%04d", incr);
        return prefix + str;
    }

}
