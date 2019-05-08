package cn.lite.flow.executor.client.model;

import cn.lite.flow.executor.client.base.BaseRpcListParam;
import lombok.Data;
import lombok.ToString;

/**
 * @description: 附件请求
 * @author: yueyunyue
 * @create: 2019-01-17
 **/
@Data
@ToString(callSuper = true)
public class AttachmentParam extends BaseRpcListParam {

    private Integer status;

}
