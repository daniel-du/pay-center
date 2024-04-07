package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.req.DigitalSelectReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.DigitalRespDTO;
import com.tfjt.pay.external.unionpay.entity.DigitalUserEntity;
import com.tfjt.tfcommon.dto.response.Result;

/**
 * @author songx
 * @Date: 2023/11/28/17:53
 * @Description: 数字人民币用户biz层
 */
public interface DigitalUserBizService {
    /**
     * 查询银联指定的账号/手机号是否存在
     * @param digitalSelectReqDTO 银联查询参数
     * @return  查询结果
     */
    Result<DigitalRespDTO> selectByAccount(DigitalSelectReqDTO digitalSelectReqDTO);

    /**
     * 保存钱包信息
     * @param digitalUserEntity 接受的钱包信息
     * @return 响应结果
     */
    Result<DigitalRespDTO> bindWallet(DigitalUserEntity digitalUserEntity);

    /**
     * 解绑钱包
     * @param digitalUserEntity 解绑信息
     * @return 响应结果
     */
    Result<DigitalRespDTO> unbindWallet(DigitalUserEntity digitalUserEntity);
}
