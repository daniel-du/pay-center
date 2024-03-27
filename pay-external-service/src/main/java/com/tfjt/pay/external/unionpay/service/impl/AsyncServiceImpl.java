package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.fms.data.insight.api.service.SupplierApiService;
import com.tfjt.pay.external.unionpay.api.dto.req.BusinessBasicInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryAccessBankStatueRespDTO;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dto.BusinessIsIncomingRespDTO;
import com.tfjt.pay.external.unionpay.entity.SalesAreaIncomingChannelEntity;
import com.tfjt.pay.external.unionpay.entity.SelfSignEntity;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.tfjt.pay.external.unionpay.enums.*;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.robot.common.message.ding.MarkdownMessage;
import com.tfjt.robot.service.dingtalk.DingRobotService;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author zxy
 * @create 2024/1/16 9:49
 */
@Service
@Slf4j
public class AsyncServiceImpl implements AsyncService {

    @Autowired
    private SelfSignService selfSignService;
    @Autowired
    private TfIncomingInfoService tfIncomingInfoService;
    @Autowired
    private SalesAreaIncomingChannelService salesAreaIncomingChannelService;
    @Autowired
    private DingRobotService dingRobotService;
    @Autowired
    private FaStandardLocationDictService faStandardLocationDictService;

    @DubboReference(retries = 0)
    private SupplierApiService supplierApiService;


    @Value("${dingding.incoming.accessToken}")
    private String accessToken;
    @Value("${dingding.incoming.encryptKey}")
    private String encryptKey;
    @Value("${dingding.incoming.sleep}")
    private Integer sleepTime;


    @Override
    @Async
    public void dingWarning(Long supplierId, List<Integer> newIdentifyList, List<String> newSaleAreas, Boolean saleFlag, Boolean identityFlag, List<String> oldSaleAreas, List<Integer> oldIdentifyList) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //查询出当前用户的进件信息，如果当前用户既在银联进件了又在平安进件了，则不会触发钉钉报警。
        //查询银联进件信息
        //银联进件查询
        QueryAccessBankStatueRespDTO unionIncoming = getUnionIncoming(null, String.valueOf(supplierId));
        //平安进件查询
        QueryAccessBankStatueRespDTO pabcIncoming = getPabcIncoming(String.valueOf(supplierId));
        //判断用户身份，供应商必须要银联和平安都进件，经销商需要根据销售区域进行判断
        //供应商钉钉报警不考虑销售区域变更
        Result<String> result = supplierApiService.getSupplierNameBySupplierId(supplierId);
        String supplierName = null;
        int code = result.getCode();
        if (code == NumberConstant.ZERO) {
            supplierName = result.getData();
        }
        if (newIdentifyList.contains(SupplierTypeEnum.SUPPLIER.getCode())) {
            log.info("身份信息包含供应商");
            //身份变更
            if (!identityFlag) {
                log.info("供应商身份发生了变更");
                String title = "商户身份变更";
                String oldIdentity = getIdentityByCode(oldIdentifyList);
                String newIdentity = getIdentityByCode(newIdentifyList);
                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append("时间：" + DateUtil.format(new Date(), DatePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()) + "\n\n");
                msgBuilder.append("商户名称:"+supplierName+"\n\n");
                msgBuilder.append("商户ID:"+supplierId+"\n\n");
                if (StringUtils.isNotBlank(oldIdentity)) {
                    msgBuilder.append("变更内容:商户身份由"+oldIdentity+"变更为"+newIdentity+"，请尽快【<font color=#FF0000>%s</font>】进件。");
                }else {
                    msgBuilder.append("变更内容:新增"+newIdentity+"，请尽快【<font color=#FF0000>%s</font>】进件。");
                }
                if (IncomingStatusEnum.NOT_INCOMING.getCode().equals(unionIncoming.getStatus())) {
                    String msg = msgBuilder.toString();
                    msg = String.format(msg, "银联");
                    sendMessage(title,msg);
                }
                if (IncomingStatusEnum.NOT_INCOMING.getCode().equals(pabcIncoming.getStatus())) {
                    String msg = msgBuilder.toString();
                    msg = String.format(msg, "平安");
                    sendMessage(title,msg);
                }
            }
        }
        if (newIdentifyList.contains(SupplierTypeEnum.DEALER.getCode())) {
            log.info("身份信息包含经销商");
            //判断经销商的销售区域是否包含新城
            //销售区域变更
            if (!saleFlag) {
                log.info("经销商销售区域发生了变更");
                //查询新城地区code集合
                String oldSales = getSalesByCodes(oldSaleAreas);
                String newSales = getSalesByCodes(newSaleAreas);
                List<SalesAreaIncomingChannelEntity> list = salesAreaIncomingChannelService.list();
                List<String> pabcDistrictsCode = list.stream().map(SalesAreaIncomingChannelEntity::getDistrictsCode).collect(Collectors.toList());
                //判断新城地区code集合是否包含当前销售城市code
                boolean newCityFlag = pabcDistrictsCode.stream().anyMatch(e -> contains(newSaleAreas, e));
                log.info("经销商销售区域是否包含了平安地区：{},平安进件状态：{}",newCityFlag,pabcIncoming.getStatus());
                String title = "销售区域变更";
                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append("时间：" + DateUtil.format(new Date(), DatePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()) + "\n\n");
                msgBuilder.append("商户名称:"+supplierName+"\n\n");
                msgBuilder.append("商户ID:"+supplierId+"\n\n");
                msgBuilder.append("变更内容：\n\n老销售区域:"+oldSales+";\n\n新销售区域:"+newSales+"，请尽快【<font color=#FF0000>%s</font>】进件。");
                if (newCityFlag && IncomingStatusEnum.NOT_INCOMING.getCode().equals(pabcIncoming.getStatus())) {
                    String msg = msgBuilder.toString();
                    log.info("平安进件报警，报警信息为：{}",msg);
                    msg = String.format(msg, "平安");
                    sendMessage(title,msg);
                }
                //
                //此时用户还在旧城
                newSaleAreas.removeAll(pabcDistrictsCode);
                if (CollectionUtil.isNotEmpty(newSaleAreas) && IncomingStatusEnum.NOT_INCOMING.getCode().equals(unionIncoming.getStatus())){
                    String msg = msgBuilder.toString();
                    log.info("银联进件报警，报警信息为：{}",msg);
                    msg = String.format(msg, "银联");
                    sendMessage(title,msg);
                }
            }
        }

    }

    @Override
    @Async
    public void dingWarning(List<BusinessBasicInfoReqDTO> dtos, List<BusinessIsIncomingRespDTO> businessList) {
        List<BusinessBasicInfoReqDTO> warningList = new ArrayList<>();
        if (CollectionUtil.isEmpty(businessList)) {
            warningList = dtos;
        }else {
            Map<String, BusinessIsIncomingRespDTO> map = new HashMap<>();
            for (BusinessIsIncomingRespDTO businessIsIncomingRespDTO : businessList) {
                if (businessIsIncomingRespDTO.getBusinessType() != null) {
                    map.put(businessIsIncomingRespDTO.getBusinessId()+"-"+businessIsIncomingRespDTO.getBusinessType(),businessIsIncomingRespDTO);
                }
            }

            for (BusinessBasicInfoReqDTO dto : dtos) {
                String key = dto.getBusinessId()+"-"+dto.getBusinessType();
                BusinessIsIncomingRespDTO businessIsIncomingRespDTO = map.get(key);
                if (null == businessIsIncomingRespDTO) {
                    warningList.add(dto);
                }else {
                    if (StringUtils.isBlank(businessIsIncomingRespDTO.getAccountNo())) {
                        warningList.add(dto);
                    }
                }
            }

        }
        if (CollectionUtil.isNotEmpty(warningList)) {
            //发送钉钉
            String title = "未入网通知";
            String msg = "";
            for (BusinessBasicInfoReqDTO dto : warningList) {
                if (dto.getBusinessType() == 1) {
                    msg = "商户身份为：经销商或供应商的商户，ID为【"+dto.getBusinessId()+"】的商户还未入网";
                }
                if (dto.getBusinessType() == 2) {
                    msg = "商户身份为：云商，ID为【"+dto.getBusinessId()+"】的商户还未入网";
                }
                log.info(msg);
                sendMessage(title,msg);
            }
        }
    }

    @Async("incomingDingleAsyncExecutor")
    @Override
    public void dingWarningNew(List<BusinessBasicInfoReqDTO> dtos, Map<String, BusinessIsIncomingRespDTO> map) {
        List<BusinessBasicInfoReqDTO> warningList = new ArrayList<>();
        if (CollectionUtil.isEmpty(map)) {
            warningList = dtos;
        }else {
//            Map<String, BusinessIsIncomingRespDTO> map = new HashMap<>();
//            for (BusinessIsIncomingRespDTO businessIsIncomingRespDTO : businessList) {
//                if (businessIsIncomingRespDTO.getBusinessType() != null) {
//                    map.put(businessIsIncomingRespDTO.getBusinessId()+"-"+businessIsIncomingRespDTO.getBusinessType(),businessIsIncomingRespDTO);
//                }
//            }

            for (BusinessBasicInfoReqDTO dto : dtos) {
                String key = dto.getBusinessId()+"-"+dto.getBusinessType();
                BusinessIsIncomingRespDTO businessIsIncomingRespDTO = map.get(key);
                if (null == businessIsIncomingRespDTO) {
                    warningList.add(dto);
                }else {
                    if (StringUtils.isBlank(businessIsIncomingRespDTO.getAccountNo())) {
                        warningList.add(dto);
                    }
                }
            }

        }
        if (CollectionUtil.isNotEmpty(warningList)) {
            //发送钉钉
            String title = "未入网通知";
            String msg = "";
            for (BusinessBasicInfoReqDTO dto : warningList) {
                if (dto.getBusinessType() == 1) {
                    msg = "商户身份为：经销商或供应商的商户，ID为【"+dto.getBusinessId()+"】的商户还未入网";
                }
                if (dto.getBusinessType() == 2) {
                    msg = "商户身份为：云商，ID为【"+dto.getBusinessId()+"】的商户还未入网";
                }
                log.info(msg);
                sendMessage(title,msg);
            }
        }
    }


    private String getIdentityByCode(List<Integer> identifyList) {
        if (CollectionUtil.isNotEmpty(identifyList)){
            StringBuilder sb = new StringBuilder();
            for (Integer identity : identifyList) {
                String msgByCode = getMsgByCode(identity);
                sb.append(msgByCode);
                sb.append(",");
            }
            String identityStr = sb.toString();
            return identityStr.substring(0, identityStr.length() - 1);
        }
        return null;
    }

    private String getMsgByCode(Integer code){
        for (SupplierTypeEnum value : SupplierTypeEnum.values()) {
            if (value.getCode().equals(code)){
                return value.getDesc();
            }
        }
        return "";
    }

    private String getSalesByCodes(List<String> saleAreas) {
        if (CollectionUtil.isNotEmpty(saleAreas)) {
            List<String> areas = faStandardLocationDictService.getAreasByCode(saleAreas);
            if (CollectionUtil.isNotEmpty(areas)) {
                return String.join(",", areas);
            }
        }
        return null;
    }


    private void sendMessage(String title,String msg) {

        MarkdownMessage message = MarkdownMessage.buildBizFree(title, msg);
        dingRobotService.send(message, accessToken, true, encryptKey);
    }

    private boolean contains(List<String> cus, String value){
        return cus.stream().filter(f -> f.equals(value)).findAny().isPresent();
    }





    /**
     * 查询银联进件信息
     *
     * @param businessType 经销商/供应商id/店铺id
     * @param businessId   判断查询参数是否为空
     * @return
     */
    private QueryAccessBankStatueRespDTO getUnionIncoming(Integer businessType, String businessId) {
        QueryAccessBankStatueRespDTO queryAccessBankStatueRespDTO = new QueryAccessBankStatueRespDTO();
        String buisnessNo = businessId;
        if (BusinessUserTypeEnum.BUSINESS.getCode().equals(businessType)) {
            buisnessNo = "tfys" + businessId;
        }else {
            Result<String> result = supplierApiService.getSupplierIdById(businessId);
            if (result.getCode()==NumberConstant.ZERO){
                buisnessNo = result.getData();
            }
        }
        SelfSignEntity one = selfSignService.getOne(new LambdaQueryWrapper<SelfSignEntity>().eq(SelfSignEntity::getAccesserAcct, buisnessNo));
        List<String> notSigningStatus = new ArrayList<>();
        notSigningStatus.add("-1");
        notSigningStatus.add("00");
        notSigningStatus.add("18");
        if (null != one && !notSigningStatus.contains(one.getSigningStatus())) {
            queryAccessBankStatueRespDTO.setStatus(one.getSigningStatus());
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.UNIONPAY.getName());
            queryAccessBankStatueRespDTO.setMsg(one.getMsg());
        } else {
            queryAccessBankStatueRespDTO.setStatus(String.valueOf(IncomingStatusEnum.NOT_INCOMING.getCode()));
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.UNIONPAY.getName());
        }
        return queryAccessBankStatueRespDTO;
    }

    /**
     * 查询平安进件信息
     *
     * @param businessId 经销商/供应商id/店铺id
     * @return
     */
    private QueryAccessBankStatueRespDTO getPabcIncoming(String businessId) {
        QueryAccessBankStatueRespDTO queryAccessBankStatueRespDTO = new QueryAccessBankStatueRespDTO();
        TfIncomingInfoEntity one = tfIncomingInfoService.getOne(new LambdaQueryWrapper<TfIncomingInfoEntity>().eq(TfIncomingInfoEntity::getBusinessId, businessId).eq(TfIncomingInfoEntity::getAccessChannelType, IncomingAccessChannelTypeEnum.PINGAN.getCode()).eq(TfIncomingInfoEntity::getAccessType, IncomingAccessTypeEnum.COMMON.getCode()));
        if (null != one) {
            String code = PabcUnionNetworkStatusMappingEnum.getMsg(one.getAccessStatus());
            queryAccessBankStatueRespDTO.setStatus(code);
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.PINGAN.getName());
            queryAccessBankStatueRespDTO.setMsg(one.getFailReason());
        } else {
            queryAccessBankStatueRespDTO.setStatus(String.valueOf(IncomingStatusEnum.NOT_INCOMING.getCode()));
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.PINGAN.getName());
        }
        return queryAccessBankStatueRespDTO;
    }


}
