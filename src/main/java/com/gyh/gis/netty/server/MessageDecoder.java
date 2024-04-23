package com.gyh.gis.netty.server;

import com.gyh.gis.netty.NettyServletRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by GYH on 2018/12/3.
 */
public class MessageDecoder extends ByteToMessageDecoder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final byte start = 0x68;
    private final byte end = 0x16;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        System.out.println("查找 in :"+ in.readableBytes() +"  w:" +  in.writerIndex());
        int readable = in.readableBytes();
        byte startByte = in.readByte();
        if (startByte != start) {
            in.skipBytes(in.readableBytes());
            logger.info("协议不正确，丢弃所有字节");
            return;
        }
        byte length = in.readByte();
        logger.info("startByte: {}, length:{}", startByte, length);
        byte[] data = new byte[length + 1];
        if (in.isReadable() && readable >= length + 5) {
            in.readBytes(data);
            logger.info(Arrays.toString(data));
            NettyServletRequest inputMessage = new NettyServletRequest(data, ctx);
            out.add(inputMessage);
            byte crc = in.readByte();
            byte endBytes = in.readByte();
            logger.info("crc:{}  end:{}", crc, endBytes);
            if (end != endBytes) {
                in.skipBytes(in.readableBytes());
                logger.info("协议不正确，丢弃所有字节");
            }
        }
    }

}

