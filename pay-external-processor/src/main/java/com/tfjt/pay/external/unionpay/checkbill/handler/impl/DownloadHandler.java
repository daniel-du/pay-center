package com.tfjt.pay.external.unionpay.checkbill.handler.impl;

import cn.hutool.core.date.DateUtil;
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayCheckBillReqDTO;
import com.tfjt.pay.external.unionpay.biz.LoanUnionPayCheckBillBiz;
import com.tfjt.pay.external.unionpay.checkbill.handler.CheckBillHandler;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.tfcommon.dto.response.Result;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Auther: songx
 * @Date: 2023/10/28/09:26
 * @Description:
 */
@Order(NumberConstant.ZERO)
@Component
public class DownloadHandler implements CheckBillHandler {
    @Resource
    private LoanUnionPayCheckBillBiz loanUnionPayCheckBillBiz;

    /**
     * 调用银联接口下载电子对账单
     * @param date 对账日期
     * @return  是否执行下一步
     */
    @Override
    public boolean handler(Date date) {
        UnionPayCheckBillReqDTO unionPayCheckBillReqDTO = new UnionPayCheckBillReqDTO();
        unionPayCheckBillReqDTO.setDate(DateUtil.formatDate(date));
        Result<String> result = loanUnionPayCheckBillBiz.downloadCheckBill(unionPayCheckBillReqDTO);
        return result.getCode()== NumberConstant.ZERO;
    }
}
