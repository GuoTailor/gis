package com.gyh.gis.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by GYH on 2018/12/3.
 */
@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<NettyServletResponse> {
    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());


    @Override
    protected void encode(ChannelHandlerContext ctx, NettyServletResponse msg, ByteBuf out) {

        byte[] data = msg.getData();
        logger.info("编码 " + Arrays.toString(data));
        out.writeBytes(data);
    }

}
