package com.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yongxum
 * @date 2021年07月08日 9:21
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    // GlobalEventExecutor.INSTANCE 是全局事件执行器, 是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 表示channel 处于就绪状态, 提示上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 将该客户加入聊天的信息推送给其他在线的客户端
        // 该方法会将 channelGroup中所有的channel遍历, 并发送消息
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 上线了 "
                + sdf.format(new Date()) + "\n");
        // 将当前channel加入到channelGroup
        channelGroup.add(channel);
        System.out.println(ctx.channel().remoteAddress() + " 上线了 " + "\n");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 下线了 " + "\n");
        System.out.println(channel.remoteAddress() + " 下线了 " + "\n");
        System.out.println("channelGroup size = " + channelGroup.size());
    }

    /**
     * 读取数据
     * @author yongxum
     * @date 2021/7/8 9:51
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        // 遍历channelGroup, 根据不同情况, 回送不同的消息
        channelGroup.forEach(ch -> {
            if (channel != ch){
                ch.writeAndFlush("[客户端]" + channel.remoteAddress() + " 发送了消息: " + msg + "\n");
            } else {
                ch.writeAndFlush("[ 自己 ]发送了消息: " + msg + "\n");
            }
        });
    }
}
