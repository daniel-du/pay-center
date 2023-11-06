package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayLoanOrderUnifiedorderReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.LoanOrderDetailsRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.LoanQueryOrderRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.MergeConsumerRepDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.UnionPayTransferRespDTO;
import com.tfjt.pay.external.unionpay.dto.req.ConsumerPoliciesReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.LoanOrderUnifiedorderReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.LoanTransferRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesRespDTO;
import com.tfjt.pay.external.unionpay.entity.LoanOrderDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import com.tfjt.tfcommon.dto.response.Result;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author songx
 * @date 2023-08-21 18:49
 * @email 598482054@qq.com
 */
public interface LoanOrderBizService {
    /**
     * 保存转账参数
     * @param loanTransferRespDTO
     * @param tradeOrderNo
     */
    void transferSaveOrder(LoanTransferRespDTO loanTransferRespDTO, String tradeOrderNo);

    /**
     * 验证指定的单号是否存在
     * @param businessOrderNo 业务单号
     * @param appId  应用id
     * @return  true 存在  false 不存在
     */
    boolean checkExistBusinessOrderNo(String businessOrderNo, String appId);

    /**
     * 根据业务单号和业务应用 获取订单信息
     * @param businessOrderNo 业务单号
     * @param appId         业务appid
     * @return 订单信息
     */
    LoanOrderEntity getByBusinessAndAppId(String businessOrderNo, String appId);

    /**
     * 保存下单商品信息
     * 并生成调用银联下单接口参数
     *
     * @param loanOrderUnifiedorderReqDTO 商品订单信息
     * @param notifyUrl                   银联回调通知地址
     * @return 调用银联参数
     */
     ConsumerPoliciesReqDTO unifiedorderSaveOrderAndBuildUnionPayParam(LoanOrderUnifiedorderReqDTO loanOrderUnifiedorderReqDTO, String notifyUrl);
    /**
     * 修改订单状态
     *
     * @param result 银联合并下单返回数据
     * @param appId appId
     */
    void saveMergeConsumerResult(Result<ConsumerPoliciesRespDTO> result, String appId);

    /**
     * 根据订单id获取订单详情
     * @param id
     * @return
     */
    List<LoanOrderDetailsEntity> listOrderDetailByOrderId(Long id);

    List<LoanOrderDetailsRespDTO> listLoanOrderDetailsRespDTO(Long id);

    /**
     * 下单接口
     * @param loanOrderUnifiedorderDTO 下单参数
     * @return 下单返回信息
     */
    Result<MergeConsumerRepDTO> unifiedorder(UnionPayLoanOrderUnifiedorderReqDTO loanOrderUnifiedorderDTO);

    /**
     * 转账信息
     * @param payTransferDTO 转账参数
     * @return               转账结果
     */
    Result<String> transfer(UnionPayTransferRespDTO payTransferDTO);

    /**
     * 查询交易记录接口
     * @param businessOrderNo 业务系统唯一标识
     * @param appId           业务appId
     * @return                交易结果
     */
    Result<LoanQueryOrderRespDTO> orderQuery(String businessOrderNo, String appId);

    /**
     * 下单未核对数量
     * @param date 核对日期
     * @return 数量
     */
    Integer unCheckCount(Date date);

    /**
     * 订单未核对列表
     * @param date 核对日期
     * @param pageNo 页数
     * @param pageSize 每页显示数量
     * @return 分页数据
     */
    List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, Integer pageNo, Integer pageSize);

    /**
     * 订单确认未核对列表
     * @param date 核对日期
     * @param pageNo 页数
     * @param pageSize 每页显示数量
     * @return 分页数据
     */
    List<LoanUnionpayCheckBillDetailsEntity> listDetailsUnCheckBill(Date date, Integer pageNo, Integer pageSize);
    /**
     * 订单确认未核对数量
     * @param date 核对日期
     * @return 数量
     */
    Integer unDetailsCheckCount(Date date);
}
