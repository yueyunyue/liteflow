INSERT INTO
lf_console_menu(name, icon, description, order_num)
VALUES
('Dashboard', 'laptop', '看板', 1),
('任务', 'deployment-unit', '任务相关', 2),
('执行引擎', 'rocket', '执行者相关', 3),
('系统管理', 'setting', '系统权限相关', 4);

INSERT INTO
lf_console_menu_item(menu_id, name, url, order_num)
VALUES
(1, '看板', '/console/dashboard', 1),
(2, '任务流', '/console/flow', 1),
(2, '任务', '/console/task', 2),
(2, '任务版本', '/console/version', 3),
(2, '任务初始化', '/console/dailyInit', 4),
(3, '执行者', '/executor/server', 1),
(3, '执行容器', '/executor/container', 2),
(3, '执行插件', '/executor/plugin', 3),
(3, '执行任务', '/executor/exeJob', 4),
(4, '用户管理', '/console/system/user', 1),
(4, '用户组管理', '/console/system/ugroup', 2),
(4, '角色管理', '/console/system/role', 3);

-- admin默认密码123456
INSERT INTO lf_console_user(user_name, password, is_super, status) VALUE('lite', '62b402965c8a42e58bcb562a49c6b83e', 1, 1);
INSERT INTO lf_console_user_group(name, description, user_id, status) VALUE('默认用户组', '默认用户组', 1, 1);

INSERT INTO lf_console_role(id, role_name, description, user_id, create_time, update_time) VALUE (1, 'admin', '管理员', 1, now(), now());

INSERT INTO
lf_console_role_auth_mid(role_id, menu_item_id, create_time)
VALUES
(1, 1, now()),
(1, 2, now()),
(1, 3, now()),
(1, 4, now()),
(1, 5, now()),
(1, 6, now()),
(1, 7, now()),
(1, 8, now()),
(1, 9, now()),
(1, 10, now()),
(1, 11, now());

INSERT INTO
lf_executor_container(name, class_name, field_config, description, user_id)
VALUES
('Noop', 'cn.lite.flow.executor.kernel.container.impl.NoopContainer', '', '无操作容器', 1),

('Shell',
'cn.lite.flow.executor.kernel.container.impl.ShellContainer',
'[
  {
    "label": "shell",
    "name": "shellContent",
    "type": "TextArea",
    "editable": true,
    "defaultValue": "",
    "required": true,
    "componentConfig": {
       "rows":15
    }
  }
]', 'shell脚本', 1),

('JAVA_PROCESS',
'cn.lite.flow.executor.kernel.container.impl.JavaProcessContainer', '[
  {
    "label": "jar路径",
    "name": "mainJarPath",
    "type": "Input",
    "editable": false,
    "defaultValue": "",
    "required": true,
    "help": "jar路径"
  },{
    "label": "主类",
    "name": "mainClass",
    "type": "Input",
    "editable": false,
    "defaultValue": "",
    "required": true,
    "help": "main所在函数类名"
  },
  {
    "label": "jvm参数",
    "name": "jvmArgs",
    "type": "Input",
    "editable": false,
    "defaultValue": "",
    "required": true,
    "help": "jvm 参数"
  }
]   ',
'java进程',
1),
('SPARK_ON_YARN',
'cn.lite.flow.executor.kernel.container.impl.SparkOnYarnContainer', '[
  {
    "label": "yarn队列",
    "name": "yarnQueue",
    "type": "Input",
    "editable": false,
    "defaultValue": "default",
    "required": true,
    "help": ""
  },
  {
    "label": "主类",
    "name": "mainClass",
    "type": "Input",
    "editable": false,
    "defaultValue": "",
    "required": true,
    "help": "main函数所在类名"
  },
  {
    "label": "jar包",
    "name": "mainJar",
    "type": "Input",
    "editable": false,
    "defaultValue": "",
    "required": true,
    "help": "主类所在jar包"
  },
  {
    "label": "依赖jar包",
    "name": "dependencyJars",
    "type": "Input",
    "editable": false,
    "defaultValue": "",
    "required": false,
    "help": "运行依赖的jar包，多个以,隔开"
  },
  {
    "label": "instanceNum",
    "name": "instanceNum",
    "type": "InputNumber",
    "editable": false,
    "defaultValue": "1",
    "required": true,
    "help": ""
  },
  {
    "label": "driver核数",
    "name": "driverCore",
    "type": "InputNumber",
    "editable": false,
    "defaultValue": "1",
    "required": true,
    "help": ""
  },
  {
    "label": "driver内存",
    "name": "driverMemory",
    "type": "InputNumber",
    "editable": false,
    "defaultValue": "100",
    "required": true,
    "help": ""
  },
  {
    "label": "executor核数",
    "name": "executorCore",
    "type": "InputNumber",
    "editable": false,
    "defaultValue": "1",
    "required": true,
    "help": ""
  },
  {
    "label": "executor内存",
    "name": "executorMemory",
    "type": "InputNumber",
    "editable": false,
    "defaultValue": "100",
    "required": true,
    "help": ""
  }
]',
'sparkOnYarn',
1);




INSERT INTO
lf_executor_plugin(name, field_config, description, container_id, user_id)
VALUES
('Noop', '', '无操作容器', 1, 1),
('ShellScript', '', 'linux shell脚本', 2, 1),
('JavaProcess', '', 'java经常', 3, 1);





