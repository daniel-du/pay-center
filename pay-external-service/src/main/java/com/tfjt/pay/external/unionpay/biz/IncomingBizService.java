package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingChangeAccessMainTypeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingCheckCodeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSubmitMessageReqDTO;
import com.tfjt.tfcommon.dto.response.Result;

import java.util.List;
import java.util.Map;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 20:04
 * @description 进件服务
 */
public interface IncomingBizService {

    /**
     * 提交基本信息、获取验证码
     * @return
     */
    Result incomingSubmit(IncomingSubmitMessageReqDTO incomingSubmitMessageReqDTO);

    /**
     * 保存结算信息并回填校验验证码、打款金额
     * @return
     */
    Result checkCode(IncomingCheckCodeReqDTO inComingCheckCodeReqDTO);

    /**
     * 根据商户信息查询进件信息
     * @param incomingMessageReqDTO
     * @return
     */
    Result<IncomingMessageRespDTO> queryIncomingMessage(IncomingMessageReqDTO incomingMessageReqDTO);

    /**
     * 根据多个商户信息批量查询进件信息
     * @param incomingMessageReqs
     * @return
     */
    Result<Map<String, IncomingMessageRespDTO>> queryIncomingMessages(List<IncomingMessageReqDTO> incomingMessageReqs);

    /**
     * 变更进件主体类型
     * @param changeAccessMainTypeReqDTO
     * @return
     */
    Result changeAccessMainType(IncomingChangeAccessMainTypeReqDTO changeAccessMainTypeReqDTO);
}
