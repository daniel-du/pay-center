alter table tf_incoming_info
    add source int null comment '1 福商通 2 福战通' after business_type;
