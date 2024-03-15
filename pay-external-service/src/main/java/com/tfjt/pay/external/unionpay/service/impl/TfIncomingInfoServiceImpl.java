package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.api.dto.req.BusinessBasicInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingModuleStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.dao.TfIncomingInfoDao;
import com.tfjt.pay.external.unionpay.dto.BusinessIsIncomingRespDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingDataIdDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.tfjt.pay.external.unionpay.enums.DeleteStatusEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessStatusEnum;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 入网信息 服务实现类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
@Service
public class TfIncomingInfoServiceImpl extends BaseServiceImpl<TfIncomingInfoDao, TfIncomingInfoEntity> implements TfIncomingInfoService {


    @Override
    public TfIncomingInfoEntity queryIncomingInfoById(Long id) {
        return this.baseMapper.selectById(id);
    }

    /**
     * 根据id查询进件提交所需信息
     * @param id
     * @return IncomingSubmitMessageDTO
     */
    @Override
    public IncomingSubmitMessageDTO queryIncomingMessage(Long id) {
        return this.baseMapper.queryIncomingMessage(id);
    }

    /**
     * 根据商户信息查询进件信息
     * @param incomingMessageReqDTO
     * @return
     */
    @Override
    public IncomingMessageRespDTO queryIncomingMessageByMerchant(IncomingMessageReqDTO incomingMessageReqDTO) {
        return this.baseMapper.queryIncomingMessageByMerchant(incomingMessageReqDTO);
    }

    /**
     * 根据多个商户信息批量查询进件信息
     * @param incomingMessageReqs
     * @return
     */
    @Override
    public List<IncomingMessageRespDTO> queryIncomingMessagesByMerchantList(List<IncomingMessageReqDTO> incomingMessageReqs) {
        return this.baseMapper.queryIncomingMessagesByMerchantList(incomingMessageReqs);
    }

    /**
     * 根据进件id查询进件相关信息表id
     * @param id
     * @return
     */
    @Override
    public IncomingDataIdDTO queryIncomingDataId(Long id) {
        return this.baseMapper.queryIncomingDataId(id);
    }

    /**
     * 根据商户类型、商户id、查询进件主表信息
     * @param incomingModuleStatusReqDTO
     * @return
     */
    @Override
    public TfIncomingInfoEntity queryIncomingInfoByMerchant(IncomingModuleStatusReqDTO incomingModuleStatusReqDTO) {
        LambdaQueryWrapper<TfIncomingInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TfIncomingInfoEntity::getAccessChannelType, incomingModuleStatusReqDTO.getAccessChannelType());
        queryWrapper.eq(TfIncomingInfoEntity::getBusinessType, incomingModuleStatusReqDTO.getBusinessType());
        queryWrapper.eq(TfIncomingInfoEntity::getBusinessId, incomingModuleStatusReqDTO.getBusinessId());
        queryWrapper.eq(TfIncomingInfoEntity::getIsDeleted, DeleteStatusEnum.NO.getCode());
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据商户类型、商户id、进件渠道查询进件主表是否存在
     * @param businessId
     * @param businessType
     * @param accessChannelType
     * @return
     */
    @Override
    public Long queryIncomingInfoCountByMerchant(Long businessId, Byte businessType, Byte accessChannelType) {
        LambdaQueryWrapper<TfIncomingInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TfIncomingInfoEntity::getAccessChannelType, accessChannelType);
        queryWrapper.eq(TfIncomingInfoEntity::getBusinessType, businessType);
        queryWrapper.eq(TfIncomingInfoEntity::getBusinessId, businessId);
        queryWrapper.eq(TfIncomingInfoEntity::getIsDeleted, DeleteStatusEnum.NO.getCode());
        return this.baseMapper.selectCount(queryWrapper);
    }

    @Override
    public TfIncomingInfoEntity queryNotSubmitMinIdData() {
        LambdaQueryWrapper<TfIncomingInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TfIncomingInfoEntity::getAccessStatus, IncomingAccessStatusEnum.IMPORTS_CLOSURE);
        queryWrapper.orderByAsc(TfIncomingInfoEntity::getId).last("limit 1");
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<TfIncomingInfoEntity> queryListByStartId(Long id) {
        LambdaQueryWrapper<TfIncomingInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TfIncomingInfoEntity::getAccessStatus, IncomingAccessStatusEnum.IMPORTS_CLOSURE);
        queryWrapper.ge(TfIncomingInfoEntity::getId, id);
        queryWrapper.orderByAsc(TfIncomingInfoEntity::getId).last("limit 100");
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int updateTimeById(Long id) {
        TfIncomingInfoEntity tfIncomingInfoEntity = new TfIncomingInfoEntity();
        tfIncomingInfoEntity.setId(id);
        tfIncomingInfoEntity.setUpdateTime(LocalDateTime.now());
        return this.baseMapper.updateById(tfIncomingInfoEntity);
    }

    @Override
    public List<TfIncomingInfoEntity> queryListByBusinessIdAndType(List<Long> businessIds, Integer businessType) {
        LambdaQueryWrapper<TfIncomingInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TfIncomingInfoEntity::getBusinessType, businessType);
        queryWrapper.eq(TfIncomingInfoEntity::getIsDeleted, DeleteStatusEnum.NO.getCode());
        queryWrapper.in(TfIncomingInfoEntity::getBusinessId, businessIds);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<BusinessIsIncomingRespDTO> isIncomingByBusinessIdAndType(List<BusinessBasicInfoReqDTO> dtos) {
        LambdaQueryWrapper<TfIncomingInfoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfIncomingInfoEntity::getIsDeleted,DeleteStatusEnum.NO.getCode());
        String sql = "(";
        for (BusinessBasicInfoReqDTO dto : dtos) {
            sql+="("+dto.getBusinessId()+","+dto.getBusinessType()+"),";
        }
        sql = sql.substring(0,sql.length()-1);
        sql+=")";
        wrapper.apply("( business_id, business_type ) IN "+sql);
        return super.list(BusinessIsIncomingRespDTO.class,wrapper);
    }
}
