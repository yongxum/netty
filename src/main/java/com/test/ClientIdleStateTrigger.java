package com.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author yongxum
 * @date 2021年07月12日 11:59
 */
public class ClientIdleStateTrigger extends ChannelInboundHandlerAdapter {

    public static final String HEART_BEAT = "heart beat!";

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                // write heartbeat to server
                ctx.writeAndFlush(HEART_BEAT);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
