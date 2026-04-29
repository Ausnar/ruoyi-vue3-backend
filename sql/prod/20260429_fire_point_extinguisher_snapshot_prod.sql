-- 消防点应配灭火器数量与设备数量快照（正式库 ruoyi）
-- 生产执行前请确认 fe_fire_point 尚未存在 expected_extinguisher_count，
-- 且 fe_fire_point_device_snapshot 尚未创建。
-- 用于设备报告“灭火器数量不足：实测数量 < 应配数量，连续3次”严谨判定。

set names utf8mb4;

alter table fe_fire_point
    add column expected_extinguisher_count int(11) null comment '应配灭火器数量' after contact_phone;

create table fe_fire_point_device_snapshot
(
    snapshot_id bigint(20) not null auto_increment comment '快照ID',
    fire_point_id bigint(20) not null comment '消防点ID',
    dept_id bigint(20) default null comment '归属部门ID',
    source_dept_id bigint(20) default null comment '凭证来源部门ID',
    config_id bigint(20) default null comment '合同/API配置ID',
    expected_extinguisher_count int(11) default null comment '应配灭火器数量',
    actual_extinguisher_count int(11) not null default 0 comment '实测灭火器数量',
    actual_sensor_count int(11) not null default 0 comment '实测传感器数量',
    snapshot_time datetime not null comment '快照时间',
    source_type varchar(32) default 'sdk_sync' comment '快照来源',
    create_by varchar(64) default '' comment '创建者',
    create_time datetime default null comment '创建时间',
    primary key (snapshot_id),
    key idx_fp_snapshot_fire_time (fire_point_id, snapshot_time),
    key idx_fp_snapshot_dept_time (dept_id, snapshot_time),
    key idx_fp_snapshot_source_dept_time (source_dept_id, snapshot_time),
    key idx_fp_snapshot_config_time (config_id, snapshot_time)
) engine=InnoDB default charset=utf8mb4 comment='消防点设备数量快照表';
