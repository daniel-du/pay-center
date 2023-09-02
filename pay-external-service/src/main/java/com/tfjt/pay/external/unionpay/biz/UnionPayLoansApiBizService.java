package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.IncomingReturn;
import com.tfjt.pay.external.unionpay.dto.ReqDeleteSettleAcctParams;
import com.tfjt.pay.external.unionpay.dto.SettleAcctsMxDTO;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansSettleAcctDTO;
import com.tfjt.pay.external.unionpay.entity.CustBankInfoEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;

import java.io.File;

/**
 * @author zxy
 * @Date 2023/09/02 10:49
 */
public interface UnionPayLoansApiBizService {

    /**
     * 进件接口
     * @param tfLoanUserEntity
     * @return
     */
    LoanUserEntity incoming(LoanUserEntity tfLoanUserEntity, String smsCode);


    /**
     * 查询进件接口
     * @param outRequestNo
     * @return
     */
    IncomingReturn getIncomingInfo(String outRequestNo);

    /**
     * 进件修改接口
     * @param tfLoanUserEntity
     */
    IncomingReturn incomingEdit(LoanUserEntity tfLoanUserEntity, String smsCode);

    /**
     * 图片上传接口
     * @param file
     * @return
     */
    String upload(File file);

    /**
     * 新增绑定账户
     * @param custBankInfoEntity
     * @return
     */
    UnionPayLoansSettleAcctDTO bindAddSettleAcct(CustBankInfoEntity custBankInfoEntity);

    SettleAcctsMxDTO querySettleAcct(Integer id);

    UnionPayLoansSettleAcctDTO querySettleAcctByOutRequestNo(Long loanUserId, String outRequestNo);

    void deleteSettleAcct(ReqDeleteSettleAcctParams deleteSettleAcctParams);

    /**
     * 个人手机号验证
     * @param mobileNumber
     * @return
     */
    String validationMobileNumber(String mobileNumber);

    /**
     * 删除并绑定银行卡号
     * @param custBankInfo
     */
    UnionPayLoansSettleAcctDTO delAndBindAddSettleAcct(CustBankInfoEntity custBankInfo, String oldBankCardNo);

    /**
     * 二级进件
     * @param tfLoanUserEntity
     * @return
     */
    IncomingReturn twoIncoming(LoanUserEntity tfLoanUserEntity,String smsCode);

    /**
     * 二级进件查询
     * @param outRequestNo
     * @return
     */
    IncomingReturn getTwoIncomingInfo(String outRequestNo);

    /**
     * 二级进件修改
     * @param tfLoanUserEntity
     * @param smsCode
     * @return
     */
    IncomingReturn twoIncomingEdit(LoanUserEntity tfLoanUserEntity, String smsCode);

    /**
     * 打款金额验证
     * @param id
     * @param payAmount
     * @return
     */
    UnionPayLoansSettleAcctDTO settleAcctsValidate(Long loanUserId, Integer payAmount);

    /**
     * 获取绑定账户编号
     * @param id
     * @return
     */
    String getSettleAcctId(Long loanUserId);

    Boolean getMobileStatus(String mobile);
}
