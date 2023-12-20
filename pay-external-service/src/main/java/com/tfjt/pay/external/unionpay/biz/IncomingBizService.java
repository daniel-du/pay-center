package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.req.IncomingCheckCodeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSubmitMessageReqDTO;
import com.tfjt.tfcommon.dto.response.Result;

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
    Result submitMessage(IncomingSubmitMessageReqDTO incomingSubmitMessageReqDTO);

    /**
     * 保存结算信息并回填校验验证码、打款金额
     * @return
     */
    Result checkCode(IncomingCheckCodeReqDTO inComingCheckCodeReqDTO);
}
