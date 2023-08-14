package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.dto.req.*;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesCheckRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.WithdrawalCreateRespDTO;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.dto.response.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 会员表
 *
 * @author ???
 * @email sunlightcs@gmail.com
 * @date 2022-09-23 16:37:14
 */
@RestController
@RequestMapping("demo/user")
public class DemoController {


    @Autowired
    private RedisCache redisCache;


    @Autowired
    private UnionPayService unionPayService;
    /**
     * 合并消费担保下单 例子
     */
    @RequestMapping("/list")
    public Result<ConsumerPoliciesRespDTO> list(){
//        String key = CacheConstants.USER_DETAILS + ":li";
//        redisCache.setCacheObject(key,"11", 5, TimeUnit.SECONDS);
//        String s = redisCache.getCacheObject(key);
//        return Result.ok(demoService.page(page, Wrappers.query(userEntity) ));
        ConsumerPoliciesReqDTO consumerPoliciesReqDTO = new ConsumerPoliciesReqDTO();
        consumerPoliciesReqDTO.setCombinedOutOrderNo("sdk-example-1685093948g5Gbf");
        consumerPoliciesReqDTO.setSentAt("2023-05-26T17:39:08.699+08:00");
        consumerPoliciesReqDTO.setPayBalanceAcctId("2008362494748960292"); //2008362494748960292
        consumerPoliciesReqDTO.setPassword("BJJ/EAiU2lzXyEMc6VcpfCghuyWEmH2urxgwNr7MYX3bXjV2SdSjHXFg6NJHKcFS/xl+BD9GUSJUyI1OsXcD3Syndh+XWwSLfflttlrtu0A3W18v1UiZf11oNL4ag8LqpsIEvpuFLnAO");
            //担保消费参数
            List<GuaranteePaymentDTO> list = new ArrayList<>();
            GuaranteePaymentDTO guaranteePaymentDTO = new GuaranteePaymentDTO();
            guaranteePaymentDTO.setAmount(1);
            guaranteePaymentDTO.setRecvBalanceAcctId("2008349494890702347"); //2008349494890702347
            guaranteePaymentDTO.setOutOrderNo("D202308091505111");
                //扩展字段集合
                List<ExtraDTO>  list2 = new ArrayList<>();
                ExtraDTO extraDTO = new ExtraDTO();
                    extraDTO.setOrderNo("2008349494890702347");
                    extraDTO.setOrderAmount(1);
                    extraDTO.setProductCount(1);
                    extraDTO.setProductName("测试产品");
                Map<String,Object> map = new HashMap<>();
                list2.add(extraDTO);
                map.put("productInfos",list2);
                guaranteePaymentDTO.setExtra(map);
            list.add(guaranteePaymentDTO);
        consumerPoliciesReqDTO.setRemark("用于 SDK 示例测试");
        consumerPoliciesReqDTO.setGuaranteePaymentParams(list);
        Map<String,Object> extra = new HashMap<>();
        extra.put("notifyUrl","https://www.baidu.com/?tn=87135040_8_oem_dg");  //回调地址
        consumerPoliciesReqDTO.setExtra(extra);
        return  unionPayService.mergeConsumerPolicies(consumerPoliciesReqDTO);
    }

    /**
     * 合并消费担保确认 例子
     */
    @RequestMapping("/listCheck")
    public Result<ConsumerPoliciesCheckRespDTO> listCheck(){
        //合并消费担保确认
        ConsumerPoliciesCheckReqDTO consumerPoliciesCheckReqDTO = new ConsumerPoliciesCheckReqDTO();
        consumerPoliciesCheckReqDTO.setOutOrderNo("2008349494890702347");
        consumerPoliciesCheckReqDTO.setGuaranteePaymentId("4908694296947634279"); //合并消费担保下单子订单系统订单号
        consumerPoliciesCheckReqDTO.setSentAt("2023-08-14T11:30:08.647+08:00");
        consumerPoliciesCheckReqDTO.setAmount(1);
        consumerPoliciesCheckReqDTO.setPassword("BJJ/EAiU2lzXyEMc6VcpfCghuyWEmH2urxgwNr7MYX3bXjV2SdSjHXFg6NJHKcFS/xl+BD9GUSJUyI1OsXcD3Syndh+XWwSLfflttlrtu0A3W18v1UiZf11oNL4ag8LqpsIEvpuFLnAO");
        consumerPoliciesCheckReqDTO.setRemark("用于 SDK 示例测试");
        //扩展字段集合
        List<ExtraDTO>  list2 = new ArrayList<>();
        ExtraDTO extraDTO = new ExtraDTO();
        extraDTO.setOrderNo("2008349494890702347");
        extraDTO.setOrderAmount(1);
        extraDTO.setProductCount(1);
        extraDTO.setProductName("测试产品");
        Map<String,Object> map = new HashMap<>();
        list2.add(extraDTO);
        map.put("productInfos",list2);
        consumerPoliciesCheckReqDTO.setExtra(map);

        return  unionPayService.mergeConsumerPoliciesCheck(consumerPoliciesCheckReqDTO);
    }

    /**
     * 提现 例子
     */
    @RequestMapping("/withdrawalCreation")
    public Result<WithdrawalCreateRespDTO> withdrawalCreation(){
        //提现
        WithdrawalCreateReqDTO withdrawalCreateReqDTO = new WithdrawalCreateReqDTO();
        withdrawalCreateReqDTO.setOutOrderNo("2008349494890702347");
        withdrawalCreateReqDTO.setSentAt("2008362494748960292");
        withdrawalCreateReqDTO.setAmount(1);
        withdrawalCreateReqDTO.setServiceFee(null);
        withdrawalCreateReqDTO.setBalanceAcctId("2008362494748960292");//电子账簿ID
        withdrawalCreateReqDTO.setBusinessType("1");
        withdrawalCreateReqDTO.setBankAcctNo("");//提现目标银行账号 提现目标银行账号需要加密处理
        withdrawalCreateReqDTO.setBankAcctType("1"); //提现至非绑定账户时 必填 枚举值： 1-对私银行卡 2-对公银行账户
        withdrawalCreateReqDTO.setBankCode("");// 开户银行联行号  提现至非绑定账户时必填
        withdrawalCreateReqDTO.setName(""); //开户名称 提现至非绑定账户时必填  需要加密处理
        withdrawalCreateReqDTO.setBankMemo(""); //银行附言
        withdrawalCreateReqDTO.setMobileNumber(""); //手机号 需要加密处理
        withdrawalCreateReqDTO.setRemark("用于 SDK 示例测试");
        Map<String,Object> map = new HashMap<>();
        map.put("notifyUrl","http://");
        withdrawalCreateReqDTO.setExtra(map);

        return  unionPayService.withdrawalCreation(withdrawalCreateReqDTO);
    }

}
