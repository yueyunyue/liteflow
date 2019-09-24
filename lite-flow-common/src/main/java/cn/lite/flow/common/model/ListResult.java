package cn.lite.flow.common.model;

import lombok.Data;
import lombok.ToString;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-08-30
 **/
@Data
@ToString(callSuper = true)
public class ListResult<D> extends Result{

    private int count;

    public static <D> ListResult success(D d, int count){

        ListResult<D> responseResult = new ListResult<>();
        responseResult.setData(d);
        responseResult.setSuccess(true);
        return responseResult;

    }
}

