package com.tfjt.pay.external.unionpay.web.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.dto.resp.ReasonCountVO;
import com.tfjt.pay.external.unionpay.dto.resp.NcomingReasonDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ProgressDTO;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.tfcommon.core.util.BeanUtils;
import com.tfjt.tfcommon.dto.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 进件失败原因
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-24 09:00:44
 */
@RestController
@RequestMapping("ncomingreason")
public class NcomingReasonController {
    @Autowired
    private NcomingReasonService ncomingReasonService;

    @Resource
    private CustBankInfoService custBankInfoService;

    @Resource
    private CustIdcardInfoService custIdcardInfoService;
    @Resource
    private CustBusinessInfoService custBusinessInfoService;

    @Resource
    private CustHoldingService tfCustHoldingService;

    @Resource
    private CustBusinessDetailService custBusinessDetailService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result<?> list(@RequestParam Map<String, Object> params) {
        //只获取失败的
        params.put("status", 0);
        List<NcomingReasonEntity> list = ncomingReasonService.listByMap(params);
        return Result.ok(BeanUtils.copyList2Other(NcomingReasonDTO.class, list));
    }


    @GetMapping("/count/{loanUserId}")
    public Result<?> count(@PathVariable("loanUserId") Integer loanUserId) {
        List<ReasonCountVO> reasonCountVOList = ncomingReasonService.getReasonCount(loanUserId);
        return Result.ok(reasonCountVOList);
    }


    /**
     * 进件资料进度是否完成
     *
     * @param loanUserId
     * @return
     */
    @GetMapping("/progress/{loanUserId}")
    public Result<?> progress(@PathVariable("loanUserId") Integer loanUserId) {
        //客户银行信息
        List<CustBankInfoEntity> custBankInfos = custBankInfoService.getBaseMapper().selectList(Wrappers.lambdaQuery(CustBankInfoEntity.class).eq(CustBankInfoEntity::getLoanUserId, loanUserId));
        //身份信息表
        List<CustIdcardInfoEntity> idcardInfos = custIdcardInfoService.getBaseMapper().selectList(Wrappers.lambdaQuery(CustIdcardInfoEntity.class).eq(CustIdcardInfoEntity::getLoanUserId, loanUserId));
        //经营信息
        List<CustBusinessInfoEntity> custBusinessInfos = custBusinessInfoService.getBaseMapper().selectList(Wrappers.lambdaQuery(CustBusinessInfoEntity.class).eq(CustBusinessInfoEntity::getLoanUserId, loanUserId));
        //控股信息
        List<CustHoldingEntity> custHolding = tfCustHoldingService.list(new QueryWrapper<CustHoldingEntity>().eq("loan_user_id", loanUserId));
        //营业信息
        List<CustBusinessDetailEntity> custBusinessDetail = custBusinessDetailService.list(new QueryWrapper<CustBusinessDetailEntity>().eq("loan_user_id", loanUserId));

        List<ProgressDTO> ProgressDTOList = new ArrayList<>();
        ProgressDTO cardProgressDTO = new ProgressDTO();
        //身份信息
        cardProgressDTO.setType(1);
        cardProgressDTO.setFinish(CollUtil.isNotEmpty(idcardInfos));
        ProgressDTOList.add(cardProgressDTO);
        //结算信息
        ProgressDTO stProgressDTO = new ProgressDTO();
        stProgressDTO.setType(2);
        stProgressDTO.setFinish(CollUtil.isNotEmpty(custBankInfos));
        ProgressDTOList.add(stProgressDTO);
        //图片信息
        ProgressDTO buzzProgressDTO = new ProgressDTO();
        buzzProgressDTO.setType(3);
        buzzProgressDTO.setFinish(CollUtil.isNotEmpty(custBusinessInfos));
        ProgressDTOList.add(buzzProgressDTO);
        //营业信息
        ProgressDTO hProgressDTO = new ProgressDTO();
        hProgressDTO.setType(4);
        hProgressDTO.setFinish(CollUtil.isNotEmpty(custBusinessDetail));
        ProgressDTOList.add(hProgressDTO);
        //控股信息
        ProgressDTO bdProgressDTO = new ProgressDTO();
        bdProgressDTO.setType(5);
        bdProgressDTO.setFinish(CollUtil.isNotEmpty(custHolding));
        ProgressDTOList.add(bdProgressDTO);

        return Result.ok(ProgressDTOList);
    }
}
