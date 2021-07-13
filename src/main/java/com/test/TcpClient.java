package com.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author yongxum
 * @date 2021年07月12日 12:02
 */
public class TcpClient {

    private String host;
    private int port;
    private Bootstrap bootstrap;
    /** 重连策略 */
    private RetryPolicy retryPolicy;
    /** 将<code>Channel</code>保存起来, 可用于在其他非handler的地方发送数据 */
    private Channel channel;

    public TcpClient(String host, int port) {
        this(host, port, new ExponentialBackOffRetry(1000, Integer.MAX_VALUE, 1000));
    }

    public TcpClient(String host, int port, RetryPolicy retryPolicy) {
        this.host = host;
        this.port = port;
        this.retryPolicy = retryPolicy;
        init();
    }

    /**
     * 向远程TCP服务器请求连接
     */
    public void connect() {
        synchronized (bootstrap) {
            ChannelFuture future = bootstrap.connect(host, port);
            future.addListener(getConnectionListener());
            this.channel = future.channel();
        }
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    private void init() {
        EventLoopGroup group = new NioEventLoopGroup();
        // bootstrap 可重用, 只需在TcpClient实例化的时候初始化即可.
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientHandlersInitializer(TcpClient.this));
    }

    private ChannelFutureListener getConnectionListener() {
        return new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    future.channel().pipeline().fireChannelInactive();
                }
            }
        };
    }

    public static void main(String[] args) {
        TcpClient tcpClient = new TcpClient("10.103.0.172", 8085);
        tcpClient.connect();
    }

}
