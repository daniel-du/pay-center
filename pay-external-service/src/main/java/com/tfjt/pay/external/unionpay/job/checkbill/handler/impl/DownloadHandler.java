package com.tfjt.pay.external.unionpay.job.checkbill.handler.impl;

import cn.hutool.core.date.DateUtil;
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayCheckBillReqDTO;
import com.tfjt.pay.external.unionpay.biz.LoanUnionPayCheckBillBizService;
import com.tfjt.pay.external.unionpay.job.checkbill.handler.CheckBillHandler;
import com.tfjt.pay.external.unionpay.dto.CheckLoanBillDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author songx
 * @Date 2023/10/28/09:26
 * @Description:
 */
@Order(0)
@Component
public class DownloadHandler implements CheckBillHandler {
    @Resource
    private LoanUnionPayCheckBillBizService loanUnionPayCheckBillBiz;

    /**
     * 调用银联接口下载电子对账单
     * @param checkLoanBillDTO 对账参数
     * @return  是否执行下一步
     */
    @Override
    public boolean handler(CheckLoanBillDTO checkLoanBillDTO) {
        UnionPayCheckBillReqDTO unionPayCheckBillReqDTO = new UnionPayCheckBillReqDTO();
        unionPayCheckBillReqDTO.setDate(DateUtil.formatDate(checkLoanBillDTO.getDate()));
        return loanUnionPayCheckBillBiz.download(unionPayCheckBillReqDTO);
    }
}
