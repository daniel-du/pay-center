package com.tfjt.pay.external.unionpay.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.service.CustBusinessInfoService;
import com.tfjt.pay.external.unionpay.entity.CustBusinessAttachInfoEntity;
import com.tfjt.pay.external.unionpay.entity.CustBusinessInfoEntity;
import com.tfjt.pay.external.unionpay.enums.ImgTypeEnum;
import com.tfjt.pay.external.unionpay.service.CustBusinessAttachInfoService;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 经营信息
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-20 09:27:38
 */
@RestController
@RequestMapping("custbusinessinfo")
@Slf4j
public class CustBusinessInfoController {
    @Autowired
    private CustBusinessInfoService custBusinessInfoService;
    @Autowired
    private CustBusinessAttachInfoService custBusinessAttachInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result<?> list(@RequestParam Map<String, Object> params) {

        return Result.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{loanUserId}")
    public Result<?> info(@PathVariable("loanUserId") Integer loanUserId) {
        CustBusinessInfoEntity custBusinessInfo = custBusinessInfoService.getBaseMapper().selectOne(Wrappers.lambdaQuery(CustBusinessInfoEntity.class).eq(CustBusinessInfoEntity::getLoanUserId, loanUserId));
        List<CustBusinessAttachInfoEntity> attachInfoList = custBusinessAttachInfoService.list(Wrappers.lambdaQuery(CustBusinessAttachInfoEntity.class).eq(CustBusinessAttachInfoEntity::getCustBusinessInfoId, custBusinessInfo.getId()));
        List<CustBusinessAttachInfoEntity> goodsAttachInfoList = new ArrayList<>();
        /**
         * 证明材料
         */
        List<CustBusinessAttachInfoEntity> evidenceAttachInfoList = new ArrayList<>();
        attachInfoList.forEach(attachInfo -> {
            if (attachInfo.getType().equals(ImgTypeEnum.FACADE_PHOTO.getCode())) {
                custBusinessInfo.setFacadePhotoUrl(attachInfo.getImgUrl());
                custBusinessInfo.setFacadePhotoId(attachInfo.getId());
            }

            if (attachInfo.getType().equals(ImgTypeEnum.SHOP_PHOTO.getCode())) {
                custBusinessInfo.setShopPhotoUrl(attachInfo.getImgUrl());
                custBusinessInfo.setShopPhotoId(attachInfo.getId());
            }

            if (attachInfo.getType().equals(ImgTypeEnum.GOOD_PHOTO.getCode())) {
                goodsAttachInfoList.add(attachInfo);
            }

            if (attachInfo.getType().equals(ImgTypeEnum.EVIDENC_PHOTO.getCode())) {
                evidenceAttachInfoList.add(attachInfo);
            }
        });
        custBusinessInfo.setGoodsAttachInfoList(goodsAttachInfoList);
        custBusinessInfo.setEvidenceAttachInfoList(evidenceAttachInfoList);
        return Result.ok(custBusinessInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> save(@RequestBody CustBusinessInfoEntity custBusinessInfo) {
        try {
            boolean flag = DateUtil.timeComparison(null,null);
            if(!flag){
                return Result.failed("0点到凌晨04点，不受理申请！");
            }
            custBusinessInfoService.save(custBusinessInfo);
        } catch (Exception ex) {
            log.error("保存异常", ex);
            return Result.failed("保存异常");
        }
        List<CustBusinessAttachInfoEntity> businessAttachInfo = new ArrayList<>();

        /**
         * 有店铺
         */
        CustBusinessAttachInfoEntity facadeBusinessAttachInfo = new CustBusinessAttachInfoEntity();
        facadeBusinessAttachInfo.setType(ImgTypeEnum.FACADE_PHOTO.getCode());
        facadeBusinessAttachInfo.setCustBusinessInfoId(custBusinessInfo.getId());
        facadeBusinessAttachInfo.setImgUrl(custBusinessInfo.getFacadePhotoUrl());
        CustBusinessAttachInfoEntity shopBusinessAttachInfo = new CustBusinessAttachInfoEntity();
        shopBusinessAttachInfo.setType(ImgTypeEnum.SHOP_PHOTO.getCode());
        shopBusinessAttachInfo.setCustBusinessInfoId(custBusinessInfo.getId());
        shopBusinessAttachInfo.setImgUrl(custBusinessInfo.getShopPhotoUrl());


        businessAttachInfo.add(facadeBusinessAttachInfo);
        businessAttachInfo.add(shopBusinessAttachInfo);
        custBusinessInfo.getGoodsAttachInfoList().forEach(custBusinessAttachInfoEntity -> {
            custBusinessAttachInfoEntity.setType(ImgTypeEnum.GOOD_PHOTO.getCode());
            custBusinessAttachInfoEntity.setCustBusinessInfoId(custBusinessInfo.getId());
        });
        custBusinessInfo.getEvidenceAttachInfoList().forEach(custBusinessAttachInfoEntity -> {
            custBusinessAttachInfoEntity.setType(ImgTypeEnum.EVIDENC_PHOTO.getCode());
            custBusinessAttachInfoEntity.setCustBusinessInfoId(custBusinessInfo.getId());
        });

        businessAttachInfo.addAll(custBusinessInfo.getGoodsAttachInfoList());
        businessAttachInfo.addAll(custBusinessInfo.getEvidenceAttachInfoList());

        try {
            custBusinessAttachInfoService.saveBatch(businessAttachInfo);
        } catch (Exception ex) {
            log.error("保存异常", ex);
            return Result.failed("保存异常");
        }
        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(@RequestBody CustBusinessInfoEntity custBusinessInfo) {
        try {
            boolean bool = custBusinessInfoService.updateById(custBusinessInfo);
            custBusinessInfo.getGoodsAttachInfoList().forEach(custBusinessAttachInfoEntity -> {
                if (custBusinessAttachInfoEntity.getId() != null) {
                    custBusinessAttachInfoService.updateById(custBusinessAttachInfoEntity);
                } else {
                    custBusinessAttachInfoEntity.setCustBusinessInfoId(custBusinessInfo.getId());
                    custBusinessAttachInfoService.save(custBusinessAttachInfoEntity);
                }
            });

            custBusinessInfo.getEvidenceAttachInfoList().forEach(custBusinessAttachInfoEntity -> {
                if (custBusinessAttachInfoEntity.getId() != null) {
                    custBusinessAttachInfoService.updateById(custBusinessAttachInfoEntity);
                } else {
                    custBusinessAttachInfoEntity.setCustBusinessInfoId(custBusinessInfo.getId());
                    custBusinessAttachInfoService.save(custBusinessAttachInfoEntity);
                }
            });

            if(StringUtils.isNotBlank(custBusinessInfo.getFacadePhotoUrl())){
                CustBusinessAttachInfoEntity custBusinessAttachInfoEntity = custBusinessAttachInfoService.getOne(new LambdaQueryWrapper<CustBusinessAttachInfoEntity>().eq(CustBusinessAttachInfoEntity::getCustBusinessInfoId, custBusinessInfo.getId()).eq(CustBusinessAttachInfoEntity::getType, 1));
                if(custBusinessAttachInfoEntity !=null ){
                    custBusinessAttachInfoEntity.setImgUrl(custBusinessInfo.getFacadePhotoUrl());
                    custBusinessAttachInfoService.updateById(custBusinessAttachInfoEntity);
                }
            }
            if(StringUtils.isNotBlank(custBusinessInfo.getShopPhotoUrl())){
                CustBusinessAttachInfoEntity custBusinessAttachInfoEntity = custBusinessAttachInfoService.getOne(new LambdaQueryWrapper<CustBusinessAttachInfoEntity>().eq(CustBusinessAttachInfoEntity::getCustBusinessInfoId, custBusinessInfo.getId()).eq(CustBusinessAttachInfoEntity::getType, 2));
                if(custBusinessAttachInfoEntity !=null ){
                    custBusinessAttachInfoEntity.setImgUrl(custBusinessInfo.getShopPhotoUrl());
                    custBusinessAttachInfoService.updateById(custBusinessAttachInfoEntity);
                }
            }
            if (bool) {
                return Result.ok("更新成功");
            } else {
                return Result.failed("更新异常");
            }
        } catch (Exception ex) {
            log.error("更新图片异常", ex);
            return Result.failed("更新异常");
        }

    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result<?> delete(@RequestBody Integer[] ids) {
        custBusinessInfoService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
