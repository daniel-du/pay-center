package com.tfjt.pay.external.unionpay.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <pre>
 *  Submchnt_info_list
 * </pre>
 * @author 李扬
 * @verison $Id: Submchnt_info_list v 0.1 2024-02-05 14:59:11
 */
@Data
public class SubmchntInfoListRespDTO {

    /**
     * <pre>
     * 牟平区芭佰利服装店
     * </pre>
     */
    private String	submchntBriefname;

    /**
     * <pre>
     *
     * </pre>
     */
    @JsonProperty("term_app_no_list")
    private String	term_app_no_list;

    /**
     * <pre>
     *
     * </pre>
     */
    private String	submchntId;

    /**
     * <pre>
     * 牟平区芭佰利服装店
     * </pre>
     */
    private String	submchntName;


}
