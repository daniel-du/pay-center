package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.dto.req.ConsumerPoliciesCheckReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.ConsumerPoliciesReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.ExtraDTO;
import com.tfjt.pay.external.unionpay.dto.req.GuaranteePaymentDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesRespDTO;
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


    @DubboReference
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
        consumerPoliciesReqDTO.setPayBalanceAcctId("2008362494748960292");
        consumerPoliciesReqDTO.setPassword("BJJ/EAiU2lzXyEMc6VcpfCghuyWEmH2urxgwNr7MYX3bXjV2SdSjHXFg6NJHKcFS/xl+BD9GUSJUyI1OsXcD3Syndh+XWwSLfflttlrtu0A3W18v1UiZf11oNL4ag8LqpsIEvpuFLnAO");
            //担保消费参数
            List<GuaranteePaymentDTO> list = new ArrayList<>();
            GuaranteePaymentDTO guaranteePaymentDTO = new GuaranteePaymentDTO();
            guaranteePaymentDTO.setAmount(1);
            guaranteePaymentDTO.setRecvBalanceAcctId("2008349494890702347");
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
    public Result<ConsumerPoliciesRespDTO> listCheck(){
        //合并消费担保确认
        ConsumerPoliciesCheckReqDTO consumerPoliciesCheckReqDTO = new ConsumerPoliciesCheckReqDTO();
        consumerPoliciesCheckReqDTO.setOutOrderNo("2008349494890702347");
        consumerPoliciesCheckReqDTO.setGuaranteePaymentId(""); //合并消费担保下单子订单系统订单号
        consumerPoliciesCheckReqDTO.setSentAt("2008362494748960292");
        consumerPoliciesCheckReqDTO.setAmount(1);
            //分账参数
            GuaranteePaymentDTO guaranteePaymentDTO = new GuaranteePaymentDTO();
            guaranteePaymentDTO.setRecvBalanceAcctId("");
            guaranteePaymentDTO.setAmount(1);
            guaranteePaymentDTO.setRemark("");

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

        consumerPoliciesCheckReqDTO.setPassword("BJJ/EAiU2lzXyEMc6VcpfCghuyWEmH2urxgwNr7MYX3bXjV2SdSjHXFg6NJHKcFS/xl+BD9GUSJUyI1OsXcD3Syndh+XWwSLfflttlrtu0A3W18v1UiZf11oNL4ag8LqpsIEvpuFLnAO");
        consumerPoliciesCheckReqDTO.setTransferPassword("BJJ/EAiU2lzXyEMc6VcpfCghuyWEmH2urxgwNr7MYX3bXjV2SdSjHXFg6NJHKcFS/xl+BD9GUSJUyI1OsXcD3Syndh+XWwSLfflttlrtu0A3W18v1UiZf11oNL4ag8LqpsIEvpuFLnAO");
        consumerPoliciesCheckReqDTO.setRemark("用于 SDK 示例测试");

        return  unionPayService.mergeConsumerPoliciesCheck(consumerPoliciesCheckReqDTO);
    }

}
