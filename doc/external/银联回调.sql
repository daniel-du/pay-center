alter table tf_self_sign
    add mer_ms_relation char default 0 null comment '主从关系绑定结果 “0”:绑定失败” “1”:”绑定成功',
    add sign_success_date datetime null comment '入网成功时间';

