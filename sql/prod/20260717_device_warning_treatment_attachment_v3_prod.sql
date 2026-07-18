-- 设备预警 V3 处置附件（正式库）
-- 文件保存在服务器私有目录，本表只保存附件元数据，不保存文件二进制。
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS fe_device_warning_task_attachment (
    attachment_id bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
    task_id bigint NOT NULL COMMENT '预警任务ID',
    warning_id bigint NOT NULL COMMENT '设备预警ID',
    record_id bigint NOT NULL COMMENT '处置记录ID',
    original_name varchar(255) NOT NULL COMMENT '原始文件名',
    stored_path varchar(500) NOT NULL COMMENT '私有存储相对路径',
    file_type varchar(100) NOT NULL COMMENT '文件MIME类型',
    file_size bigint NOT NULL COMMENT '文件大小（字节）',
    uploader_user_id bigint NOT NULL COMMENT '上传人用户ID',
    uploader_user_name varchar(64) NOT NULL COMMENT '上传人账号快照',
    uploader_nick_name varchar(64) DEFAULT NULL COMMENT '上传人名称快照',
    upload_time datetime NOT NULL COMMENT '上传时间',
    create_by varchar(64) DEFAULT '' COMMENT '创建者',
    create_time datetime DEFAULT NULL COMMENT '创建时间',
    del_flag char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    PRIMARY KEY (attachment_id),
    KEY idx_warning_attachment_task (task_id, upload_time),
    KEY idx_warning_attachment_record (record_id, upload_time),
    KEY idx_warning_attachment_warning (warning_id, upload_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备预警任务处置附件元数据表';
