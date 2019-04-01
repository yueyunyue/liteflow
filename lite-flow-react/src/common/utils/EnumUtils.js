const statusNew = 0;
const statusOnline = 1;
const statusOffline = -1;

const statusMap = {};
statusMap[statusNew] = "新建";
statusMap[statusOnline] = "已上线";
statusMap[statusOffline] = "已下线";


const taskVersionNew = 0;
const taskVersionInit = 1;
const taskVersionReady = 2;
const taskVersionSubmitted = 3;
const taskVersionRunning = 4;
const taskVersionSuccess = 5;
const taskVersionFail = -1;
const taskVersionKilled = -2;
const taskVersionInvalid = -3;

const taskVersionStatusMap = {};
taskVersionStatusMap[taskVersionNew] = "新建";
taskVersionStatusMap[taskVersionInit] = "初始化";
taskVersionStatusMap[taskVersionReady] = "就绪";
taskVersionStatusMap[taskVersionSubmitted] = "提交";
taskVersionStatusMap[taskVersionRunning] = "运行";
taskVersionStatusMap[taskVersionSuccess] = "成功";
taskVersionStatusMap[taskVersionFail] = "失败";
taskVersionStatusMap[taskVersionKilled] = "KILLED";
taskVersionStatusMap[taskVersionInvalid] = "无效";

const taskVersionFinalStatusUndefined = 0;
const taskVersionFinalStatusSuccess = 1;
const taskVersionFinalStatusFail = -1;
const taskVersionFinalStatusKilled = -2;
const taskVersionFinalStatusMap = {};
taskVersionFinalStatusMap[taskVersionFinalStatusUndefined] = "新建";
taskVersionFinalStatusMap[taskVersionFinalStatusSuccess] = "成功";
taskVersionFinalStatusMap[taskVersionFinalStatusFail] = "失败";
taskVersionFinalStatusMap[taskVersionFinalStatusKilled] = "KILLED";


/**
 * 依赖相关
 */
const taskDependencyTypeMap = {};

const taskDependencyTypeDefault = 0;
const taskDependencyTypeOffset = 1;
const taskDependencyTypeTimeRange = 2;
taskDependencyTypeMap[taskDependencyTypeDefault] = "无";
taskDependencyTypeMap[taskDependencyTypeOffset] = "偏移量";
taskDependencyTypeMap[taskDependencyTypeTimeRange] = "时间区间";


/**
 * 执行者任务状态
 * @type {number}
 */
const exeJobStatusNew = 0;
const exeJobStatusRunning = 1;
const exeJobStatusSuccess = 2;
const exeJobStatusFail = -1;
const exeJobStatusKilled = -2;

const exeJobStatusMap = {};
exeJobStatusMap[exeJobStatusNew] = "新建";
exeJobStatusMap[exeJobStatusRunning] = "运行中";
exeJobStatusMap[exeJobStatusSuccess] = "成功";
exeJobStatusMap[exeJobStatusFail] = "killed";
exeJobStatusMap[exeJobStatusKilled] = "失败";

const exeJobCallbackStatusMap = {};
const exeJobCallbackStatusNoNeed = -1;
const exeJobCallbackStatusWaiting = 0;
const exeJobCallbackStatusCallbacked = 1;
exeJobCallbackStatusMap[exeJobCallbackStatusNoNeed] = "不需要回调";
exeJobCallbackStatusMap[exeJobCallbackStatusWaiting] = "等待回调";
exeJobCallbackStatusMap[exeJobCallbackStatusCallbacked] = "回调完成";
/**
 * dailyInit状态
 * @type {{}}
 */
const dailyInitStatusMap = {};
const dailyInitStatusNew = 0;
const dailyInitStatusSuccess = 1;
const dailyInitStatusFail = -1;
const dailyInitStatusDisable = -2;
dailyInitStatusMap[dailyInitStatusNew] = "新建";
dailyInitStatusMap[dailyInitStatusSuccess] = "成功";
dailyInitStatusMap[dailyInitStatusFail] = "失败";
dailyInitStatusMap[dailyInitStatusDisable] = "无效";




module.exports = {

  trueInt: 1,

  statusOnline:statusOnline,

  exeJobStatusRunning: exeJobStatusRunning,

  commonStatusOff: statusOffline,

  exeJobStatusMap: exeJobStatusMap,

  exeJobCallbackStatusMap: exeJobCallbackStatusMap,

  dailyInitStatusMap: dailyInitStatusMap,
    /**
     * 任务依赖
     */
  taskDependencyTypeDefault: taskDependencyTypeDefault,

  taskDependencyTypeOffset: taskDependencyTypeOffset,

  taskDependencyTypeTimeRange: taskDependencyTypeTimeRange,

   taskDependencyTypeMap: taskDependencyTypeMap,

  booleanMap: {
    0: "否",
    1: "是"
  },
  /**
   *  任务或任务流状态
   */
  statusMap: statusMap,
  /**
   * 时间粒度Map
  */
  periodMap: {
    2: '分',
    3: '时',
    4: '日',
    5: '周',
    6: '月',
    7: '年'
  },
  /**
   * 并发策略
  */
  concurrencyMap: {
    1: '忽略',
    2: '等待'
  },

  taskVersionStatusMap: taskVersionStatusMap,

  taskVersionFinalStatusUndefined: taskVersionFinalStatusUndefined,

  taskVersionFinalStatusSuccess : taskVersionFinalStatusSuccess,

  taskVersionFinalStatusFail : taskVersionFinalStatusFail,

  taskVersionFinalStatusKilled: taskVersionFinalStatusKilled,

  taskVersionFinalStatusMap: taskVersionFinalStatusMap,
  /**
   * 获取类型名称
   */
  getStatusName(status) {
    return this.statusMap[status];
  },
  getStatusAlertType(status) {
    switch(status){
        case 0:
          return "info";
        case 1:
          return "success";
        case 2:
          return "error";

    }

    return "info";
  },
  /**
   * 获取周期名称
    * @param period
   * @returns {*}
   */
  getPeriodName(period) {
    return this.periodMap[period];
  },
  /**
   * 获取任务版本的状态名
    * @param status
   * @returns {*}
   */
  getTaskVersionStatusName(status){
    return this.taskVersionStatusMap[status];
  },
  /**
   * 获取任务版本的最终状态名
    * @param status
   * @returns {*}
   */
  getTaskVersionFinalStatusName(status){
    return this.taskVersionFinalStatusMap[status];
  },
  /**
   * 获取周期的数据
    * @returns {Array}
   */
  getPeriodOptionArray(){
    const options = [];
    for(let key in this.periodMap){
      options.push({id: key, name: this.periodMap[key]})
    }
    return options;
  },
  /**
   * 获取并发的数据
    * @returns {Array}
   */
  getConcurrencyOptionArray(){
    const options = [];
    for(let key in this.concurrencyMap){
      options.push({id: key, name: this.concurrencyMap[key]})
    }
    return options;
  },
    /**
     * 获取状态的数据
     * @returns {Array}
     */
  getStatusOptionArray(){
        const options = [];
        for(let key in this.statusMap){
            options.push({id: key, name: this.statusMap[key]})
        }
        return options;
  },
  /**
   * 获取任务版本状态的数据
   * @returns {Array}
   */
  getTaskVersionStatusOptionArray(){
        const options = [];
        for(let key in this.taskVersionStatusMap){
            options.push({id: key, name: this.taskVersionStatusMap[key]})
        }
        return options;
  },
  /**
   * 获取任务版本状态的数据
   * @returns {Array}
   */
  getTaskVersionFinalStatusOptionArray(){
        const options = [];
        for(let key in this.taskVersionFinalStatusMap){
            options.push({id: key, name: this.taskVersionFinalStatusMap[key]})
        }
        return options;
  },
  /**
   * 获取任务依赖的数据
   * @returns {Array}
   */
  getTaskDependencyTypeOptionArray(){
        const options = [];
        for(let key in this.taskDependencyTypeMap){
            options.push({id: key, name: this.taskDependencyTypeMap[key]})
        }
        return options;
  },
    /**
     * 获取类型名称
     */
   getTaskDependencyTypeName(type) {
        return this.taskDependencyTypeMap[type];
    },
    /**
     * 获取类型名称
     */
   getExeJobStatusName(status) {
        return this.exeJobStatusMap[status];
    },
    /**
     * 获取类型名称
     */
   getExeJobCallbackStatusName(status) {
        return this.exeJobCallbackStatusMap[status];
    },
    /**
     * 获取dailyInit类型名称
     */
   getDailyInitStatusName(status) {
        return this.dailyInitStatusMap[status];
    }

};
