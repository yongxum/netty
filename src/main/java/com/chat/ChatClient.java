package com.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @author yongxum
 * @date 2021年07月08日 9:34
 */
public class ChatClient {

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
//                    .localAddress(53690) // 指定客户端端口号
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new StringDecoder()) //加入解码器
                            .addLast(new StringEncoder()) // 加入编码器
                            .addLast(new ChatClientHandler()); //业务处理handler
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090).sync();
            // 得到channel
            Channel channel = channelFuture.channel();
            System.out.println("==========" + channel.localAddress() + "==========");
            // 客户端需要输入信息, 创建一个扫描器
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String msg = scanner.nextLine();
                // 通过channel发送到服务器端
                channel.writeAndFlush(msg);
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }finally {
            // 关闭资源
            group.shutdownGracefully();
        }

    }

}
