package com.gyh.gis.netty.client;

import com.gyh.gis.netty.MessageListener;
import com.gyh.gis.netty.NettyServletRequest;
import com.gyh.gis.netty.NettyServletResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;


/**
 * create by GYH on 2022/10/11
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class.getSimpleName());

    private final ConcurrentHashMap<String, NettyContext> cache = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        var inet = ((InetSocketAddress) ctx.channel().remoteAddress());
        String address = inet.getAddress().getHostAddress();
        int port = inet.getPort();
        logger.info("通道打开{} {}", address, port);
        String key = address + NettyClient.decollator + port;

        NettyContext nettyContext = new NettyContext();
        nettyContext.cxt = ctx;
        NettyContext old = cache.putIfAbsent(key, nettyContext);
        logger.info("{}++{}", key, old == null);
        if (old != null) {
            if (old.cxt != null) old.cxt.channel().close();
            old.cxt = ctx;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        var inet = ((InetSocketAddress) ctx.channel().remoteAddress());
        String address = inet.getAddress().getHostAddress();
        int port = inet.getPort();
        logger.info("连接关闭{} {}", address, port);
        cache.forEach((k, value) -> {
            if (value.cxt.equals(ctx)) cache.remove(k);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        var inet = ((InetSocketAddress) ctx.channel().remoteAddress());
        String address = inet.getAddress().getHostAddress();
        int port = inet.getPort();
        logger.info("连接异常{} {}", address, port);
        cache.forEach((k, value) -> {
            if (value.cxt.equals(ctx)) cache.remove(k);
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        logger.info("读取数据 {}", msg);
        var inet = ((InetSocketAddress) ctx.channel().remoteAddress());
        String address = inet.getAddress().getHostAddress();
        int port = inet.getPort();
        String key = address + NettyClient.decollator + port;
        NettyContext nettyContext = cache.get(key);
        logger.info("{} 读取数据 {}", key, nettyContext == null);
        if (nettyContext != null && nettyContext.listener != null)
            nettyContext.listener.onMessage((NettyServletRequest) msg);
    }

    public void addListener(String ipPort, MessageListener listener) {
        NettyContext nettyContext = new NettyContext();
        nettyContext.listener = listener;
        NettyContext old = cache.putIfAbsent(ipPort, nettyContext);
        logger.info("{} 添加监听 {}", ipPort, old == null);
        if (old != null) old.listener = listener;
    }

    /**
     * 发送消息
     *
     * @param ipPort   ip：端口
     * @param response 消息
     */
    public ChannelFuture send(String ipPort, NettyServletResponse response) {
        NettyContext nettyContext = cache.get(ipPort);
        if (nettyContext == null) {
            return null;
        }
        return nettyContext.cxt.channel().writeAndFlush(response);
    }

    public boolean exist(String ipPort) {
        return cache.containsKey(ipPort);
    }

    public void close(String ipPort) {
        NettyContext nettyContext = cache.remove(ipPort);
        if (nettyContext != null) {
            nettyContext.cxt.channel().close();
        }
    }

}
