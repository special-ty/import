-- ================================================================
-- importSystem 演示项目初始化脚本（MySQL）
-- 表结构采集自 GaussDB data_center_db 真实库（2026-10-10 JDBC 探针）
-- 索引与原表一致；sys_org 按导入接口最小依赖裁剪（原表 29 列，此处保留 3 列）
-- ================================================================

CREATE DATABASE IF NOT EXISTS import_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE import_demo;

-- ----------------------------------------------------------------
-- 1. 业务系统管理（对应原表 dc_information_system_manage，13 列全保留）
-- ----------------------------------------------------------------
DROP TABLE IF EXISTS dc_information_system_manage;
CREATE TABLE dc_information_system_manage (
    id                     varchar(36)   NOT NULL COMMENT '主键id',
    system_name            varchar(255)  DEFAULT NULL COMMENT '信息系统名称',
    system_state           varchar(10)   DEFAULT NULL COMMENT '信息系统状态,字典:system_state',
    deployment_method      varchar(10)   DEFAULT NULL COMMENT '部署方式,字典:deployment_method',
    current_affiliated_unit_id varchar(36) DEFAULT NULL COMMENT '当前所属单位id',
    implementation_unit_id varchar(36)   DEFAULT NULL COMMENT '实施单位id',
    network_access_type    varchar(10)   DEFAULT NULL COMMENT '接入网络类型,字典:network_access_type',
    function_description   varchar(2000) DEFAULT NULL COMMENT '功能描述',
    status                 varchar(10)   DEFAULT NULL COMMENT '状态',
    creator                varchar(36)   DEFAULT NULL COMMENT '创建人',
    create_time            timestamp     NULL DEFAULT NULL COMMENT '创建时间',
    update_user            varchar(36)   DEFAULT NULL COMMENT '更新人',
    update_time            timestamp     NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_system_status (status),
    KEY idx_system_state (system_state),
    KEY idx_network_access_type (network_access_type),
    KEY idx_implementation_unit_id (implementation_unit_id),
    KEY idx_current_affiliated_unit_id (current_affiliated_unit_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '业务系统管理';

-- ----------------------------------------------------------------
-- 2. 单位表 sys_org（原表 29 列；导入接口只依赖 id/name/status，裁剪保留）
-- ----------------------------------------------------------------
DROP TABLE IF EXISTS sys_org;
CREATE TABLE sys_org (
    id     varchar(36)  NOT NULL COMMENT 'ID',
    name   varchar(255) DEFAULT NULL COMMENT '机构名称',
    status varchar(10)  DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '机构（裁剪版，仅导入接口依赖列）';

-- ----------------------------------------------------------------
-- 3. 字典参数表 sys_param（原表 13 列全保留）
-- ----------------------------------------------------------------
DROP TABLE IF EXISTS sys_param;
CREATE TABLE sys_param (
    id              varchar(36)   NOT NULL COMMENT 'ID',
    param_type_id   varchar(36)   DEFAULT NULL COMMENT '参数类型ID',
    code            varchar(100)  DEFAULT NULL COMMENT '字典码',
    value           varchar(2000) DEFAULT NULL COMMENT '参数值',
    param_type_code varchar(100)  DEFAULT NULL COMMENT '参数类型编码',
    seq             int           DEFAULT NULL COMMENT '序号',
    is_def          varchar(1)    DEFAULT NULL COMMENT '是否默认值',
    remark          varchar(320)  DEFAULT NULL COMMENT '备注',
    status          varchar(10)   DEFAULT NULL COMMENT '状态',
    creator         varchar(36)   DEFAULT NULL COMMENT '创建人',
    create_time     timestamp     NULL DEFAULT NULL COMMENT '创建时间',
    update_user     varchar(36)   DEFAULT NULL COMMENT '更新人',
    update_time     timestamp     NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '参数';

-- ----------------------------------------------------------------
-- 种子数据
-- ----------------------------------------------------------------

-- 单位（取自原库 sys_org 真实样例）
INSERT INTO sys_org (id, name, status) VALUES
('-1', '广州市机构', '1'),
('56aa8ee1b1d4f2bf8c7487cdd1a821fc', '测试', '1'),
('57dfba74f3cc4fecb82be86d599830bb', '榕城区', '1');

-- 字典：system_state（0拟建/1在建/2已建/3下线）——取自原库 sys_param 真实数据
INSERT INTO sys_param (id, param_type_id, code, value, param_type_code, seq, is_def, remark, status, creator, create_time, update_time) VALUES
('958230180fff4f31916531bac946213a', 'c7c5dde227e7465d9bf4b2b8b1d56505', '0', '拟建', 'system_state', 0, '', '', '1', '1', NOW(), NOW()),
('0cc08832571341f795452bc8edea406f', 'c7c5dde227e7465d9bf4b2b8b1d56505', '1', '在建', 'system_state', 1, '', '', '1', '1', NOW(), NOW()),
('895f22d372bd443bb7d682f00d8e9786', 'c7c5dde227e7465d9bf4b2b8b1d56505', '2', '已建', 'system_state', 2, '', '', '1', '1', NOW(), NOW()),
('7e88531e9c274af49e2350f2e9cb6d89', 'c7c5dde227e7465d9bf4b2b8b1d56505', '3', '下线', 'system_state', 3, '', '', '1', '1', NOW(), NOW());

-- 字典：deployment_method
INSERT INTO sys_param (id, param_type_id, code, value, param_type_code, seq, is_def, remark, status, creator, create_time, update_time) VALUES
('1a7a6fd51f6945778549b424489208ea', 'bd345b6945af487da4d9fe47236e9125', '0', '地市集中部署', 'deployment_method', 0, '', '', '1', '1', NOW(), NOW()),
('173ca5de10454a3e956b031c39499816', 'bd345b6945af487da4d9fe47236e9125', '1', '县区部署', 'deployment_method', 1, '', '', '1', '1', NOW(), NOW()),
('853fe4f772e34d8b90e0df7cf4c2bb20', 'bd345b6945af487da4d9fe47236e9125', '2', '省市两级分级部署', 'deployment_method', 2, '', '', '1', '1', NOW(), NOW()),
('dda5247c2a5547059d38d90da30c0b0a', 'bd345b6945af487da4d9fe47236e9125', '3', '市县(区)两级分级部署', 'deployment_method', 3, '', '', '1', '1', NOW(), NOW()),
('1026e9f5f9424c1f8151969e53c2164b', 'bd345b6945af487da4d9fe47236e9125', '4', '省市县(区)三级分级部署', 'deployment_method', 4, '', '', '1', '1', NOW(), NOW());

-- 字典：network_access_type
INSERT INTO sys_param (id, param_type_id, code, value, param_type_code, seq, is_def, remark, status, creator, create_time, update_time) VALUES
('b7f2afc1391c49b896327b5fecdd2ec7', '9b2f3cc1a6044f40b77a1a9adf5d20fb', '0', '政务外网', 'network_access_type', 0, '', '', '1', '1', NOW(), NOW()),
('3b99bf8fb1904ec48cd8a1a5f71a8479', '9b2f3cc1a6044f40b77a1a9adf5d20fb', '1', '政务内网', 'network_access_type', 1, '', '', '1', '1', NOW(), NOW()),
('672b2c40a4f4460a8e163b480cc95ae4', '9b2f3cc1a6044f40b77a1a9adf5d20fb', '2', '业务专网', 'network_access_type', 2, '', '', '1', '1', NOW(), NOW()),
('8ca945311a84418dbe0f1311aa2931c5', '9b2f3cc1a6044f40b77a1a9adf5d20fb', '3', '互联网', 'network_access_type', 3, '', '', '1', '1', NOW(), NOW()),
('5080f729f07d443eab7604d0ab50f545', '9b2f3cc1a6044f40b77a1a9adf5d20fb', '4', '其他', 'network_access_type', 4, '', '', '1', '1', NOW(), NOW());

-- 存量演示记录：用它演示“同名导入整份拒绝，已有记录保持不变”
INSERT INTO dc_information_system_manage
(id, system_name, system_state, deployment_method, current_affiliated_unit_id, implementation_unit_id, network_access_type, function_description, status, creator, create_time, update_user, update_time) VALUES
('e2c4b6d8f0a24e5f8b1c3d5e7f9a1b2c', '存量演示系统', '1', '0', '-1', '-1', '0', '用于验证同名导入不修改原有数据', '1', '1', NOW(), '1', NOW());
