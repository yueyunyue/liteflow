# 一、LiteFlow是做什么的

liteflow是一个基于任务版本来实现的分布式任务流调度系统

1.实现任务在任务流间共享

2.任务/任务流的可视化配置

3.一键修复任务/任务流修复提高数据修复效率

4.动态表单+容器机制，提供一个可扩展性比较强的执行引擎

5.执行者可以运行任何类型的任务，shell、python

[码云](https://gitee.com/yueyunyue/liteflow)

[GitHub](https://github.com/yueyunyue/liteflow)


# 二、LiteFlow项目介绍
![module-struct](./docs/img/module-struct.png "模块图")

## 1.项目主要分为控制台和执行引擎
   ### 1) [控制台(CONSOLE)](./docs/md/console.md)
   主要用来配置以及调度任务
   
   ![instruction](./docs/img/instruction.png "功能介绍")
   
   ### 2) 执行引擎(EXECUTOR)
   
   依托容器->插件->任务实现
   
   容器分为同步容器和异步容器
  
   ![container](./docs/img/container-plugin-job.png "容器")


# 三、相关知识

 1.[控制台页面](docs/md/console.md)

 2.[项目运行原理](docs/md/developer.md)

 3.[任务版本&时间计算器](docs/md/task-version.md)
 
 4.[容器&插件&动态表单](docs/md/container-dynamic-form.md)
 
 5.[项目部署相关](docs/md/deploy.md)

# 相关项目
- [dubbo](https://github.com/apache/incubator-dubbo)
- [azkaban](https://github.com/azkaban/azkaban)
