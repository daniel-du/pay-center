package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.dao.TfIncomingInfoDao;
import com.tfjt.pay.external.unionpay.dto.IncomingDataIdDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 入网信息 服务实现类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
@Service
public class TfIncomingInfoServiceImpl extends BaseServiceImpl<TfIncomingInfoDao, TfIncomingInfoEntity> implements TfIncomingInfoService {


    @Override
    public TfIncomingInfoEntity queryIncomingInfoById(Long id) {
        return null;
    }

    /**
     * 根据id查询进件提交所需信息
     * @param id
     * @return IncomingSubmitMessageDTO
     */
    @Override
    public IncomingSubmitMessageDTO queryIncomingMessage(Long id) {
        return this.baseMapper.queryIncomingMessage(id);
    }

    /**
     * 根据商户信息查询进件信息
     * @param incomingMessageReqDTO
     * @return
     */
    @Override
    public IncomingMessageRespDTO queryIncomingMessageByMerchant(IncomingMessageReqDTO incomingMessageReqDTO) {
        return this.baseMapper.queryIncomingMessageByMerchant(incomingMessageReqDTO);
    }

    /**
     * 根据多个商户信息批量查询进件信息
     * @param incomingMessageReqs
     * @return
     */
    @Override
    public List<IncomingMessageRespDTO> queryIncomingMessagesByMerchantList(List<IncomingMessageReqDTO> incomingMessageReqs) {
        return this.baseMapper.queryIncomingMessagesByMerchantList(incomingMessageReqs);
    }

    /**
     * 根据进件id查询进件相关信息表id
     * @param id
     * @return
     */
    @Override
    public IncomingDataIdDTO queryIncomingDataId(Long id) {
        return this.baseMapper.queryIncomingDataId(id);
    }
}
