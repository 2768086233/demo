-- ============================================
-- 药品有效期管理系统 - 数据库初始化脚本
-- 数据库：MySQL 8.0
-- ============================================

CREATE DATABASE IF NOT EXISTS medicine_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE medicine_db;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id          BIGINT       NOT NULL PRIMARY KEY COMMENT '主键',
    openid      VARCHAR(64)  NOT NULL COMMENT '微信openid',
    nickname    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    avatar_url  VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 药品表
-- ----------------------------
DROP TABLE IF EXISTS medicine;
CREATE TABLE medicine (
    id            BIGINT       NOT NULL PRIMARY KEY COMMENT '主键',
    user_id       BIGINT       NOT NULL COMMENT '所属用户ID',
    name          VARCHAR(128) NOT NULL COMMENT '药品名称',
    generic_name  VARCHAR(128) DEFAULT NULL COMMENT '通用名',
    batch_number  VARCHAR(64)  DEFAULT NULL COMMENT '生产批号',
    produce_date  DATE         DEFAULT NULL COMMENT '生产日期',
    expiry_date   DATE         DEFAULT NULL COMMENT '有效期至',
    quantity      INT          NOT NULL DEFAULT 1 COMMENT '剩余数量',
    unit          VARCHAR(20)  DEFAULT NULL COMMENT '单位（盒/瓶/片/支）',
    location      VARCHAR(64)  DEFAULT NULL COMMENT '存放位置',
    manufacturer  VARCHAR(128) DEFAULT NULL COMMENT '厂商',
    category      VARCHAR(32)  DEFAULT NULL COMMENT '分类标签',
    remark        VARCHAR(255) DEFAULT NULL COMMENT '备注',
    images        TEXT         DEFAULT NULL COMMENT '照片URL，JSON数组',
    status        TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-正常 1-临期 2-过期 3-已用完',
    is_deleted    TINYINT      NOT NULL DEFAULT 0 COMMENT '软删除：0-未删 1-已删',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_user_id (user_id),
    KEY idx_expiry_date (expiry_date),
    KEY idx_status (status),
    KEY idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品表';

-- ----------------------------
-- 3. 药品使用记录表
-- ----------------------------
DROP TABLE IF EXISTS medicine_usage_log;
CREATE TABLE medicine_usage_log (
    id               BIGINT   NOT NULL PRIMARY KEY COMMENT '主键',
    medicine_id      BIGINT   NOT NULL COMMENT '药品ID',
    user_id          BIGINT   NOT NULL COMMENT '操作人',
    change_type      TINYINT  NOT NULL COMMENT '变更类型：1-增加 2-减少 3-删除',
    quantity_change  INT      NOT NULL COMMENT '变更数量（正负）',
    before_quantity  INT      NOT NULL COMMENT '变更前数量',
    after_quantity   INT      NOT NULL COMMENT '变更后数量',
    remark           VARCHAR(255) DEFAULT NULL COMMENT '操作备注',
    create_time      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    KEY idx_medicine_id (medicine_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品使用记录表';

-- ----------------------------
-- 4. 消息推送记录表
-- ----------------------------
DROP TABLE IF EXISTS notification_log;
CREATE TABLE notification_log (
    id            BIGINT   NOT NULL PRIMARY KEY COMMENT '主键',
    user_id       BIGINT   NOT NULL COMMENT '用户ID',
    medicine_id   BIGINT   NOT NULL COMMENT '药品ID',
    content       VARCHAR(255) DEFAULT NULL COMMENT '消息内容',
    type          TINYINT  NOT NULL COMMENT '类型：1-临期提醒 2-过期提醒 3-补充提醒',
    send_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '推送时间',
    is_read       TINYINT  NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_user_id (user_id),
    KEY idx_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息推送记录表';
