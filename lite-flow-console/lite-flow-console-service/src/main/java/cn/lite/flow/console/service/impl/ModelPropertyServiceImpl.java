package cn.lite.flow.console.service.impl;

import cn.lite.flow.console.dao.mapper.ModelPropertyMapper;
import cn.lite.flow.console.model.basic.ModelProperty;
import cn.lite.flow.console.model.query.ModelPropertyQM;
import cn.lite.flow.console.service.ModelPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
@Service
public class ModelPropertyServiceImpl implements ModelPropertyService {

    @Autowired
    private ModelPropertyMapper modelPropertyMapper;

    @Override
    public void add(ModelProperty model) {
        modelPropertyMapper.insert(model);
    }

    @Override
    public ModelProperty getById(long id) {
        return modelPropertyMapper.getById(id);
    }

    @Override
    public int update(ModelProperty model) {

       return modelPropertyMapper.update(model);
    }

    @Override
    public int count(ModelPropertyQM queryModel) {
        return modelPropertyMapper.count(queryModel);
    }

    @Override
    public List<ModelProperty> list(ModelPropertyQM queryModel) {
        return modelPropertyMapper.findList(queryModel);
    }
}
