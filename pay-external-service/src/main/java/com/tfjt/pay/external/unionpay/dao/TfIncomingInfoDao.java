package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.api.dto.req.BusinessBasicInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryTtqfSignMsgRespDTO;
import com.tfjt.pay.external.unionpay.dto.BusinessIsIncomingRespDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingDataIdDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.dto.TtqfSignMsgDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 入网信息 Mapper 接口
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
public interface TfIncomingInfoDao extends BaseMapper<TfIncomingInfoEntity> {

    IncomingSubmitMessageDTO queryIncomingMessage(@Param("id") Long id);

    IncomingMessageRespDTO queryIncomingMessageByMerchant(@Param("param") IncomingMessageReqDTO incomingMessageReqDTO);

    List<IncomingMessageRespDTO> queryIncomingMessagesByMerchantList(@Param("params") List<IncomingMessageReqDTO> incomingMessageReqs);

    IncomingDataIdDTO queryIncomingDataId(@Param("id") Long id);

    List<BusinessIsIncomingRespDTO> isIncomingByBusinessIdAndType(@Param(("params")) List<BusinessBasicInfoReqDTO> dtos);

    IncomingMessageRespDTO queryIncomingMessageRespById(@Param("id") Long id);

    QueryTtqfSignMsgRespDTO queryTtqfSignMsg(@Param("businessId") Long businessId,@Param("businessType")Integer businessType);

    List<TtqfSignMsgDTO> querySignMsgStartByIncomingId(@Param("id") Long id);

    List<TtqfSignMsgDTO> querySignMsgByIdCardAndBankCard(@Param("idCardNo") String idCardNo, @Param("bankCardNo") String bankCardNo);

    List<QueryTtqfSignMsgRespDTO> queryTtqfSignMsgByIdCardNo(@Param("idCardNo") String idCardNo,@Param("businessType") Integer businessType);
}
