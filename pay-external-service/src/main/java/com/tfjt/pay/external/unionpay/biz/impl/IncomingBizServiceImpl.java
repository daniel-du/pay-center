package com.tfjt.pay.external.unionpay.biz.impl;

import com.tfjt.pay.external.unionpay.biz.IncomingBizService;
import com.tfjt.pay.external.unionpay.dto.req.InComingBinkCardReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.InComingCheckCodeReqDTO;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessChannelTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingSettleTypeEnum;
import com.tfjt.pay.external.unionpay.service.IncomingBindCardService;
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

    @Override
    public boolean binkCard(InComingBinkCardReqDTO inComingBinkCardReqDTO) {
        //根据参数类型获取实现类 pingan_common_corporate
        String bindServiceName = IncomingAccessChannelTypeEnum.getNameFromCode(inComingBinkCardReqDTO.getAccessChannelType()) +
                "_" + IncomingAccessTypeEnum.getNameFromCode(inComingBinkCardReqDTO.getAccessType()) +
                "_" + IncomingSettleTypeEnum.getNameFromCode(inComingBinkCardReqDTO.getSettelAccountType());
        IncomingBindCardService incomingBindCardService = incomingBindCardServiceMap.get(bindServiceName);
        //调用实现类方法
        incomingBindCardService.binkCard(inComingBinkCardReqDTO);
        //保存结算信息

        return false;
    }

    @Override
    public boolean saveIncomingSettle(InComingCheckCodeReqDTO inComingCheckCodeReqDTO) {
        //根据参数类型获取实现类 pingan_common_corporate
        String bindServiceName = IncomingAccessChannelTypeEnum.getNameFromCode(inComingCheckCodeReqDTO.getAccessChannelType()) +
                "_" + IncomingAccessTypeEnum.getNameFromCode(inComingCheckCodeReqDTO.getAccessType()) +
                "_" + IncomingSettleTypeEnum.getNameFromCode(inComingCheckCodeReqDTO.getSettelAccountType());
        IncomingBindCardService incomingBindCardService = incomingBindCardServiceMap.get(bindServiceName);
        //调用实现类方法
        incomingBindCardService.checkCode(inComingCheckCodeReqDTO);
        return false;
    }
}
