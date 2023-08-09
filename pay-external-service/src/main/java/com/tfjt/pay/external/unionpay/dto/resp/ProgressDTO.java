package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

/**
 * @description: ProgressVO <br>
 * @date: 2023/5/24 13:40 <br>
 * @author: young <br>
 * @version: 1.0
 */
@Data
public class ProgressDTO {

    private Integer type;

    private boolean finish;
}
