package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.unionpay.dto.req.IncomingCheckCodeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSubmitMessageReqDTO;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 20:45
 * @description 进件绑卡服务
 */
public interface IncomingBindCardService {

    /**
     * 绑定银行卡、获取验证码
     * @return
     */
    boolean binkCard(IncomingSubmitMessageReqDTO incomingSubmitMessageReqDTO);

    /**
     * 回填校验验证码、打款金额
     * @return
     */
    boolean checkCode(IncomingCheckCodeReqDTO inComingCheckCodeReqDTO);
}
