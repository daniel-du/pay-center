package com.tfjt.pay.external.unionpay.web.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.dto.resp.CustBusinessInfoRespDTO;
import com.tfjt.pay.external.unionpay.entity.CustBusinessAttachInfoEntity;
import com.tfjt.pay.external.unionpay.entity.CustBusinessInfoEntity;
import com.tfjt.pay.external.unionpay.enums.ImgTypeEnum;
import com.tfjt.pay.external.unionpay.service.CustBusinessAttachInfoService;
import com.tfjt.pay.external.unionpay.service.CustBusinessInfoService;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${unionPay.isTest:false}")
    boolean isTest;

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
        /**
         * 证明材料
         */
        CustBusinessInfoRespDTO custBusinessInfoRespDTO = new CustBusinessInfoRespDTO();
        BeanUtils.copyProperties(custBusinessInfo, custBusinessInfoRespDTO);
        attachInfoList.forEach(attachInfo -> {
            if (attachInfo.getType().equals(ImgTypeEnum.FACADE_PHOTO.getCode())) {
                custBusinessInfoRespDTO.setFacadePhotoUrl(attachInfo.getImgUrl());
                custBusinessInfoRespDTO.setFacadePhotoId(attachInfo.getId());
            }

            if (attachInfo.getType().equals(ImgTypeEnum.SHOP_PHOTO.getCode())) {
                custBusinessInfoRespDTO.setShopPhotoUrl(attachInfo.getImgUrl());
                custBusinessInfoRespDTO.setShopPhotoId(attachInfo.getId());
            }

            if (attachInfo.getType().equals(ImgTypeEnum.GOOD_PHOTO.getCode())) {
                custBusinessInfoRespDTO.setGoodsPhotoUrl(attachInfo.getImgUrl());
                custBusinessInfoRespDTO.setGoodsPhotoId(attachInfo.getId());
            }

            if (attachInfo.getType().equals(ImgTypeEnum.EVIDENC_PHOTO.getCode())) {
                custBusinessInfoRespDTO.setAuxiliaryPhotoUrl(attachInfo.getImgUrl());
                custBusinessInfoRespDTO.setAuxiliaryPhotoId(attachInfo.getId());
            }
        });
        return Result.ok(custBusinessInfoRespDTO);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> save(@RequestBody CustBusinessInfoRespDTO custBusinessInfo) {
        CustBusinessInfoEntity custBusinessInfoEntity = new CustBusinessInfoEntity();
        try {
            boolean flag = DateUtil.timeComparison(null,null, isTest);
            if(!flag){
                return Result.failed("0点到凌晨04点，不受理申请！");
            }
            BeanUtils.copyProperties(custBusinessInfo, custBusinessInfoEntity);
            custBusinessInfoService.save(custBusinessInfoEntity);
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
        facadeBusinessAttachInfo.setCustBusinessInfoId(custBusinessInfoEntity.getId());
        facadeBusinessAttachInfo.setImgUrl(custBusinessInfo.getFacadePhotoUrl());

        CustBusinessAttachInfoEntity shopBusinessAttachInfo = new CustBusinessAttachInfoEntity();
        shopBusinessAttachInfo.setType(ImgTypeEnum.SHOP_PHOTO.getCode());
        shopBusinessAttachInfo.setCustBusinessInfoId(custBusinessInfoEntity.getId());
        shopBusinessAttachInfo.setImgUrl(custBusinessInfo.getShopPhotoUrl());

        CustBusinessAttachInfoEntity goodsAttachInfo = new CustBusinessAttachInfoEntity();
        goodsAttachInfo.setType(ImgTypeEnum.GOOD_PHOTO.getCode());
        goodsAttachInfo.setCustBusinessInfoId(custBusinessInfoEntity.getId());
        goodsAttachInfo.setImgUrl(custBusinessInfo.getGoodsPhotoUrl());

        CustBusinessAttachInfoEntity evidenceAttachInfo = new CustBusinessAttachInfoEntity();
        evidenceAttachInfo.setType(ImgTypeEnum.EVIDENC_PHOTO.getCode());
        evidenceAttachInfo.setCustBusinessInfoId(custBusinessInfoEntity.getId());
        evidenceAttachInfo.setImgUrl(custBusinessInfo.getAuxiliaryPhotoUrl());

        businessAttachInfo.add(facadeBusinessAttachInfo);
        businessAttachInfo.add(shopBusinessAttachInfo);
        businessAttachInfo.add(goodsAttachInfo);
        businessAttachInfo.add(evidenceAttachInfo);
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
    public Result<?> update(@RequestBody CustBusinessInfoRespDTO custBusinessInfo) {
        try {
            CustBusinessInfoEntity custBusinessInfoEntity = new CustBusinessInfoEntity();
            BeanUtils.copyProperties(custBusinessInfo, custBusinessInfoEntity);
            boolean bool = custBusinessInfoService.updateById(custBusinessInfoEntity);

            if(StringUtils.isNotBlank(custBusinessInfo.getFacadePhotoUrl())){
                CustBusinessAttachInfoEntity custBusinessAttachInfoEntity = custBusinessAttachInfoService.getById(custBusinessInfo.getFacadePhotoId());
                if(custBusinessAttachInfoEntity !=null ){
                    custBusinessAttachInfoEntity.setImgUrl(custBusinessInfo.getFacadePhotoUrl());
                    custBusinessAttachInfoService.updateById(custBusinessAttachInfoEntity);
                }else{
                    custBusinessAttachInfoEntity = new CustBusinessAttachInfoEntity();
                    custBusinessAttachInfoEntity.setImgUrl(custBusinessInfo.getFacadePhotoUrl());
                    custBusinessAttachInfoEntity.setType(ImgTypeEnum.FACADE_PHOTO.getCode());
                    custBusinessAttachInfoEntity.setCustBusinessInfoId(custBusinessInfo.getId());
                    custBusinessAttachInfoService.save(custBusinessAttachInfoEntity);
                }
            }
            if(StringUtils.isNotBlank(custBusinessInfo.getShopPhotoUrl())){
                CustBusinessAttachInfoEntity custBusinessAttachInfoEntity = custBusinessAttachInfoService.getById(custBusinessInfo.getShopPhotoId());
                if(custBusinessAttachInfoEntity !=null ){
                    custBusinessAttachInfoEntity.setImgUrl(custBusinessInfo.getShopPhotoUrl());
                    custBusinessAttachInfoService.updateById(custBusinessAttachInfoEntity);
                }else{
                    custBusinessAttachInfoEntity = new CustBusinessAttachInfoEntity();
                    custBusinessAttachInfoEntity.setType(ImgTypeEnum.SHOP_PHOTO.getCode());
                    custBusinessAttachInfoEntity.setImgUrl(custBusinessInfo.getShopPhotoUrl());
                    custBusinessAttachInfoEntity.setCustBusinessInfoId(custBusinessInfo.getId());
                    custBusinessAttachInfoService.save(custBusinessAttachInfoEntity);
                }
            }

            if(StringUtils.isNotBlank(custBusinessInfo.getGoodsPhotoUrl())){
                CustBusinessAttachInfoEntity custBusinessAttachInfoEntity = custBusinessAttachInfoService.getById(custBusinessInfo.getGoodsPhotoId());
                if(custBusinessAttachInfoEntity !=null ){
                    custBusinessAttachInfoEntity.setImgUrl(custBusinessInfo.getGoodsPhotoUrl());
                    custBusinessAttachInfoService.updateById(custBusinessAttachInfoEntity);
                }else{
                    custBusinessAttachInfoEntity = new CustBusinessAttachInfoEntity();
                    custBusinessAttachInfoEntity.setType(ImgTypeEnum.GOOD_PHOTO.getCode());
                    custBusinessAttachInfoEntity.setImgUrl(custBusinessInfo.getGoodsPhotoUrl());
                    custBusinessAttachInfoEntity.setCustBusinessInfoId(custBusinessInfo.getId());
                    custBusinessAttachInfoService.save(custBusinessAttachInfoEntity);
                }
            }
            if(StringUtils.isNotBlank(custBusinessInfo.getAuxiliaryPhotoUrl())){
                CustBusinessAttachInfoEntity custBusinessAttachInfoEntity = custBusinessAttachInfoService.getById(custBusinessInfo.getAuxiliaryPhotoId());
                if(custBusinessAttachInfoEntity !=null ){
                    custBusinessAttachInfoEntity.setImgUrl(custBusinessInfo.getAuxiliaryPhotoUrl());
                    custBusinessAttachInfoService.updateById(custBusinessAttachInfoEntity);
                }else{
                    custBusinessAttachInfoEntity = new CustBusinessAttachInfoEntity();
                    custBusinessAttachInfoEntity.setType(ImgTypeEnum.EVIDENC_PHOTO.getCode());
                    custBusinessAttachInfoEntity.setImgUrl(custBusinessInfo.getAuxiliaryPhotoUrl());
                    custBusinessAttachInfoEntity.setCustBusinessInfoId(custBusinessInfo.getId());
                    custBusinessAttachInfoService.save(custBusinessAttachInfoEntity);
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
