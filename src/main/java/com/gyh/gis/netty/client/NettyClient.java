package com.gyh.gis.netty.client;

import com.gyh.gis.netty.MessageListener;
import com.gyh.gis.netty.NettyServletResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

/**
 * create by GYH on 2022/10/11
 */
@Component
public class NettyClient {
    private final NettyServerInitializer initializer = new NettyServerInitializer();
    private final Bootstrap bootstrap = new Bootstrap();
    public final static String decollator = ":";
    private final static byte[] data = new byte[]{0x00, 0x03, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04, 0x00, 0x00, 0x00, 0x01};

    public NettyClient() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(initializer);
    }

    /**
     * 连接服务器, 如果已经连接就覆盖，断开以前连接
     *
     * @param ip   ip
     * @param port 端口
     * @return {@link NettyServerHandler}
     */
    public NettyServerHandler connect(String ip, int port, MessageListener listener) throws InterruptedException {
        NettyServerHandler serverHandler = initializer.getServerHandler();
        bootstrap.connect(ip, port)
                .sync()
                .addListener(it -> initializer.getServerHandler().addListener(ip + decollator + port, listener))
                .channel();
        return serverHandler;
    }


    public NettyServerHandler connect(String ip, int port, MessageListener onMessage, MessageListener onConnect) throws InterruptedException {
        NettyServerHandler serverHandler = initializer.getServerHandler();
        bootstrap.connect(ip, port)
                .sync()
                .addListener(it -> {
                    initializer.getServerHandler().addListener(ip + decollator + port, onMessage);
                    onConnect.onMessage(null);
                })
                .channel();
        return serverHandler;
    }

    /**
     * 查看是否已经连接过
     *
     * @param ip   id地址
     * @param port 端口
     * @return true:已经连接过
     */
    public boolean exist(String ip, int port) {
        return initializer.getServerHandler().exist(ip + decollator + port);
    }

    /**
     * 发送获取流量消息，异步
     *
     * @param ip   ip地址
     * @param port 端口
     */
    public ChannelFuture sendAsyncGainValue(String ip, int port) {
        NettyServletResponse response = new NettyServletResponse(data);
        return initializer.getServerHandler().send(ip + decollator + port, response);
    }


}
