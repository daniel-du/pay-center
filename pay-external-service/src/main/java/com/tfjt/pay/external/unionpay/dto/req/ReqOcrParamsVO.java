package com.tfjt.pay.external.unionpay.dto.req;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: ReqOcr <br>
 * @date: 2023/5/20 10:07 <br>
 * @author: young <br>
 * @version: 1.0
 */

@Data
public class ReqOcrParamsVO {
    String image;
    Map<String,Object> configure;
}
