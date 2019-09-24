package cn.lite.flow.common.model;

import lombok.Data;
import lombok.ToString;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-08-30
 **/
@Data
@ToString
public class Result<D> {

    private Boolean success;

    private String msg;

    private D data;

    public static <D> Result success(D d){

        Result<D> responseResult = new Result<>();
        responseResult.setData(d);
        responseResult.setSuccess(true);
        return responseResult;

    }
    public static Result error(String msg){
        Result responseResult = new Result<>();
        responseResult.setSuccess(false);
        responseResult.setMsg(msg);
        return responseResult;
    }
    public Boolean isSuccess(){
        return success;
    }


}

