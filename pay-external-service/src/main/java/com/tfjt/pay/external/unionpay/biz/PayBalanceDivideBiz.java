package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.req.BalanceDivideReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayDivideRespDTO;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceDivideDetailsEntity;

import java.util.List;

/**
 * @author songx
 * @date 2023-08-22 09:09
 * @email 598482054@qq.com
 */
public interface PayBalanceDivideBiz {
    /**
     * 保存分账信息
     *
     * @param tradeOrderNo        银联交易单号
     * @param saveList            分支详情
     * @param balanceDivideReqDTO 分账请求
     */
    void saveDivide(String tradeOrderNo, List<LoanBalanceDivideDetailsEntity> saveList, BalanceDivideReqDTO balanceDivideReqDTO);

    /**
     * 检验主交易单号是否存在
     * @param businessOrderNo 主交易单号
     */
    void checkExistBusinessOrderNo(String businessOrderNo);

    void updateByUnionPayDivideReqDTO(UnionPayDivideRespDTO data,  String appId);
}
