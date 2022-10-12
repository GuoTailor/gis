package com.gyh.gis.netty;

import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;

/**
 * Created by GYH on 2018/12/4.
 */
public class NettyServletRequest {
    private byte[] body;
    private ChannelHandlerContext ctx;

    public NettyServletRequest(byte[] body, ChannelHandlerContext ctx) {
        this.body = body;
        this.ctx = ctx;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public String toString() {
        return "NettyServletRequest{" +
                "body=" + Arrays.toString(body) +
                ", ctx=" + ctx +
                '}';
    }
}
