package com.gyh.gis.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * create by GYH on 2024/4/19
 */
@Slf4j
@Service
public class NettyServer implements InitializingBean {
    public NettyServerInitializer bind(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        NettyServerInitializer childHandler = new NettyServerInitializer();
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(childHandler);

        log.info("netty server start {} success!", port);
        serverBootstrap.bind(port).sync();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("关闭netty服务端：{}", port);
        }));
        return childHandler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        NettyServerInitializer bind = new NettyServer().bind(9000);
        bind.getServerHandler().addListener(request -> {
            byte[] body = request.getBody();
            log.info("onMessage:{}", body);
            String code = HexUtils.toHexString(Arrays.copyOfRange(body, 2, 7));
            log.info(code);
            String day = Integer.toHexString(body[25]);
            String hour = Integer.toHexString(body[24]);
            String minute = Integer.toHexString(body[23]);
            String second = Integer.toHexString(body[22]);
            log.info("day:{},hour:{},minute:{},second:{}", day, hour, minute, second);
            LocalDateTime localDateTime = LocalDate.now().withDayOfMonth(Integer.parseInt(day))
                    .atTime(Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(second));
            log.info("localDateTime:{}", localDateTime);
            byte flag = (byte) ((body[12] & 0xf0) >> 4);
            String num2 = HexUtils.toHexString(new byte[]{body[11], body[10], body[9], body[8]});
            StringBuilder sb = new StringBuilder(num2);
            sb.insert(5, '.');
            while (sb.charAt(0) == '0') {
                sb.deleteCharAt(0);
            }
            if (flag == 1) {
                sb.insert(0, "-");
            }
            float flow = Float.parseFloat(sb.toString());
            System.out.println(flow);
        });
    }
}
