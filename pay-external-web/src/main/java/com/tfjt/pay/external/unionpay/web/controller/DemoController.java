package com.tfjt.pay.external.unionpay.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.dto.ExtraDTO;
import com.tfjt.pay.external.unionpay.dto.GuaranteePaymentDTO;
import com.tfjt.pay.external.unionpay.dto.req.*;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesCheckRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ElectronicBookRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.WithdrawalCreateRespDTO;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.pay.external.unionpay.utils.UnionPaySignUtil;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@Slf4j
@RestController
@RequestMapping("demo/user")
public class DemoController {


    @Autowired
    private RedisCache redisCache;


    @Autowired
    private UnionPayService unionPayService;

    @Value("${unionPayLoans.encodedPub}")
    private String encodedPub;
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
        consumerPoliciesReqDTO.setCombinedOutOrderNo("sdk-example-1695093956g5Gbf");
        consumerPoliciesReqDTO.setSentAt("2023-08-16T14:58:08.699+08:00");
        consumerPoliciesReqDTO.setPayBalanceAcctId("2008362494748960292"); //2008362494748960292
       // consumerPoliciesReqDTO.setPassword("BJJ/EAiU2lzXyEMc6VcpfCghuyWEmH2urxgwNr7MYX3bXjV2SdSjHXFg6NJHKcFS/xl+BD9GUSJUyI1OsXcD3Syndh+XWwSLfflttlrtu0A3W18v1UiZf11oNL4ag8LqpsIEvpuFLnAO");
            //担保消费参数
            List<GuaranteePaymentDTO> list = new ArrayList<>();
            GuaranteePaymentDTO guaranteePaymentDTO = new GuaranteePaymentDTO();
            guaranteePaymentDTO.setAmount(1);
            guaranteePaymentDTO.setRecvBalanceAcctId("2008349494890702347"); //2008349494890702347
            guaranteePaymentDTO.setOutOrderNo("D202308091505120");
                //扩展字段集合
                List<ExtraDTO>  list2 = new ArrayList<>();
                ExtraDTO extraDTO = new ExtraDTO();
                    extraDTO.setOrderNo("2008349494890702358");
                    extraDTO.setOrderAmount("1");
                    extraDTO.setProductCount("1");
                    extraDTO.setProductName("测试产品2");
                Map<String,Object> map = new HashMap<>();
                list2.add(extraDTO);
                map.put("productInfos",list2);
                guaranteePaymentDTO.setExtra(map);
            list.add(guaranteePaymentDTO);
        consumerPoliciesReqDTO.setRemark("用于 SDK 示例测试");
        consumerPoliciesReqDTO.setGuaranteePaymentParams(list);
        Map<String,Object> extra = new HashMap<>();
        extra.put("notifyUrl","http://uagp2g.natappfree.cc/tf-pay-external/demo/user/withdrawalCallback");  //回调地址
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
        consumerPoliciesCheckReqDTO.setOutOrderNo("D202308091505120");
        consumerPoliciesCheckReqDTO.setGuaranteePaymentId("3308706297495007304"); //合并消费担保下单子订单系统订单号
        consumerPoliciesCheckReqDTO.setSentAt("2023-08-16T09:12:08.699+08:00");
        consumerPoliciesCheckReqDTO.setAmount(1);
        //consumerPoliciesCheckReqDTO.setPassword("BJJ/EAiU2lzXyEMc6VcpfCghuyWEmH2urxgwNr7MYX3bXjV2SdSjHXFg6NJHKcFS/xl+BD9GUSJUyI1OsXcD3Syndh+XWwSLfflttlrtu0A3W18v1UiZf11oNL4ag8LqpsIEvpuFLnAO");
        consumerPoliciesCheckReqDTO.setRemark("用于 SDK 示例测试");
        List<GuaranteePaymentDTO> list = new ArrayList<>();
        GuaranteePaymentDTO guaranteePaymentDTO = new GuaranteePaymentDTO();
        guaranteePaymentDTO.setRecvBalanceAcctId("2008362494748960292");  // 付款方电子账簿ID
        guaranteePaymentDTO.setAmount(1);
        guaranteePaymentDTO.setRemark("用于 SDK 示例测试");
        //扩展字段集合
        List<ExtraDTO>  list2 = new ArrayList<>();
        ExtraDTO extraDTO = new ExtraDTO();
        extraDTO.setOrderNo("2008349494890702358");
        extraDTO.setOrderAmount("1");
        extraDTO.setProductCount("1");
        extraDTO.setProductName("测试产品2");
        Map<String,Object> map = new HashMap<>();
        list2.add(extraDTO);
        map.put("productInfos",list2);
        guaranteePaymentDTO.setExtra(map);
        list.add(guaranteePaymentDTO);
        consumerPoliciesCheckReqDTO.setTransferParams(list);
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
        withdrawalCreateReqDTO.setOutOrderNo("2008349494890702355");
        withdrawalCreateReqDTO.setSentAt("2023-08-14T11:30:08.647+08:00");
        withdrawalCreateReqDTO.setAmount(1L);
        withdrawalCreateReqDTO.setServiceFee(null);
        withdrawalCreateReqDTO.setBalanceAcctId("2008362494748960292");//电子账簿ID
        withdrawalCreateReqDTO.setBusinessType("1");
        withdrawalCreateReqDTO.setBankAcctNo(UnionPaySignUtil.SM2(encodedPub, "6222030402000890604"));//提现目标银行账号 提现目标银行账号需要加密处理  6228480639353401873
        withdrawalCreateReqDTO.setBankAcctType("1"); //提现至非绑定账户时 必填 枚举值： 1-对私银行卡 2-对公银行账户
//        withdrawalCreateReqDTO.setBankCode("");// 开户银行联行号  提现至非绑定账户时必填
//        withdrawalCreateReqDTO.setName(""); //开户名称 提现至非绑定账户时必填  需要加密处理
//        withdrawalCreateReqDTO.setBankMemo(""); //银行附言
        withdrawalCreateReqDTO.setMobileNumber(UnionPaySignUtil.SM2(encodedPub, "18712942960")); //手机号 需要加密处理
        withdrawalCreateReqDTO.setRemark("用于 SDK 示例测试");
        Map<String,Object> map = new HashMap<>();
        map.put("notifyUrl","http://uagp2g.natappfree.cc/tf-pay-external/demo/user/withdrawalCallback");
        withdrawalCreateReqDTO.setExtra(map);

        return  unionPayService.withdrawalCreation(withdrawalCreateReqDTO);
    }

    /**
     * 查询订单状态(暂未开通)
     */
    @RequestMapping("/queryOrder")
    public Result<ConsumerPoliciesRespDTO> queryOrder(){
        String combinedGuaranteePaymentId = "4908694296947634279";


        return  unionPayService.querySystemOrderStatus( combinedGuaranteePaymentId);
    }

    /**
     * 查询订单状态
     */
    @RequestMapping("/queryPlatformOrderStatus")
    public Result<ConsumerPoliciesRespDTO> queryPlatformOrderStatus(@RequestParam String combinedGuaranteePaymentId){
       // String combinedGuaranteePaymentId = "sdk-example-1695093949g5Gbf";//"sdk-example-1685093948g5Gbf";//"sdk-example-1685093949g5Gbf";


        return  unionPayService.queryPlatformOrderStatus( combinedGuaranteePaymentId);
    }
    /**
     * 查询订单状态
     */
    @RequestMapping("/getWithdrawal")
    public Result<WithdrawalCreateRespDTO> getWithdrawal(@RequestParam String outOrderNo){
        // String combinedGuaranteePaymentId = "sdk-example-1695093949g5Gbf";//"sdk-example-1685093948g5Gbf";//"sdk-example-1685093949g5Gbf";


        return  unionPayService.getWithdrawal(outOrderNo);
    }


    /**
     * 电子账簿查询
     */
    @RequestMapping("/electronicBook")
    public Result<ElectronicBookRespDTO> electronicBook(){
        ElectronicBookReqDTO electronicBookReqDTO = new ElectronicBookReqDTO();
        electronicBookReqDTO.setBalanceAcctId("2008362494748960292");
        electronicBookReqDTO.setEndAt(null);
        electronicBookReqDTO.setCursor(null);
        electronicBookReqDTO.setSentAt(null);
        electronicBookReqDTO.setSize(10);
        electronicBookReqDTO.setTradeId(null);
         return  unionPayService.electronicBook(electronicBookReqDTO);
    }
}
