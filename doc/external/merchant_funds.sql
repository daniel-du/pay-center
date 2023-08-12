alter table tf_cust_bank_info
    add deleted tinyint default 0 not null comment '标记删除';