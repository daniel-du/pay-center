package com.tfjt.pay.external.unionpay.biz.impl;

import com.tfjt.pay.external.unionpay.biz.IncomingBizService;
import com.tfjt.pay.external.unionpay.dto.req.InComingBinkCardReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.InComingCheckCodeReqDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.tfjt.pay.external.unionpay.entity.TfIncomingSettleInfoEntity;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessChannelTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingSettleTypeEnum;
import com.tfjt.pay.external.unionpay.service.IncomingBindCardService;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.tfjt.pay.external.unionpay.service.TfIncomingSettleInfoService;
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
    public boolean binkCard(InComingBinkCardReqDTO inComingBinkCardReqDTO) {
        //根据参数类型获取实现类 pingan_common_corporate
        String bindServiceName = getServiceName(inComingBinkCardReqDTO.getIncomingId());
        IncomingBindCardService incomingBindCardService = incomingBindCardServiceMap.get(bindServiceName);
        //调用实现类方法
        incomingBindCardService.binkCard(inComingBinkCardReqDTO);
        //更新进件信息

        return false;
    }

    @Override
    public boolean checkCode(InComingCheckCodeReqDTO inComingCheckCodeReqDTO) {
        //根据进件信息类型数据获取对应实现
        String bindServiceName = getServiceName(inComingCheckCodeReqDTO.getIncomingId());
        IncomingBindCardService incomingBindCardService = incomingBindCardServiceMap.get(bindServiceName);
        //调用实现类方法
        incomingBindCardService.checkCode(inComingCheckCodeReqDTO);
        //更新进件信息
        return false;
    }

    /**
     * 根据进行信息获取实现类name
     * @param incomingId
     * @return
     */
    private String getServiceName(Long incomingId) {
        //根据参数查询进件信息
        TfIncomingInfoEntity  tfIncomingInfoEntity =
                tfIncomingInfoService.queryIncomingInfoById(incomingId);
        TfIncomingSettleInfoEntity tfIncomingSettleInfoEntity =
                tfIncomingSettleInfoService.querySettleInfoByIncomingId(incomingId);
        //根据进件信息类型数据获取对应实现
        String bindServiceName = IncomingAccessChannelTypeEnum.getNameFromCode(tfIncomingInfoEntity.getAccessChannelType()) +
                "_" + IncomingAccessTypeEnum.getNameFromCode(tfIncomingInfoEntity.getAccessType()) +
                "_" + IncomingSettleTypeEnum.getNameFromCode(tfIncomingSettleInfoEntity.getSettlementAccountType());

        return bindServiceName;
    }
}
