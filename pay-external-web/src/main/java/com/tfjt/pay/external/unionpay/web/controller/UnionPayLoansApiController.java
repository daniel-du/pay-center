package com.tfjt.pay.external.unionpay.web.controller;

import com.alibaba.fastjson.JSON;
import com.tfjt.pay.external.unionpay.biz.LoanUserBizService;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansApiBizService;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansSettleAcctDTO;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import com.tfjt.pay.external.unionpay.utils.FileUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 银联-货款
 */
@Slf4j
@RestController
@RequestMapping("unionPayLoans")
public class UnionPayLoansApiController {

    @Resource
    private UnionPayLoansApiBizService unionPayLoansApiBizService;

    @Resource
    private LoanUserBizService loanUserBizService;

    /**
     * 进件
     *
     * @param id
     * @return
     */
    @PostMapping("incoming")
        public Result<?> incoming(Long id,String smsCode) {
        try {
            LoanUserEntity loanUserEntity = loanUserBizService.getById(id);
            if (null == loanUserEntity) {
                Result.failed(500, "贷款商户不存在");
            }
            unionPayLoansApiBizService.incoming(loanUserEntity,smsCode);
            return Result.ok(loanUserEntity);
        } catch (TfException e) {
            log.error("YinLianLoansApiController.incoming.err:{}", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("银联-货款-进件：param={}", JSON.toJSONString(id), e);
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 二级进件
     *
     * @param id
     * @return
     */
    @PostMapping("twoIncoming")
    public Result<?> twoIncoming(Long id,String smsCode) {
        try {
            LoanUserEntity LoanUserEntity = loanUserBizService.getById(id);
            if (null == LoanUserEntity) {
                Result.failed(500, "贷款商户不存在");
            }
            return Result.ok(unionPayLoansApiBizService.twoIncoming(LoanUserEntity,smsCode));
        } catch (TfException e) {
            log.error("YinLianLoansApiController.incoming.err:{}", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("银联-货款-二级进件：param={}", JSON.toJSONString(id), e);
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 进件-查询
     *
     * @param outRequestNo
     * @return
     */
    @GetMapping("getIncomingInfo")
    public Result<?> incomingInfo(String outRequestNo) {
        try {
            return Result.ok(unionPayLoansApiBizService.getIncomingInfo(outRequestNo));
        } catch (TfException e) {
            log.error("YinLianLoansApiController.getIncomingInfo.err:{}", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("银联-货款-进件-查询：param={}", JSON.toJSONString(outRequestNo), e);
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 二级进件-查询
     *
     * @param outRequestNo
     * @return
     */
    @GetMapping("getTwoIncomingInfo")
    public Result<?> getTwoIncomingInfo(String outRequestNo) {
        try {
            return Result.ok(unionPayLoansApiBizService.getTwoIncomingInfo(outRequestNo));
        } catch (TfException e) {
            log.error("YinLianLoansApiController.getTwoIncomingInfo.err:{}", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("银联-货款-二级进件-查询：param={}", JSON.toJSONString(outRequestNo), e);
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 个人手机号验证 SMS_CODES
     *
     * @param mobileNumber
     * @return
     */
    @GetMapping("validationMobileNumber")
    public Result<?> validationMobileNumber(String mobileNumber) {
        try {
            return Result.ok(unionPayLoansApiBizService.validationMobileNumber(mobileNumber));
        } catch (TfException e) {
            log.error("YinLianLoansApiController.validationMobileNumber.err:{}", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("银联-货款-个人手机号-验证：param={}", mobileNumber, e);
            return Result.failed(e.getMessage());
        }
    }


    /**
     * 进件-修改
     *
     * @param id
     * @return
     */
    @PostMapping("incomingEdit")
    public Result<?> incomingEdit(Long id, String smsCode) {
        try {
            LoanUserEntity LoanUserEntity = loanUserBizService.getById(id);
            if (null == LoanUserEntity) {
                Result.failed(500, "贷款商户不存在");
            }
            return Result.ok(unionPayLoansApiBizService.incomingEdit(LoanUserEntity, smsCode));
        } catch (TfException e) {
            log.error("YinLianLoansApiController.incoming.err:{}", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("银联-货款-进件：param={}", JSON.toJSONString(id), e);
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 二级进件-修改
     *
     * @param id
     * @return
     */
    @PostMapping("twoIncomingEdit")
    public Result<?> twoIncomingEdit(Long id, String smsCode) {
        try {
            LoanUserEntity LoanUserEntity = loanUserBizService.getById(id);
            if (null == LoanUserEntity) {
                Result.failed(500, "贷款商户不存在");
            }
            return Result.ok(unionPayLoansApiBizService.twoIncomingEdit(LoanUserEntity, smsCode));
        } catch (TfException e) {
            log.error("YinLianLoansApiController.incoming.err:{}", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("银联-货款-进件：param={}", JSON.toJSONString(id), e);
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 打款金额验证
     *
     * @return
     */
    @PostMapping("settleAcctsValidate")
    public Result<?> settleAcctsValidate(Long loanUserId, Integer payAmount) {
        try {
            return Result.ok(unionPayLoansApiBizService.settleAcctsValidate(loanUserId, payAmount));
        } catch (TfException e) {
            log.error("YinLianLoansApiController.settleAcctsValidate.err:{}", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("银联-货款-打款金额验证：param={}", loanUserId, e);
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 获取绑定账户编号
     *
     * @return
     */
    @GetMapping("getSettleAcctId")
    public Result<?> getSettleAcctId(Long loanUserId) {
        try {
            return Result.ok(unionPayLoansApiBizService.getSettleAcctId(loanUserId));
        } catch (TfException e) {
            log.error("YinLianLoansApiController.getSettleAcctId.err:{}", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("银联-货款-绑定账户编号查看：param={}", loanUserId, e);
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 绑定账户查询
     * 返回默认
     *
     * @param loanUserId
     * @return
     */
    @GetMapping("querySettleAcctByOutRequestNo")
    public Result<UnionPayLoansSettleAcctDTO> querySettleAcctByOutRequestNo(Long loanUserId) {
        try {

            LoanUserEntity LoanUserEntity = loanUserBizService.getById(loanUserId);
            if (StringUtils.isBlank(LoanUserEntity.getOutRequestNo())) {
                return Result.failed("平台订单号不能为空");
            }
            return Result.ok(unionPayLoansApiBizService.querySettleAcctByOutRequestNo(loanUserId, LoanUserEntity.getOutRequestNo()));
        } catch (TfException e) {
            log.error("YinLianLoansApiController.绑定账户查询.err:{}", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("银联-货款-绑定账户查询：param={}", loanUserId, e);
            return Result.failed(e.getMessage());
        }
    }


    /**
     * 上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam(value = "file") MultipartFile file) {

        try {
            return Result.ok(unionPayLoansApiBizService.upload(FileUtil.transferToFile(file)));
        } catch (TfException e) {
            log.error("YinLianLoansApiController.incomingInfo.err:{}", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("银联-货款-上传图片：param={}", e);
            return Result.failed(e.getMessage());
        }
    }


}
