package com.tfjt.pay.external.unionpay.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <pre>
 *  Mapp_info_list
 * </pre>
 *
 * @author 李扬
 * @verison $Id: Mapp_info_list v 0.1 2024-02-05 14:59:11
 */
@Data
public class MappInfoListRespDTO {

    /**
     * <pre>
     *
     * </pre>
     */
    @JsonProperty("apptype_id")
    private String apptypeId;

    /**
     * <pre>
     * submchnt_info_list
     * </pre>
     */
    @JsonProperty("submchnt_info_list")
    private List<SubmchntInfoListRespDTO> submchntInfoList;

    /**
     * <pre>
     *
     * </pre>
     */
    @JsonProperty("mapp_no")
    private String mappNo;

    /**
     * <pre>
     *
     * </pre>
     */
    private String aliAuthorized;

    /**
     * <pre>
     *
     * </pre>
     */
    @JsonProperty("term_app_no_list")
    private String termAppNoList;

    /**
     * <pre>
     *
     * </pre>
     */
    private String wxAuthorized;

    /**
     * <pre>
     * card_type_fee_list
     * </pre>
     */
    @JsonProperty("card_type_fee_list")
    private List<CardTypeFeeListRespDTO> cardTypeFeeList;


}
