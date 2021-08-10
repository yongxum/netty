package com.returnClient;

/**
 * @author yongxum
 * @date 2021年08月10日 9:50
 */
public class ClientService {

    private static NettyConfig nettyConfig = new NettyConfig();

    /**
     * 发送消息, 并返回json
     * @author yongxum
     * @date 2021/8/10 9:56
     * @return java.lang.String
     */
    public String sendMsg(){
        return nettyConfig.connAndSendMsg("zhangsan");
    }
}
