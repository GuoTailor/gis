package com.gyh.gis.netty.server;

import com.gyh.gis.netty.MessageListener;
import com.gyh.gis.netty.NettyServletRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * create by GYH on 2022/10/11
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(com.gyh.gis.netty.client.NettyServerHandler.class.getSimpleName());
    private MessageListener listener;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("通道打开{}", ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("连接关闭{}", ctx.channel().id());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.info("连接异常{}", ctx.channel().id());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        logger.info("读取数据 {}", msg);
        if (listener != null)
            listener.onMessage((NettyServletRequest) msg);
    }

    public void addListener(MessageListener listener) {
        this.listener = listener;
    }

}
