package cn.lite.flow.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: 服务节点
 * @author: yueyunyue
 * @create: 2020-09-14
 **/
@Getter
@Setter
@NoArgsConstructor
public class ServerNode {

    private String host;

    private Integer port;

    public ServerNode(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public boolean isSameNode(ServerNode node){
        return isSameNodeUseHost(node.getHost(), node.getPort());
    }

    public boolean isSameNodeUseHost(String host, Integer port){
        return StringUtils.equals(this.host, host) && this.port.equals(port);
    }
}
