package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.api.dto.req.IncomingModuleStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryAccessBankStatueRespDTO;
import com.tfjt.pay.external.unionpay.biz.PabcBizService;
import com.tfjt.pay.external.unionpay.api.dto.req.QueryAccessBankStatueReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.*;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 16:03
 */
@RestController
@RequestMapping("/bankinfo")
@Slf4j
public class BankInfoController {

    @Autowired
    private PabcBizService pabcBizService;

    /**
     * 根据名称查询总行信息
     * @param name  模糊查询条件（名称）非必须
     * @return
     */
    @GetMapping("/getBankInfoByName")
    public Result<List<PabcBankNameAndCodeRespDTO>> getBankInfoByName(String name){
        return pabcBizService.getBankInfoByName(name);
    }

    /**
     * 根据名称查询省份名称
     * @param name  模糊查询条件（省份名称）非必须
     * @return
     */
    @GetMapping("/getProvinceList")
    public Result<List<PabcProvinceInfoRespDTO>> getProvinceList(String name){
        return pabcBizService.getProvinceList(name);
    }

    /**
     * 根据省份code和总行code查询市信息
     * @param provinceCode  省份code
     * @param bankCode      银行code
     * @return
     */
    @GetMapping("/getCityList")
        public Result<List<PabcCityInfoRespDTO>> getCityList(String provinceCode,String bankCode){
        return pabcBizService.getCityList(provinceCode,bankCode);
    }

    /**
     * 查询支行信息（大小额联行号、支行名称、超级网银号，清算行号）
     * @param bankCode          总行code，必填
     * @param cityCode          城市code，必填
     * @param branchBankName    支行名称，模糊查询条件，非必填
     * @return
     */
    @GetMapping("/getBranchBankInfo")
    public Result<List<PabcBranchBankInfoRespDTO>> getBranchBankInfo(String bankCode,String cityCode,String branchBankName){
        return pabcBizService.getBranchBankInfo(bankCode,cityCode,branchBankName);
    }

    /**
     * 查询入网状态
     * @param queryAccessBankStatueReqDTO   查询参数
     * @return
     */
    @PostMapping("/getNetworkStatus")
    public Result<List<QueryAccessBankStatueRespDTO>> getNetworkStatus(@RequestBody QueryAccessBankStatueReqDTO queryAccessBankStatueReqDTO){
        return pabcBizService.getNetworkStatus(queryAccessBankStatueReqDTO);
    }

    /**
     * 根据区域判断进件入网类型
     * @param code  地区code
     * @return
     */
    @GetMapping("/getNetworkTypeByAreaCode")
    public Result<Integer> getNetworkTypeByAreaCode(String code){
        return pabcBizService.getNetworkTypeByAreaCode(code);
    }

    /**
     * 查询模块完成状态（身份信息、营业信息、结算信息）
     * @param incomingModuleStatusReqDTO
     */
    @PostMapping("/getModuleStatus")
    public Result<MoudleStatusRespDTO> getModuleStatus(@RequestBody IncomingModuleStatusReqDTO incomingModuleStatusReqDTO){
        return pabcBizService.getModuleStatus(incomingModuleStatusReqDTO);
    }


}
