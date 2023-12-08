package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.unionpay.dto.req.InComingBinkCardReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.InComingCheckCodeReqDTO;

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
    boolean binkCard(InComingBinkCardReqDTO inComingBinkCardReqDTO);

    /**
     * 回填校验验证码、打款金额
     * @return
     */
    boolean checkCode(InComingCheckCodeReqDTO inComingCheckCodeReqDTO);
}
