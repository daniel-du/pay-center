# config.py

SQL_CONFIG = {
    'query': """
           select a.id,
               1                         as                           accessChannelType,
               a.access_main_type        as                           accessMainType,
               a.access_type             as                           accessType,
               a.member_id               as                           memberId,
               ts.id                     as                           businessId,
               1                         as                           businessType,
               case when a.id is null then 0 else a.access_status end accessStatus,
               a.account_no              as                           accountNo,
               b.legal_mobile            as                           legalMobile,
               b.agent_mobile            as                           agentMobile,
               c.`name`                  as                           legalName,
               c.id_no                   as                           legalIdNo,
               d.`name`                  as                           agentName,
               d.id_no                   as                           agentIdNo,
               f.business_name           as                           businessName,
               f.business_license_no     as                           businessLicenseNo,
               g.settlement_account_type as                           settlementAccountType,
               h.bank_account_name       as                           bankAccountName,
               h.bank_card_no            as                           bankCardNo,
               h.bank_card_mobile        as                           bankCardMobile,
               h.bank_name               as                           bankName,
               h.bank_sub_branch_name    as                           bankSubBranchName,
               h.bank_branch_code        as                           bankBranchCode,
               h.eicon_bank_branch       as                           eiconBankBranch
        from tf_supplier ts
                 left join tf_incoming_info a on ts.id = a.business_id and a.business_type = 1  and a.is_deleted = 0
                 left join tf_incoming_merchant_info b on b.incoming_id = a.id and b.is_deleted = 0
                 left join tf_idcard_info c on b.legal_id_card = c.id and c.is_deleted = 0
                 left join tf_idcard_info d on b.agent_id_card = d.id and d.is_deleted = 0
                 left join tf_incoming_business_info e on e.incoming_id = a.id and e.is_deleted = 0
                 left join tf_business_license_info f on e.business_license_id = f.id and f.is_deleted = 0
                 left join tf_incoming_settle_info g on g.incoming_id = a.id and g.is_deleted = 0
                 left join tf_bank_card_info h on g.bank_card_id = h.id and h.is_deleted = 0
        union all
        select a.id,
               1                         as                           accessChannelType,
               a.access_main_type        as                           accessMainType,
               a.access_type             as                           accessType,
               a.member_id               as                           memberId,
               fws.id                    as                           businessId,
               2                         as                           businessType,
               case when a.id is null then 0 else a.access_status end accessStatus,
               a.account_no              as                           accountNo,
               b.legal_mobile            as                           legalMobile,
               b.agent_mobile            as                           agentMobile,
               c.`name`                  as                           legalName,
               c.id_no                   as                           legalIdNo,
               d.`name`                  as                           agentName,
               d.id_no                   as                           agentIdNo,
               f.business_name           as                           businessName,
               f.business_license_no     as                           businessLicenseNo,
               g.settlement_account_type as                           settlementAccountType,
               h.bank_account_name       as                           bankAccountName,
               h.bank_card_no            as                           bankCardNo,
               h.bank_card_mobile        as                           bankCardMobile,
               h.bank_name               as                           bankName,
               h.bank_sub_branch_name    as                           bankSubBranchName,
               h.bank_branch_code        as                           bankBranchCode,
               h.eicon_bank_branch         as                           eiconBankBranch
        from fa_wanlshop_shop fws
                 left join tf_incoming_info a on fws.id = a.business_id and a.business_type = 2 and a.is_deleted = 0
                 left join tf_incoming_merchant_info b on b.incoming_id = a.id and b.is_deleted = 0
                 left join tf_idcard_info c on b.legal_id_card = c.id and c.is_deleted = 0
                 left join tf_idcard_info d on b.agent_id_card = d.id and d.is_deleted = 0
                 left join tf_incoming_business_info e on e.incoming_id = a.id and e.is_deleted = 0
                 left join tf_business_license_info f on e.business_license_id = f.id and f.is_deleted = 0
                 left join tf_incoming_settle_info g on g.incoming_id = a.id and g.is_deleted = 0
                 left join tf_bank_card_info h on g.bank_card_id = h.id and h.is_deleted = 0
 order by businessId,businessType desc
    """,
    'page_size': 100000
}

## 银联渠道
# SQL_CONFIG = {
#     'query': """
# select 2 as access_channel_type, ts.id business_id,1 as business_type,
# case when tss.id is null then '-1' else tss.signing_status end unionpay_sign_status,
# tss.mid as account_no,tss.business_no as account_no2
# from tf_supplier ts left join tf_self_sign tss on ts.supplier_id  = tss.accesser_acct
# union all
# select 2 as access_channel_type, fws.id business_id,2 as business_type,
# case when tss.id is null then '-1' else tss.signing_status end unionpay_sign_status,
# tss.mid as account_no,tss.business_no as account_no2
# from fa_wanlshop_shop fws left join tf_self_sign tss on CONCAT('tfys', fws.id) = tss.accesser_acct
#     """,
#     'page_size': 100000
# }
