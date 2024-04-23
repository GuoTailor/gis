package com.gyh.gis.netty.client;

import com.gyh.gis.netty.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

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
        //pipeline.addLast(new IdleStateHandler(60 * 3, 0, 0));
        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(messageEncoder);
        pipeline.addLast(serverHandler);
    }

    public NettyServerHandler getServerHandler() {
        return serverHandler;
    }
}
