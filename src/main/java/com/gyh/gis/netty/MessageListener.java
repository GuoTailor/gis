package com.gyh.gis.netty;

/**
 * create by GYH on 2022/10/11
 */
public interface MessageListener {
    void onMessage(NettyServletRequest request);
}
