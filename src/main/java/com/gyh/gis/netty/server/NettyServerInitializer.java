package com.gyh.gis.netty.server;

import com.gyh.gis.netty.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by GYH on 2018/12/3.
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyServerHandler serverHandler = new NettyServerHandler();
    private final MessageEncoder messageEncoder = new MessageEncoder();


    /**
     * 初始化channel
     */
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 空闲一个半小时
        pipeline.addLast(new IdleStateHandler(60 * 90, 0, 0));
        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(messageEncoder);
        pipeline.addLast(serverHandler);
    }

    public NettyServerHandler getServerHandler() {
        return serverHandler;
    }
}
