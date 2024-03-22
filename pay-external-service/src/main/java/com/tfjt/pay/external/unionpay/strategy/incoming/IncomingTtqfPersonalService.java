package com.tfjt.pay.external.unionpay.strategy.incoming;

import com.ipaynow.jiaxin.domain.PresignModel;
import com.ipaynow.jiaxin.domain.PresignResultModel;
import com.tfjt.pay.external.unionpay.dto.CheckCodeMessageDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingSubmitMessageRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingExtendInfoEntity;
import com.tfjt.pay.external.unionpay.enums.ExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.TfIncomingExtendInfoService;
import com.tfjt.pay.external.unionpay.utils.TtqfApiUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/21 8:48
 * @description
 */
@Slf4j
@Service("ttqf_common_personal")
public class IncomingTtqfPersonalService extends AbstractIncomingService {

    @Autowired
    private TtqfApiUtil ttqfApiUtil;

    @Autowired
    private TfIncomingExtendInfoService incomingExtendInfoService;

    DateFormat format1 = new SimpleDateFormat("yyyyMMdd");

    DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public IncomingSubmitMessageRespDTO incomingSubmit(IncomingSubmitMessageDTO incomingSubmitMessageDTO) {
        try {
            Date expiryStart = format1.parse(incomingSubmitMessageDTO.getLegalIdExpiryStart());
            String expiryStartStr = format2.format(expiryStart);
            Date expiryEnd = format1.parse(incomingSubmitMessageDTO.getLegalIdExpiryEnd());
            String expiryEndStr = format2.format(expiryEnd);
            //上传身份证正面照片
            String frontFieldId = TtqfApiUtil.pictureUpload(incomingSubmitMessageDTO.getLegalIdFrontUrl());
            //上传身份证背面照片
            String backFieldId = TtqfApiUtil.pictureUpload(incomingSubmitMessageDTO.getLegalIdBackUrl());
            PresignModel model = PresignModel.builder().name(incomingSubmitMessageDTO.getLegalName())
                    .idCardNo(incomingSubmitMessageDTO.getLegalIdNo())
                    .mobile(incomingSubmitMessageDTO.getLegalMobile())
                    .bankCardNo(incomingSubmitMessageDTO.getBankCardNo())
                    .expiryStart(expiryStartStr)
                    .expiryEnd(expiryEndStr)
                    .idCardPicAFileId(frontFieldId)
                    .idCardPicBFileId(backFieldId).build();
            PresignResultModel resultModel = TtqfApiUtil.presign(model);
            TfIncomingExtendInfoEntity extendInfo = new TfIncomingExtendInfoEntity();
            extendInfo.setIncomingId(incomingSubmitMessageDTO.getIncomingId());
            extendInfo.setAuthStatus(resultModel.getAuthStatus().byteValue());
            extendInfo.setSignStatus(resultModel.getSignStatus().byteValue());
            extendInfo.setBindStatus(resultModel.getBindStatus().byteValue());
            if (!incomingExtendInfoService.save(extendInfo)) {
                log.error("IncomingTtqfPersonalService--saveExtendInfoError");
                throw new TfException(ExceptionCodeEnum.FAIL);
            }
        } catch (ParseException e) {
            log.error("IncomingTtqfPersonalService--ParseException", e);
            throw new TfException(ExceptionCodeEnum.FAIL);
        }

        return null;
    }

    @Override
    public boolean checkCode(CheckCodeMessageDTO checkCodeMessageDTO) {
        return false;
    }

    @Override
    public boolean openAccount(IncomingSubmitMessageDTO incomingSubmitMessageDTO) {
        return false;
    }
}
