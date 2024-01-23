package com.tfjt.pay.external.unionpay.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.biz.IncomingBusinessBizService;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.RegularConstants;
import com.tfjt.pay.external.unionpay.dto.req.IncomingBusinessReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingBusinessRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfBusinessLicenseInfoEntity;
import com.tfjt.pay.external.unionpay.entity.TfIncomingBusinessInfoEntity;
import com.tfjt.pay.external.unionpay.enums.ExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.enums.IdTypeEnum;
import com.tfjt.pay.external.unionpay.service.TfBusinessLicenseInfoService;
import com.tfjt.pay.external.unionpay.service.TfIncomingBusinessInfoService;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.util.BeanUtils;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.core.validator.group.UpdateGroup;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/12 9:14
 * @description 进件-商户营业信息服务
 */
@Slf4j
@Service
public class IncomingBusinessBizServiceImpl implements IncomingBusinessBizService {

    @Autowired
    private TfIncomingBusinessInfoService tfIncomingBusinessInfoService;

    @Autowired
    private TfBusinessLicenseInfoService tfBusinessLicenseInfoService;

    @Autowired
    private TfIncomingInfoService tfIncomingInfoService;

    /**
     * 邮箱验证格式
     */
    private final Pattern EMAIL_REGEXP = Pattern.compile(RegularConstants.EMAIL);

    @Override
    public Result<IncomingBusinessRespDTO> getById(Long id) {
        log.info("IncomingBusinessBizServiceImpl---getById, id:{}", id);
        IncomingBusinessRespDTO incomingBusinessRespDTO = tfIncomingBusinessInfoService.queryBusinessById(id);
        if (ObjectUtils.isEmpty(incomingBusinessRespDTO)) {
            return Result.ok();
        }
        if (NumberConstant.ONE.equals(incomingBusinessRespDTO.getBusinessLicenseIsLongTerm())) {
            incomingBusinessRespDTO.setBusinessLicenseExpireDate("");
        }
        log.info("IncomingBusinessBizServiceImpl---getById, incomingBusinessRespDTO:{}", JSONObject.toJSONString(incomingBusinessRespDTO));
        return Result.ok(incomingBusinessRespDTO);
    }

    @Override
    @Transactional(rollbackFor = {TfException.class, Exception.class})
    public Result save(IncomingBusinessReqDTO incomingBusinessReqDTO) {
        log.info("IncomingBusinessBizServiceImpl---save, incomingBusinessReqDTO:{}", JSONObject.toJSONString(incomingBusinessReqDTO));
        ValidatorUtils.validateEntity(incomingBusinessReqDTO, AddGroup.class);
        validateBusinessEntity(incomingBusinessReqDTO);
        tfIncomingInfoService.updateTimeById(incomingBusinessReqDTO.getIncomingId());
        TfBusinessLicenseInfoEntity tfBusinessLicenseInfoEntity = new TfBusinessLicenseInfoEntity();
        BeanUtils.copyProperties(incomingBusinessReqDTO, tfBusinessLicenseInfoEntity);
        tfBusinessLicenseInfoEntity.setBusinessLicenseType(IdTypeEnum.SOCIAL_CREDIT_CODE.getCode());
        //保存营业执照信息表
        if (!tfBusinessLicenseInfoService.save(tfBusinessLicenseInfoEntity)) {
            log.error("保存营业执照信息失败:{}", JSONObject.toJSONString(tfBusinessLicenseInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        TfIncomingBusinessInfoEntity tfIncomingBusinessInfoEntity = TfIncomingBusinessInfoEntity.builder().
                incomingId(incomingBusinessReqDTO.getIncomingId()).
                businessLicenseId(tfBusinessLicenseInfoEntity.getId()).
                email(incomingBusinessReqDTO.getEmail()).build();
        //保存营业信息表
        if (!tfIncomingBusinessInfoService.save(tfIncomingBusinessInfoEntity)) {
            log.error("保存营业信息失败:{}", JSONObject.toJSONString(tfIncomingBusinessInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        return Result.ok();
    }

    /**
     * 修改商户营业信息
     * @param incomingBusinessReqDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = {TfException.class, Exception.class})
    public Result update(IncomingBusinessReqDTO incomingBusinessReqDTO) {
        log.info("IncomingBusinessBizServiceImpl---update, incomingBusinessReqDTO:{}", JSONObject.toJSONString(incomingBusinessReqDTO));
        ValidatorUtils.validateEntity(incomingBusinessReqDTO, UpdateGroup.class);
        validateBusinessEntity(incomingBusinessReqDTO);
        if (incomingBusinessReqDTO.getId() == null || incomingBusinessReqDTO.getBusinessLicenseId() == null) {
            throw new TfException(ExceptionCodeEnum.INCOMING_BUSINESS_ID_IS_NULL);
        }
        tfIncomingInfoService.updateTimeById(incomingBusinessReqDTO.getIncomingId());
        TfBusinessLicenseInfoEntity tfBusinessLicenseInfoEntity = new TfBusinessLicenseInfoEntity();
        BeanUtils.copyProperties(incomingBusinessReqDTO, tfBusinessLicenseInfoEntity);
        tfBusinessLicenseInfoEntity.setId(incomingBusinessReqDTO.getBusinessLicenseId());
        //保存营业执照信息表
        if (!tfBusinessLicenseInfoService.updateById(tfBusinessLicenseInfoEntity)) {
            log.error("修改营业执照信息失败:{}", JSONObject.toJSONString(tfBusinessLicenseInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        TfIncomingBusinessInfoEntity tfIncomingBusinessInfoEntity = TfIncomingBusinessInfoEntity.builder().
                id(incomingBusinessReqDTO.getId()).
                email(incomingBusinessReqDTO.getEmail()).build();
        //保存营业信息表
        if (!tfIncomingBusinessInfoService.updateById(tfIncomingBusinessInfoEntity)) {
            log.error("修改营业信息失败:{}", JSONObject.toJSONString(tfIncomingBusinessInfoEntity));
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        return Result.ok();
    }

    /**
     * 保存营业信息时校验字段格式及是否重复
     * @param incomingBusinessReqDTO
     */
    private void validateBusinessEntity(IncomingBusinessReqDTO incomingBusinessReqDTO) {
        if (StringUtils.isNotBlank(incomingBusinessReqDTO.getEmail()) && !EMAIL_REGEXP.matcher(incomingBusinessReqDTO.getEmail()).matches()) {
            throw new TfException(ExceptionCodeEnum.INCOMING_EMAIL_FORMAT_ERROR);
        }
        if (StringUtils.isBlank(incomingBusinessReqDTO.getBusinessLicenseExpireDate()) && NumberConstant.ZERO.equals(incomingBusinessReqDTO.getBusinessLicenseIsLongTerm())) {
            throw new TfException(ExceptionCodeEnum.INCOMING_EMAIL_FORMAT_ERROR);
        }
        //校验当前营业执照号码是否已存在
        if (tfBusinessLicenseInfoService.queryCountByLicenseNo(incomingBusinessReqDTO) > 0) {
            throw new TfException(ExceptionCodeEnum.INCOMING_BUSINESS_LICENSE_NO_REPEAT);
        }
    }
}
