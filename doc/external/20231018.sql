alter table tf_loan_user
    modify id bigint auto_increment,
    modify cus_application_id bigint null comment '系统订单号';


-- 添加同福云商母账户信息
INSERT INTO tf_pay.tf_loan_user (id, type, loan_user_type, bus_id, application_status, cus_id, succeeded_at, failed_at,
                                 failure_msgs, failure_msgs_param, failure_msgs_reason, out_request_no,
                                 cus_application_id, creator, create_date, updater, update_date, name, settle_acct_id,
                                 bind_acct_name, mch_id, mch_application_id, audited_at, bank_call_status)
VALUES (-1, 2, 1, '0', 'succeeded', null, null, null, null, null,
        null, null, '1008791971176287241', 'system', now(), null,
        now(), '河北同福云商科技有限公司', null, null, null, null, null, 0);
-- 测试环境
INSERT INTO tf_pay.tf_cust_bank_info (front_bank_card_url, account_name, bank_card_no, phone, province,
                                      province_name, city, city_name, bank_branch_code, bank_code, bank_name,
                                      big_bank_name, sms_code, loan_user_id, settlement_type, creator, create_date,
                                      updater, update_date, career, verify_status, deleted)
VALUES (null, '河北同福云商科技有限公司', '6221320406254979', '18737979501', '11', null, '110101', null,
        '313335081005', '313335081005', null, null, '999999', -1, 2, null,
        now(), null, now(), '10200', null, 0);
