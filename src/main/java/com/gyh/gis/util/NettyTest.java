package com.gyh.gis.util;

import com.gyh.gis.netty.NettyClient;

import java.net.ConnectException;

/**
 * create by GYH on 2022/10/11
 */
public class NettyTest {
    public static void main(String[] args) throws InterruptedException {
        NettyClient client = new NettyClient();
        client.connect("192.168.0.103", 8080, System.out::println);
        client.connect("192.168.0.103", 8081, System.out::println);
        try {
            client.connect("192.168.0.103", 8083, System.out::println);
        } catch (Exception e) {
            System.out.println(">>>>");
            e.printStackTrace();
        }

        Thread.sleep(1000);
        client.sendAsyncGainValue("192.168.0.103", 8080);
        client.sendAsyncGainValue("192.168.0.103", 8081);

    }
}
