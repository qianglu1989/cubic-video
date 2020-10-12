#######

CREATE TABLE `video_information`
(
    `id`            int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `service_id`    varchar(100)     NOT NULL COMMENT '业务唯一标识',
    `org_file_name` varchar(100) DEFAULT NULL COMMENT '原文件名称',
    `file_name`     varchar(100) DEFAULT NULL COMMENT '文件名称',
    `file_url`      varchar(200) DEFAULT NULL COMMENT '资源地址',
    `data`          varchar(500) DEFAULT NULL COMMENT '业务参数',
    `create_date`   datetime     DEFAULT NULL COMMENT '创建时间',
    `modify_date`   datetime     DEFAULT NULL COMMENT '修改时间',
    `type`          varchar(20)  DEFAULT NULL COMMENT '类型',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_service_id` (`service_id`) USING BTREE,
    KEY `idx_create_date` (`create_date`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 379
  DEFAULT CHARSET = utf8 COMMENT ='视频流基础信息表';


CREATE TABLE `video_channels`
(
    `id`             int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '唯一主键',
    `remote_address` varchar(100) DEFAULT NULL COMMENT '远程地址',
    `server_address` varchar(100) DEFAULT NULL COMMENT '服务地址',
    `file_name`      varchar(100) DEFAULT NULL COMMENT '文件名称',
    `state`          int(11)      DEFAULT NULL COMMENT '状态',
    `data`           varchar(100) DEFAULT NULL COMMENT '业务数据',
    `create_date`    datetime     DEFAULT NULL COMMENT '创建时间',
    `modify_date`    datetime     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `channel_id`     varchar(100) DEFAULT NULL COMMENT '通道ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_channel_id` (`channel_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 116
  DEFAULT CHARSET = utf8 COMMENT ='连接数据表';



