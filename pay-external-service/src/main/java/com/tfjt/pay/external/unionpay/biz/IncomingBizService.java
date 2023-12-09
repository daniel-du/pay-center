package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.req.InComingBinkCardReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.InComingCheckCodeReqDTO;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 20:04
 * @description 进件服务
 */
public interface IncomingBizService {

    /**
     * 绑定银行卡、获取验证码
     * @return
     */
    boolean binkCard(InComingBinkCardReqDTO inComingBinkCardReqDTO);

    /**
     * 保存结算信息并回填校验验证码、打款金额
     * @return
     */
    boolean checkCode(InComingCheckCodeReqDTO inComingCheckCodeReqDTO);
}
