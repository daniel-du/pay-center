package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-12 17:17
 * @email 598482054@qq.com
 */
@Data
public class LoanAccountDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**系统生成的唯一的电子账簿ID*/
    private String balanceAcctId;
    /**电子账簿账簿号*/
    private String relAcctNo;
    /**可提现余额*/
    private Integer settledAmount;
    /**在途余额*/
    private Integer pendingAmount;
    /**不可用余额*/
    private Integer expensingAmount;
    /**冻结余额*/
    private Integer frozenSettledAmount;
    /**电子账簿类型 电子账簿类型：
     basic:电子账簿
     charge:挂帐账簿
     fee:平台手续费账簿
     deposit:充值账簿
     withdrawal:提现在途账簿
     guarantee:担保账簿
     advance:垫资账簿
     marketing:营销账簿
     bank_fund:银行存款账簿*/
    private String acctType;
    /**电子账簿是否被冻结*/
    private boolean isFrozen;
    /**备注*/
    private String remark;
}
