package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ElectronicBookRespDTO
 * @description: 电子账簿查询返回结果
 * @author Lzh
 * @date 2023年08月09日
 * @version: 1.0
 */
@Data
public class ElectronicBookRespDTO implements Serializable {
    /**总条数*/
    private Integer total;

    /**下一页游标 */
    private String nextCursor;

    /**页面条数 */
    private Integer size;

    /**返回结果集合*/
    private List<ElectronicBookResultRespDTO> data;


}
