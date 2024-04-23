package com.gyh.gis.netty.client;

import com.gyh.gis.netty.NettyServletRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by GYH on 2018/12/3.
 */
public class MessageDecoder extends ByteToMessageDecoder {
    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private final int BODY_LENGTH = 11;
    static final int BODY_INDEX = 5;    //包长度的位置

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        System.out.println("查找 in :"+ in.readableBytes() +"  w:" +  in.writerIndex());
        int readable = in.readableBytes();
        byte[] data = new byte[readable];
        if (in.isReadable() && readable >= BODY_LENGTH) {
            in.readBytes(data);
            logger.info(Arrays.toString(data));

            NettyServletRequest inputMessage = new NettyServletRequest(data, ctx);
            out.add(inputMessage);
        }
    }

}

