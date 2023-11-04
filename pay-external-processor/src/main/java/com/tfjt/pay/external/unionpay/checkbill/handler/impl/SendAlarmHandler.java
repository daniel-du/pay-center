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

/**
 * @author songx
 * @Date: 2023/10/28/09:34
 * @Description:
 */
@Order(2)
@Component
public class SendAlarmHandler implements CheckBillHandler {

    @Value("${spring.profiles.active}")
    private String env;

    @Resource
    private DingRobotService dingRobotService;

    /**
     * 发送钉钉报警
     * @param checkLoanBillDTO  报警参数
     * @return 是否后续流程
     */
    @Override
    public boolean handler(CheckLoanBillDTO checkLoanBillDTO) {
        String envName;
        if (("prod").equals(env) || ("pre").equals(env)){
            envName = "生产";
        }else {
            envName = "测试";
        }
        dingRobotService.send(MarkdownMessage.buildBiz(String.format("【%s】%s",envName, "校验贷款数据对账单"), String.format("贷款对账数据存疑,对账批次号:[%s],请尽快查看",checkLoanBillDTO.getWarnBatchNo())));
        return false;
    }
}
