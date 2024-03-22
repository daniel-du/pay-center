package com.tfjt.pay.external.unionpay.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.ipaynow.jiaxin.domain.QueryPresignResultModel;
import com.tfjt.pay.external.unionpay.api.dto.req.QueryTtqfSignMsgReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.TtqfContractReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryTtqfSignMsgRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.TtqfContractRespDTO;
import com.tfjt.pay.external.unionpay.biz.IncomingTtqfBizService;
import com.tfjt.pay.external.unionpay.dto.TtqfSignMsgDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingExtendInfoEntity;
import com.tfjt.pay.external.unionpay.service.TfIncomingExtendInfoService;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.tfjt.pay.external.unionpay.utils.TtqfApiUtil;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/21 17:14
 * @description
 */
@Slf4j
@Service
public class IncomingTtqfBizServiceImpl implements IncomingTtqfBizService {

    @Autowired
    private TfIncomingInfoService incomingInfoService;

    @Autowired
    private TfIncomingExtendInfoService incomingExtendInfoService;

    @Override
    public Result<TtqfContractRespDTO> ttqfContract(TtqfContractReqDTO ttqfContractReqDTO) {
        String signUrl = TtqfApiUtil.contractH5(ttqfContractReqDTO.getIdCardNo(), ttqfContractReqDTO.getMchReturnUrl());
        TtqfContractRespDTO ttqfContractRespDTO = TtqfContractRespDTO.builder().signUrl(signUrl).build();
        return Result.ok(ttqfContractRespDTO);
    }

    @Override
    public Result<QueryTtqfSignMsgRespDTO> queryTtqfSignMsg(QueryTtqfSignMsgReqDTO queryTtqfSignMsgReqDTO) {
        log.info("IncomingTtqfBizServiceImpl--queryTtqfSignMsg, req:{}", JSONObject.toJSONString(queryTtqfSignMsgReqDTO));
        return Result.ok(incomingInfoService.queryTtqfSignMsg(queryTtqfSignMsgReqDTO.getBusinessId()));
    }

    /**
     * 批量更新天天企赋签约状态
     */
    @Override
    public void updateTtqfSignStatus() {
        TfIncomingExtendInfoEntity extendInfo = incomingExtendInfoService.queryNotSignMinIdData();
        log.info("IncomingTtqfBizServiceImpl--updateTtqfSignStatus, extendInfo:{}", JSONObject.toJSONString(extendInfo));
        if (ObjectUtils.isEmpty(extendInfo)) {
            return;
        }
        boolean updateFlag = true;
        long startId = extendInfo.getIncomingId();
        while(updateFlag) {
            List<TtqfSignMsgDTO> signMsgList = incomingInfoService.querySignMsgStartByIncomingId(startId);
            if (CollectionUtils.isEmpty(signMsgList)) {
                break;
            }
            queryAndUpdatePresignStatus(signMsgList);

        }
    }

    /**
     * 根据未完全完成状态数据查询天天企赋api，并更新数据
     * @param signMsgList
     */
    private void queryAndUpdatePresignStatus(List<TtqfSignMsgDTO> signMsgList) {
        signMsgList.forEach(signMsg -> {
            try {
                QueryPresignResultModel presignResult = TtqfApiUtil.queryPresign(signMsg.getIdCardNo());
                if (ObjectUtils.isEmpty(presignResult)) {
                    return;
                }
                TfIncomingExtendInfoEntity extendInfoEntity = new TfIncomingExtendInfoEntity();
                extendInfoEntity.setIncomingId(signMsg.getId());
                extendInfoEntity.setSignStatus(presignResult.getSignStatus().byteValue());
                extendInfoEntity.setAuthStatus(presignResult.getAuthStatus().byteValue());
                if (CollectionUtils.isEmpty(presignResult.getCards())) {
                    incomingExtendInfoService.updateByIncomingId(extendInfoEntity);
                    return;
                }
                presignResult.getCards().forEach(card -> {
                    if (card.getBankCardNo().equals(signMsg.getBankCardNo())) {
                        extendInfoEntity.setBindStatus(card.getBindStatus().byteValue());
                    }
                });
                incomingExtendInfoService.updateByIncomingId(extendInfoEntity);
            } catch (Exception e) {
                log.error("IncomingTtqfBizServiceImpl--queryAndUpdatePresignStatus, error", e);
            }
        });
    }


}
