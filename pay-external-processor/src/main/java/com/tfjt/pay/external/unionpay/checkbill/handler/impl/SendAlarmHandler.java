package com.tfjt.pay.external.unionpay.checkbill.handler.impl;

import com.tfjt.pay.external.unionpay.checkbill.handler.CheckBillHandler;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dto.CheckLoanBillDTO;
import com.tfjt.robot.common.message.ding.MarkdownMessage;
import com.tfjt.robot.service.dingtalk.DingRobotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Auther: songx
 * @Date: 2023/10/28/09:34
 * @Description:
 */
@Order(NumberConstant.TWO)
@Component
public class SendAlarmHandler implements CheckBillHandler {

    @Value("${spring.profiles.active}")
    private String env;

    @Resource
    private DingRobotService dingRobotService;
    @Override
    public boolean handler(CheckLoanBillDTO checkLoanBillDTO) {
        String envName;
        if (("prod").equals(env) || ("pre").equals(env)){
            envName = "生产";
        }else {
            envName = "测试";
        }
        dingRobotService.send(MarkdownMessage.buildBiz(String.format("【%s】%s",envName, "校验贷款数据对账单"), String.format("贷款对账数据存疑,对账批次号:[%s],请尽快查看",checkLoanBillDTO.getWarnBatchNo())),
                "ce2af6cff8993d88584620a8149e8678e10ac210a85925ce1c25aaa9546a1b14", true, "SEC1a33971a146d349f962c98110ad1234e4f7cc93dc6dd8e644f6f9a80e732ed61");
        return false;
    }
}
