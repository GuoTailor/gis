package com.gyh.gis.netty;

/**
 * Created by GYH on 2018/12/13.
 */
public class NettyServletResponse {
    private byte[] data;

    public NettyServletResponse(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
