package cn.lite.flow.console.kernel.job;

import cn.lite.flow.common.job.basic.AbstractUnstatefullJob;
import cn.lite.flow.common.model.query.Page;
import cn.lite.flow.common.utils.ExceptionUtils;
import cn.lite.flow.console.common.consts.TimeUnit;
import cn.lite.flow.console.common.model.TimeRange;
import cn.lite.flow.console.common.time.TimeCalculatorFactory;
import cn.lite.flow.console.common.time.calculator.TimeCalculator;
import cn.lite.flow.common.utils.DateUtils;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskVersionDailyInit;
import cn.lite.flow.console.model.consts.DailyInitStatus;
import cn.lite.flow.console.model.consts.TaskStatus;
import cn.lite.flow.console.model.query.TaskVersionDailyInitQM;
import cn.lite.flow.console.service.TaskService;
import cn.lite.flow.console.service.TaskVersionDailyInitService;
import cn.lite.flow.console.service.TaskVersionService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: 初始化相关job
 * @author: yueyunyue
 * @create: 2018-07-30
 **/
public class VersionDailyInit2FireJob extends AbstractUnstatefullJob {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskVersionDailyInitService dailyInitService;

    @Autowired
    private TaskVersionService versionService;

    private final static int PAGE_SIZE = 100;

    private final static int CONCURRENT_COUNT = 10;

    @Override
    public void executeInternal() {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(CONCURRENT_COUNT);

        TaskVersionDailyInitQM dailyInitQM = new TaskVersionDailyInitQM();
        dailyInitQM.setStatus(DailyInitStatus.NEW.getValue());
        dailyInitQM.addOrderAsc(TaskVersionDailyInitQM.COL_ID);

        List<TaskVersionDailyInit> dailyInits = null;
        final int pageNo = 1;
        do{
            try {
                dailyInitQM.setPage(Page.getPageByPageNo(pageNo, PAGE_SIZE));
                dailyInits = dailyInitService.list(dailyInitQM);
                if(CollectionUtils.isEmpty(dailyInits)){
                    break;
                }
                final CountDownLatch countDownLatch = new CountDownLatch(dailyInits.size());
                for(TaskVersionDailyInit dailyInit : dailyInits){
                    //已添加的，忽略
                    Long taskId = dailyInit.getTaskId();
                    fixedThreadPool.execute(() -> {
                        try {
                            Task task = taskService.getById(taskId);
                            if(task.getStatus() != TaskStatus.ONLINE.getValue()){
                                return;
                            }
                            /**
                             * 生成任务版本
                             */
                            Date day = DateUtils.longToDate(dailyInit.getDay());
                            Date startTime = DateUtils.getStartTimeOfDay(day);
                            Date endTime = DateUtils.getEndTimeOfDay(day);
                            versionService.calVersionAndInstanceWithDependency(taskId, startTime, endTime);
                            dailyInitService.successDailyInit(dailyInit.getId());
                            LOG.info("daily init of task:{}", taskId);
                        }catch (Throwable e){
                            String errorMsg = ExceptionUtils.collectStackMsg(e);
                            dailyInitService.failDailyInit(dailyInit.getId(), errorMsg);
                            LOG.info("daily init error of task:{}", taskId, e);
                        }finally {
                            countDownLatch.countDown();
                        }
                    });
                }
                countDownLatch.await();
            }catch (Throwable e){
                LOG.error("daily init error", e);
            }

        }while (CollectionUtils.isNotEmpty(dailyInits));
    }
}
