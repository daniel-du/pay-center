alter table tf_cust_bank_info
    add deleted tinyint default 0 not null comment '标记删除';
-- 提现回调
INSERT INTO tf_pay.tf_pay_application_callback_url ( url, app_id, type) VALUES ( 'https://test.tftest.tfzhongchukeji.com/api/agency/callback/callBackLoan', '349c34ff', 74);
