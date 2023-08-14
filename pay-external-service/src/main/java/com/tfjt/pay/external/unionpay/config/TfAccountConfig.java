package com.tfjt.pay.external.unionpay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author songx
 * @date 2023-08-12 16:44
 * @email 598482054@qq.com
 */
@Data
@Component
@ConfigurationProperties(prefix = "unionpay")
public class TfAccountConfig {
    /**
     * 同福电子账簿id
     */
    private String balanceAcctId;
    /**
     * 同福电子账簿名称
     */
    private String balanceAcctName;
}
