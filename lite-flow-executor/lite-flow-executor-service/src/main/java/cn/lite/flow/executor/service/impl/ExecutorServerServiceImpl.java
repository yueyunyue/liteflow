package cn.lite.flow.executor.service.impl;
import cn.lite.flow.common.model.consts.StatusType;
import cn.lite.flow.executor.common.exception.ExecutorRuntimeException;
import cn.lite.flow.executor.dao.ExecutorServerMapper;
import cn.lite.flow.executor.model.basic.ExecutorServer;
import cn.lite.flow.executor.model.query.ExecutorServerQM;
import cn.lite.flow.executor.service.ExecutorServerService;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ly on 2018/12/14.
 */
@Service("executorServerServiceImpl")
public class ExecutorServerServiceImpl implements ExecutorServerService {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutorServerServiceImpl.class);

    @Autowired
    private ExecutorServerMapper executorServerMapper;

    @Override
    public void add(ExecutorServer executor) throws ExecutorRuntimeException {
        Preconditions.checkArgument(executor != null, "executor不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(executor.getName()), "name不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(executor.getIp()), "ip不能为空");

        executor.setStatus(StatusType.ON.getValue());
        try {
            executorServerMapper.insert(executor);
        } catch (DuplicateKeyException e) {
            LOG.error("add executor duplicate, ip:{}, name:{}", executor.getIp(), executor.getName());
            throw new ExecutorRuntimeException("已经存在相同ip or name的执行者");
        }
    }

    @Override
    public ExecutorServer getById(long id) {
        return executorServerMapper.getById(id);
    }

    @Override
    public int update(ExecutorServer executor) throws ExecutorRuntimeException {
        Preconditions.checkArgument(executor != null, "executor不能为空");
        Preconditions.checkArgument(executor.getId() != null, "id不能为空");

        try {
           return executorServerMapper.update(executor);
        } catch (DuplicateKeyException e) {
            LOG.error("update executor duplicate, id:{}, ip:{}, name:{}", executor.getId(), executor.getIp(), executor.getName());
            throw new ExecutorRuntimeException("已经存在相同ip or name的执行者");
        }
    }

    @Override
    public int count(ExecutorServerQM queryModel) {
        return executorServerMapper.count(queryModel);
    }

    @Override
    public List<ExecutorServer> list(ExecutorServerQM queryModel) {
        return executorServerMapper.findList(queryModel);
    }

    @Override
    public ExecutorServer getByIp(String ip) {
        return executorServerMapper.getByIp(ip);
    }
}
