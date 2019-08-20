package com.psvm.fun.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

// 标示一个ChannelHandler可以被多个Channel安全地共享
@ChannelHandler.Sharable
@Slf4j
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        log.info("服务端接收: " + in.toString(CharsetUtil.UTF_8));
        // 将收到的消息写给发送者，而不冲刷出站
        context.write(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        // 将未决消息冲刷到远程节点并关闭
        context.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        log.info("读取操作期间有异常抛出");
        cause.printStackTrace();
        context.close();
    }

}
