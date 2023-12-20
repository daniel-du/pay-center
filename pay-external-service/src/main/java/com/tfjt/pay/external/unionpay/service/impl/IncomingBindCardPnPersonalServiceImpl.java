package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dto.req.IncomingCheckCodeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSubmitMessageReqDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.tfjt.pay.external.unionpay.service.IncomingBindCardService;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 21:00
 * @description 平安普通进件对私结算服务
 */
@Service("pingan_common_personal")
public class IncomingBindCardPnPersonalServiceImpl implements IncomingBindCardService {

    @Autowired
    TfIncomingInfoService tfIncomingInfoService;

    @Override
    public boolean binkCard(IncomingSubmitMessageReqDTO incomingSubmitMessageReqDTO) {
        //判断开户状态
        TfIncomingInfoEntity tfIncomingInfoEntity = tfIncomingInfoService.queryIncomingInfoById(incomingSubmitMessageReqDTO.getIncomingId());
        if (NumberConstant.ONE.equals(tfIncomingInfoEntity.getAccessStatus())) {
            //调用平安6248-开户接口
        }
        //调用平安6238接口
        return false;
    }

    @Override
    public boolean checkCode(IncomingCheckCodeReqDTO inComingCheckCodeReqDTO) {
        //调用平安6239接口
        return false;
    }
}
