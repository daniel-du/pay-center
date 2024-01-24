package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingModuleStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingDataIdDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 入网信息 服务类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
public interface TfIncomingInfoService extends IService<TfIncomingInfoEntity> {

    TfIncomingInfoEntity queryIncomingInfoById(Long id);

    /**
     * 根据id查询进件提交所需信息
     * @param id
     * @return
     */
    IncomingSubmitMessageDTO queryIncomingMessage(Long id);

    /**
     * 根据商户信息查询进件完成信息
     * @param incomingMessageReqDTO
     * @return
     */
    IncomingMessageRespDTO queryIncomingMessageByMerchant(IncomingMessageReqDTO incomingMessageReqDTO);

    /**
     * 根据多个商户信息批量查询进件完成信息
     * @param incomingMessageReqs
     * @return
     */
    List<IncomingMessageRespDTO> queryIncomingMessagesByMerchantList(List<IncomingMessageReqDTO> incomingMessageReqs);

    /**
     * 根据进件id查询进件相关信息表id
     * @param id
     * @return
     */
    IncomingDataIdDTO queryIncomingDataId(Long id);

    /**
     * 根据商户类型、商户id、进件渠道查询进件主表信息
     * @param incomingModuleStatusReqDTO
     * @return
     */
    TfIncomingInfoEntity queryIncomingInfoByMerchant(IncomingModuleStatusReqDTO incomingModuleStatusReqDTO);

    /**
     * 根据商户类型、商户id、进件渠道查询进件主表是否存在
     * @param businessId
     * @param businessType
     * @param accessChannelType
     * @return
     */
    Long queryIncomingInfoCountByMerchant(Long businessId, Byte businessType, Byte accessChannelType);

    /**
     * 查询已导入状态最小数据
     * @return
     */
    TfIncomingInfoEntity queryNotSubmitMinIdData();

    /**
     * 根据起始id查询100条已导入状态进件数据
     * @param id
     * @return
     */
    List<TfIncomingInfoEntity> queryListByStartId(Long id);

    /**
     * 更新时间字段
     * @param id
     * @return
     */
    int updateTimeById(Long id);

    /**
     * 根据商户id、商户类型批量查询进件信息
     * @param ids
     * @param businessType
     * @return
     */
    List<TfIncomingInfoEntity> queryListByBusinessIdAndType(List<Long> ids, Integer businessType);

}
