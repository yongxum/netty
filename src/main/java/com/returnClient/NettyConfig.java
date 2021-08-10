package com.returnClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author yongxum
 * @date 2021年08月10日 9:27
 */
public class NettyConfig {
    private static String  host = "127.0.0.1";
    private static Integer port = 8086;

    private static Bootstrap bootstrap;
    // 事件池
    private static NioEventLoopGroup workGroup;

    // 静态方法
    static{
        bootstrap = new Bootstrap();
        workGroup = new NioEventLoopGroup();
        bootstrap.group(workGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)  // 如果要求高时性, 有数据发送时就马上发送, 就将该选项设置为true, 关闭Nagle算法
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(10*1024)); //这行配置比较重要, 关系到读取到字符串的大小
    }

    /**
     * 连接通道, 并发送消息给服务端并返回json串
     * @author yongxum
     * @date 2021/8/10 9:53
     * @param sendMsg
     * @return java.lang.String
     */
    public String connAndSendMsg(String sendMsg){

        MyChannelInitializer myChannelInitializer = new MyChannelInitializer(sendMsg);
        bootstrap.handler(myChannelInitializer);
        try {
            bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return myChannelInitializer.getResponse();
    }
}
