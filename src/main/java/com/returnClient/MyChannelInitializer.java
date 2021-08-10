package com.returnClient;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.CountDownLatch;

/**
 * 给netty线程加锁, 直到拿到返回消息为止
 * @author yongxum
 * @date 2021年08月10日 9:40
 */
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private String response;
    private CountDownLatch countDownLatch;
    // 需要发送给Server的消息
    private String param;

    public MyChannelInitializer(String param) {
        this.param = param;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        countDownLatch = new CountDownLatch(1);
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new StringDecoder());  //解码request
        pipeline.addLast(new StringEncoder());  //编码response
        pipeline.addLast(new ClientChannelHandlerAdapter(param, this));
    }

    //由于netty是异步的，所以无法等待到服务端响应后调用客户端的channelRead0方法，controller就已经返回了，导致了网页显示的返回结果一直是空
    //主线程通过CountDownLatch来锁住没有返回结果的线程，直到工作线程获得结果并解锁
    public String getResponse() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
        countDownLatch.countDown();
    }
}
