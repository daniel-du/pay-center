package com.tfjt.pay.external.unionpay.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <pre>
 *  SigningReviewReqDTO
 * </pre>
 *
 * @author 李扬
 * @verison $Id: SigningReviewReqDTO v 0.1 2024-02-05 14:59:11
 */
@Data
public class SigningReviewRespDTO {

    /**
     * <pre>
     * 商户号
     * </pre>
     */
    @JsonProperty("mer_no")
    private String merNo;

    /**
     * <pre>
     * 请求成功
     * </pre>
     */
    @JsonProperty("res_msg")
    private String resMsg;

    /**
     * <pre>
     * mapp_info_list
     * </pre>
     */
    @JsonProperty("mapp_info_list")
    private List<MappInfoListRespDTO> mappInfoList;

    /**
     * <pre>
     * 支付宝备案商户号
     * </pre>
     */
    private String aliPayRecordMchntNo;

    /**
     * <pre>
     * 申请状态
     * 00：签约中
     * 01：签约成功
     * 02：入网审核中
     * 03：入网成功
     * 04：入网失败
     * 05：对公账户待验证或异常
     * 06：风控审核中
     * 28：资料验证失败
     * 31：冻结账户
     * 99：其它错误
     * </pre>
     */
    @JsonProperty("apply_status")
    private String applyStatus;

    /**
     * <pre>
     * 银联云闪付备案商户号
     * </pre>
     */
    private String unionPayRecordMchntNo;

    /**
     * <pre>
     *
     * </pre>
     */
    @JsonProperty("res_code")
    private String resCode;

    /**
     * <pre>
     * 来源平台账户
     * </pre>
     */
    @JsonProperty("accesser_acct")
    private String accesserAcct;

    /**
     * <pre>
     * 申请状态对应的描述信息
     * 00：签约中
     * 01：签约成功
     * 02：入网审核中
     * 03：入网成功
     * 04：入网失败
     * 05：对公账户待验证或异常
     * 06：风控审核中
     * 28：资料验证失败
     * 31：冻结账户
     * 99：其它错误
     * </pre>
     */
    @JsonProperty("apply_status_msg")
    private String applyStatusMsg;

    /**
     * <pre>
     * 自助签约平台流水号
     * </pre>
     */
    @JsonProperty("ums_reg_id")
    private String umsRegId;

    /**
     * <pre>
     * 微信备案商户号
     * </pre>
     */
    private String wechatPayRecordMchntNo;

    /**
     * <pre>
     * 企业号
     * </pre>
     */
    @JsonProperty("company_no")
    private String companyNo;

    /**
     * <pre>
     * 失败原因
     * </pre>
     */
    @JsonProperty("fail_reason")
    private String failReason;

}
