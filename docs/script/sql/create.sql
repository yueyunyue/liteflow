-- 用户信息表
DROP TABLE if EXISTS lf_console_user;
CREATE TABLE lf_console_user (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  user_name varchar(32) NOT NULL DEFAULT '' COMMENT '用户名',
  email varchar(64) DEFAULT '' COMMENT '邮箱',
  phone varchar(20) DEFAULT '' COMMENT '手机号',
  password varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  is_super tinyint NOT NULL DEFAULT '0' COMMENT '是否是超级管理员',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_uniq_user_name (user_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 用户组
DROP TABLE if EXISTS lf_console_user_group;
CREATE TABLE lf_console_user_group (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  name varchar(32) NOT NULL DEFAULT '' COMMENT '用户组名',
  description varchar(128) DEFAULT '' COMMENT '说明',
  user_id int NOT NULL DEFAULT '0' COMMENT '创建者id',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_uniq_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户组信息表';

-- 用户和用户组对应关系表
DROP TABLE if EXISTS lf_console_user_group_mid;
CREATE TABLE lf_console_user_group_mid (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  user_id int NOT NULL DEFAULT '0' COMMENT '用户id',
  group_id int NOT NULL DEFAULT '0' COMMENT '用户组id',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_uniq_user_id_group_id (user_id,group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和用户组对应关系表';


-- 用户和用户组对应的任务-任务组权限表
DROP TABLE if EXISTS lf_console_user_group_auth_mid;
CREATE TABLE lf_console_user_group_auth_mid (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
	source_id INT NOT NULL DEFAULT '0' COMMENT '源id，对应用户id or 用户组id',
	source_type TINYINT NOT NULL DEFAULT '1' COMMENT '源类型',
	target_id INT NOT NULL DEFAULT '0' COMMENT '目标id，对应任务id or 任务流id',
	target_type TINYINT NOT NULL DEFAULT '1' COMMENT '目的类型',
	has_edit_auth TINYINT NOT NULL DEFAULT '0' COMMENT '是否有编辑权限 1-是 0-否',
	has_execute_auth TINYINT NOT NULL DEFAULT '0' COMMENT '是否有执行权限 1-是 0-否',
	user_id INT NOT NULL DEFAULT '0' COMMENT '创建者id',
	create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uniq_sourceId_sourceType_targetId_targetType (source_id, source_type, target_id, target_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和用户组对应的任务-任务组权限表';

-- 菜单表
DROP TABLE if EXISTS lf_console_menu;
CREATE TABLE lf_console_menu (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
	name VARCHAR(12) NOT NULL DEFAULT '' COMMENT '名称',
	icon VARCHAR(128) DEFAULT '' COMMENT '图标',
  description varchar(128) DEFAULT '' COMMENT '说明',
  order_num INT NOT NULL DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (id),
  UNIQUE KEY idx_uniq_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 子菜单表
DROP TABLE if EXISTS lf_console_menu_item;
CREATE TABLE lf_console_menu_item (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
	menu_id INT NOT NULL DEFAULT '0' COMMENT '子菜单id',
	name VARCHAR(12) NOT NULL DEFAULT '' COMMENT '名称',
	url VARCHAR(64) NOT NULL DEFAULT '' COMMENT '对应url',
  order_num INT NOT NULL DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (id),
  UNIQUE KEY idx_uniq_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='子菜单表';

-- 角色信息表
DROP TABLE if EXISTS lf_console_role;
CREATE TABLE lf_console_role (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  role_name varchar(32) NOT NULL DEFAULT '' COMMENT '角色名',
  description varchar(128) DEFAULT '' COMMENT '说明',
  user_id int NOT NULL DEFAULT '0' COMMENT '创建者id',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_uniq_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- 角色和权限对应关系表
DROP TABLE if EXISTS lf_console_role_auth_mid;
CREATE TABLE lf_console_role_auth_mid (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  role_id int NOT NULL DEFAULT '0' COMMENT '角色id',
  menu_item_id int NOT NULL DEFAULT '0' COMMENT '子菜单id',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_uniq_role_id_auth_url_id (role_id,menu_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限对应关系表';

-- 用户和角色对应关系表
DROP TABLE if EXISTS lf_console_user_role_mid;
CREATE TABLE lf_console_user_role_mid (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  user_id int NOT NULL DEFAULT '0' COMMENT '用户id',
  role_id int NOT NULL DEFAULT '0' COMMENT '角色id',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_uniq_user_id_role_id (user_id,role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色对应关系表';




-- 任务信息表
DROP TABLE if EXISTS lf_console_task;
CREATE TABLE lf_console_task (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  name varchar(32) NOT NULL DEFAULT '' COMMENT '名称',
  cron_expression varchar(32) NOT NULL DEFAULT '' COMMENT 'crontab表达式',
  period tinyint NOT NULL DEFAULT '1' COMMENT '周期',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  version tinyint NOT NULL DEFAULT '0' COMMENT '任务版本',
  is_concurrency tinyint NOT NULL DEFAULT '0' COMMENT '是否可以并发',
  execute_strategy varchar(128) DEFAULT '' COMMENT '当并发发生时的执行策略',
  plugin_id int DEFAULT NULL COMMENT '插件id',
  plugin_conf text COMMENT '插件配置',
  is_retry tinyint NOT NULL DEFAULT '0' COMMENT '失败是否重试',
  retry_conf varchar(128) DEFAULT NULL COMMENT '失败时的重试配置',
  max_run_time int DEFAULT NULL COMMENT '最长运行时长',
  description varchar(128) NOT NULL DEFAULT '' COMMENT '说明',
  alarm_email varchar(32) DEFAULT NULL COMMENT '报警邮箱',
  alarm_phone varchar(20) DEFAULT NULL COMMENT '报警电话',
  user_id int NOT NULL DEFAULT '0' COMMENT '创建者id',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_uniq_name (name),
  KEY idx_status(status),
  KEY idx_period(period)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务信息表';

-- 任务依赖关系表
DROP TABLE if EXISTS lf_console_task_dependency;
CREATE TABLE lf_console_task_dependency (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  task_id bigint NOT NULL DEFAULT '0' COMMENT '任务id',
  upstream_task_id bigint NOT NULL DEFAULT '0' COMMENT '上游任务id',
  type tinyint NOT NULL DEFAULT '0' COMMENT '类型',
  config varchar(128) DEFAULT NULL COMMENT '依赖配置信息',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_uniq_task_id_upstream_task_id(task_id,upstream_task_id),
  KEY idx_upstream_task_id_task_id(upstream_task_id, task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务依赖关系表';

-- 任务版本每天生成信息表
DROP TABLE if EXISTS lf_console_task_version_daily_init;
CREATE TABLE lf_console_task_version_daily_init (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  task_id bigint NOT NULL DEFAULT '0' COMMENT '实例id',
  day int NOT NULL DEFAULT '0' COMMENT '日期yyyyMMdd格式',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  msg text DEFAULT NULL COMMENT '信息',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_uniq_day_task_id (day, task_id),
  KEY idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务版本每天生成信息表';

-- 任务版本信息表
DROP TABLE if EXISTS lf_console_task_version;
CREATE TABLE lf_console_task_version (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  version_no bigint NOT NULL DEFAULT '0' COMMENT '版本号',
  task_id bigint NOT NULL DEFAULT '0' COMMENT '任务id',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  final_status tinyint DEFAULT NULL COMMENT '最终状态',
  retry_num int DEFAULT '0' COMMENT '尝试次数',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_uniq_task_id_version_no(task_id,version_no),
  KEY idx_tid_status(task_id,status),
  KEY idx_tid_final_status_no(task_id,final_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务版本信息表';

-- 任务实例信息表
DROP TABLE if EXISTS lf_console_task_instance;
CREATE TABLE lf_console_task_instance (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  task_id bigint NOT NULL DEFAULT '0' COMMENT '任务id',
  task_version_id bigint NOT NULL DEFAULT '0' COMMENT '任务版本id',
  task_version_no bigint NOT NULL DEFAULT '0' COMMENT '任务版本(冗余字段)',
  logic_run_time datetime NOT NULL COMMENT '逻辑运行时间',
  plugin_id int NOT NULL DEFAULT '0' COMMENT '插件id',
  plugin_conf text COMMENT '插件配置',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  run_start_time datetime DEFAULT NULL COMMENT '运行开始时间',
  run_end_time datetime DEFAULT NULL COMMENT '运行结束时间',
  msg varchar(500) DEFAULT NULL COMMENT '状态运行消息',
  executor_job_id int DEFAULT NULL COMMENT '执行引擎job id',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_version_id (task_version_id),
  KEY idx_task_id_version (task_id,task_version_no),
  KEY idx_status_logic_time (status,logic_run_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务实例信息表';

-- 任务实例依赖关系表
DROP TABLE if EXISTS lf_console_task_instance_dependency;
CREATE TABLE lf_console_task_instance_dependency (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  instance_id bigint NOT NULL DEFAULT '0' COMMENT '任务实例id',
  upstream_task_id bigint NOT NULL DEFAULT '0' COMMENT '上游任务id',
  upstream_task_version_no bigint DEFAULT NULL COMMENT '依赖上游任务版本',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_instance_id (instance_id),
  KEY idx_upstream_task_id_version (upstream_task_id, upstream_task_version_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务实例依赖关系表';

-- 任务流信息表
DROP TABLE if EXISTS lf_console_flow;
CREATE TABLE lf_console_flow (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  name varchar(32) NOT NULL DEFAULT '' COMMENT '名称',
  description varchar(64) NOT NULL DEFAULT '' COMMENT '说明',
  user_id int NOT NULL DEFAULT '0' COMMENT '创建者id',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uniq_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务流信息表';

-- 任务依赖快照信息表
DROP TABLE if EXISTS lf_console_flow_dependency_snapshot;
CREATE TABLE lf_console_flow_dependency_snapshot (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
	flow_id bigint NOT NULL DEFAULT '0' COMMENT '集合id',
	task_id bigint NOT NULL DEFAULT '0' COMMENT '任务id',
	upstream_task_id bigint NOT NULL DEFAULT '0' COMMENT '上游任务id',
	type INT NOT NULL DEFAULT '0' COMMENT '依赖类型',
	conf VARCHAR(128) DEFAULT NULL COMMENT '依赖配置信息',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_flow_id (flow_id),
  KEY idx_task_id (task_id),
  KEY idx_upstream_id (upstream_task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务依赖快照信息表';

-- 任务流中依赖任务信息表
DROP TABLE if EXISTS lf_console_flow_dependency;
CREATE TABLE lf_console_flow_dependency (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  flow_id bigint NOT NULL DEFAULT '0' COMMENT '任务流id',
  task_dependency_id bigint NOT NULL DEFAULT '0' COMMENT '依赖任务id',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY idx_uniq_flow_id_dependency_id (flow_id,task_dependency_id),
  KEY idx_dependency_id (task_dependency_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务流中任务依赖信息表';


-- 执行者信息表
DROP TABLE if EXISTS lf_executor_server;
CREATE TABLE lf_executor_server (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  name varchar(32) NOT NULL DEFAULT '' COMMENT '名称',
  ip varchar(32) NOT NULL DEFAULT '' COMMENT 'ip',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  description varchar(64) NOT NULL DEFAULT '' COMMENT '说明',
  user_id int NOT NULL DEFAULT '0' COMMENT '创建者id',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uniq_name (name),
  UNIQUE KEY uniq_ip (ip)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行者信息表';

-- 执行容器信息表
DROP TABLE if EXISTS lf_executor_container;
CREATE TABLE lf_executor_container (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  name varchar(32) NOT NULL DEFAULT '' COMMENT '容器名称',
  field_config text COMMENT '插件需要实现的参数',
  status tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  log_type tinyint NOT NULL DEFAULT '1' COMMENT '日志类型',
  description varchar(64) NOT NULL DEFAULT '' COMMENT '说明',
  class_name varchar(128) NOT NULL DEFAULT '' COMMENT '类名',
  user_id int NOT NULL DEFAULT '0' COMMENT '创建者id',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uniq_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行容器信息表';

-- 执行任务信息表
DROP TABLE if EXISTS lf_executor_job;
CREATE TABLE lf_executor_job(
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  application_id varchar(64) DEFAULT NULL COMMENT '应用id',
  executor_server_id int NOT NULL DEFAULT '0' COMMENT '执行者id',
  plugin_id int DEFAULT NULL COMMENT '插件id',
  container_id int NOT NULL DEFAULT '0' COMMENT '容器id',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  source_id bigint NOT NULL DEFAULT '0' COMMENT '来源id',
  config text COMMENT '任务配置',
  msg text COMMENT '任务信息',
  start_time datetime COMMENT '开始时间',
  end_time datetime COMMENT '结束时间',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_status(status),
  UNIQUE KEY idx_source_id(source_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行任务信息表';

-- 执行任务回调表
DROP TABLE if EXISTS lf_executor_callback;
CREATE TABLE lf_executor_callback (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  job_id bigint DEFAULT NULL COMMENT '任务id',
  job_status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  job_source_id bigint NOT NULL DEFAULT '0' COMMENT '来源id',
  executor_server_id int NOT NULL DEFAULT '0' COMMENT '执行者id',
  job_msg text COMMENT '任务信息',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_job_id(job_id),
  KEY idx_executor_status(executor_server_id, status),
  KEY idx_job_source_id(job_source_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行任务回调表';

-- 执行插件信息表
DROP TABLE if EXISTS lf_executor_plugin;
CREATE TABLE lf_executor_plugin (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  name varchar(64) DEFAULT NULL COMMENT '插件名称',
  field_config text COMMENT '插件需要实现的参数',
  config text COMMENT '插件实现容器的参数',
  description varchar(64) NOT NULL DEFAULT '' COMMENT '说明',
  container_id int NOT NULL DEFAULT '0' COMMENT '容器id',
  user_id int NOT NULL DEFAULT '0' COMMENT '创建者id',
  status tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uniq_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行插件信息表';

-- 执行附件表
DROP TABLE if EXISTS lf_executor_attachment;
CREATE TABLE lf_executor_attachment (
  id int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  name varchar(64) DEFAULT NULL COMMENT '插件名称',
  type tinyint NOT NULL DEFAULT '0' COMMENT '类型',
  content text NOT NULL  COMMENT '内容',
  user_id int NOT NULL DEFAULT '0' COMMENT '创建者id',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行附件表';