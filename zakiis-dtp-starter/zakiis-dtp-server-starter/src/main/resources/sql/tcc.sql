-- try接口成功后，往表里添加数据，cancel接口先查询该表，无数据则为空回滚，直接返回true
-- 二阶段幂等性依赖`status`字段
CREATE TABLE IF NOT EXISTS `tcc_fence_log` (
  `xid` VARCHAR(128) NOT NULL COMMENT 'global id',
  `branch_id` BIGINT NOT NULL COMMENT 'branch id',
  `action_name` VARCHAR(64) NOT NULL COMMENT 'action name',
  `status` TINYINT NOT NULL COMMENT 'tried:1;committed:2;rollbacked:3;suspended:4',
  `created_time` DATETIME(3) NOT NULL COMMENT 'created time',
  `updated_time` DATETIME(3) NOT NULL COMMENT 'updated time',
  PRIMARY KEY(`xid`, `branch_id`),
  index `idx_status`(`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;