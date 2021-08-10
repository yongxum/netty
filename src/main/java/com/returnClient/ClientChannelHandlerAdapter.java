package com.returnClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author yongxum
 * @date 2021年08月10日 9:37
 */
public class ClientChannelHandlerAdapter extends SimpleChannelInboundHandler<String> {

    // 需要发送的信息
    private String param;
    // 自定义channel初始化加载类
    private MyChannelInitializer myChannelInitializer;

    public ClientChannelHandlerAdapter(String param, MyChannelInitializer myChannelInitializer) {
        this.param = param;
        this.myChannelInitializer = myChannelInitializer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        myChannelInitializer.setResponse(msg);
        // 拿到返回后就关闭通道连接
        channelHandlerContext.close();
    }
}
