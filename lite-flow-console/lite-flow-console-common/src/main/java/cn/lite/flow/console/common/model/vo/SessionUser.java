package cn.lite.flow.console.common.model.vo;

import cn.lite.flow.console.model.basic.Menu;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by ly on 2018/10/18.
 */
@Data
@ToString
public class SessionUser {

    private Long id;                    //用户id

    private String userName;            //用户名

    private Boolean isSuper;            //是否是超级管理员

    private List<Long> groupIds;        //用户所属组id

    private List<Long> roleIds;         //用户角色id列表

    private List<String> roleUrls;      //权限url列表

    private List<Menu> menus;           //菜单列表


}
