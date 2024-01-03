package com.tfjt.pay.external.unionpay.biz.impl;

import com.tfjt.pay.external.unionpay.biz.IncomingBizService;
import com.tfjt.pay.external.unionpay.dto.CheckCodeMessageDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingCheckCodeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSubmitMessageReqDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.tfjt.pay.external.unionpay.entity.TfIncomingSettleInfoEntity;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessChannelTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingSettleTypeEnum;
import com.tfjt.pay.external.unionpay.service.IncomingBindCardService;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.tfjt.pay.external.unionpay.service.TfIncomingSettleInfoService;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 20:07
 * @description
 */
@Slf4j
@Service
public class IncomingBizServiceImpl implements IncomingBizService {

    @Autowired
    private Map<String, IncomingBindCardService> incomingBindCardServiceMap;

    @Autowired
    private TfIncomingInfoService tfIncomingInfoService;

    @Autowired
    private TfIncomingSettleInfoService tfIncomingSettleInfoService;


    @Override
    public Result incomingSubmit(IncomingSubmitMessageReqDTO incomingSubmitMessageReqDTO) {
        //查询提交进件申请所需信息
        IncomingSubmitMessageDTO incomingSubmitMessageDTO =
                tfIncomingInfoService.queryIncomingMessage(incomingSubmitMessageReqDTO.getIncomingId());
        //根据参数类型获取实现类
        String bindServiceName = getServiceName(incomingSubmitMessageDTO);

        IncomingBindCardService incomingBindCardService = incomingBindCardServiceMap.get(bindServiceName);
        //调用实现类方法
        incomingBindCardService.incomingSubmit(incomingSubmitMessageDTO);
        //更新进件信息

        return Result.ok();
    }

    @Override
    public Result checkCode(IncomingCheckCodeReqDTO inComingCheckCodeReqDTO) {
        IncomingSubmitMessageDTO incomingSubmitMessageDTO =
                tfIncomingInfoService.queryIncomingMessage(inComingCheckCodeReqDTO.getIncomingId());
        //根据进件信息类型数据获取对应实现
        String bindServiceName = getServiceName(incomingSubmitMessageDTO);
        IncomingBindCardService incomingBindCardService = incomingBindCardServiceMap.get(bindServiceName);
        //调用实现类方法
        CheckCodeMessageDTO checkCodeMessageDTO = CheckCodeMessageDTO.builder()
                .id(incomingSubmitMessageDTO.getId())
                .memberId(incomingSubmitMessageDTO.getMemberId())
                .accountNo(incomingSubmitMessageDTO.getAccountNo())
                .bankCardNo(incomingSubmitMessageDTO.getBankCardNo())
                .authAmt(inComingCheckCodeReqDTO.getAuthAmt())
                .messageCheckCode(inComingCheckCodeReqDTO.getMessageCheckCode())
                .ipAddress(inComingCheckCodeReqDTO.getIpAddress())
                .macAddress(inComingCheckCodeReqDTO.getMacAddress()).build();
        incomingBindCardService.checkCode(checkCodeMessageDTO);
        //更新进件信息
        return Result.ok();
    }

    /**
     * 根据进行信息获取实现类name
     * @param incomingSubmitMessageDTO
     * @return
     */
    private String getServiceName(IncomingSubmitMessageDTO incomingSubmitMessageDTO) {
        //根据进件信息类型数据获取对应实现
        String bindServiceName = IncomingAccessChannelTypeEnum.getNameFromCode(incomingSubmitMessageDTO.getAccessChannelType()) +
                "_" + IncomingAccessTypeEnum.getNameFromCode(incomingSubmitMessageDTO.getAccessType()) +
                "_" + IncomingSettleTypeEnum.getNameFromCode(incomingSubmitMessageDTO.getSettlementAccountType());
        return bindServiceName;
    }
}
