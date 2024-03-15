package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/13 10:23
 */
@Data
public class BusinessContactsRespDTO implements Serializable {
    private static final long serialVersionUID = 8988171813756262795L;

    /**
     * 法人姓名
     */
    private String legalName;
    /**
     * 法人证件类型
     */
    private String legalDocumentType;
    /**
     * 证件号
     */
    private String legalDocumentNo;
    /**
     * 证件图片：正面（人像面）
     */
    private String legalDocumentFrontUrl;
    /**
     * 证件图片：反面（国徽面）
     */
    private String legalDocumentOppositeUrl;
    /**
     *联系人姓名
     */
    private String contactsName;
    /**
     * 法人证件类型
     */
    private String contactsDocumentType;
    /**
     * 证件号
     */
    private String contactsDocumentNo;
    /**
     * 证件图片：正面（人像面）
     */
    private String contactsDocumentFrontUrl;
    /**
     * 证件图片：反面（国徽面）
     */
    private String contactsDocumentOppositeUrl;

}
