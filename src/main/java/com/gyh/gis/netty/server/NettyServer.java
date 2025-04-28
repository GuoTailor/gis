package com.gyh.gis.netty.server;

import com.gyh.gis.domain.Station;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.enums.StateEnum;
import com.gyh.gis.mapper.StationMapper;
import com.gyh.gis.service.DeviceStatusService;
import com.gyh.gis.service.TargetRateService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * create by GYH on 2024/4/19
 */
@Slf4j
@Service
public class NettyServer implements InitializingBean {
    @Autowired
    private DeviceStatusService deviceStatusService;
    @Autowired
    private TargetRateService targetRateService;
    @Resource
    private StationMapper stationMapper;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NettyServerInitializer bind(int port) throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        NettyServerInitializer childHandler = new NettyServerInitializer();
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(childHandler);

        log.info("netty server start {} success!", port);
        serverBootstrap.bind(port).sync();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdown();
            log.info("关闭netty服务端：{}", port);
        }));
        return childHandler;
    }

    public void shutdown() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
            bossGroup = null;
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (bossGroup != null) {
            return;
        }
        NettyServerInitializer bind = bind(9000);
        bind.getServerHandler().addListener(request -> {
            byte[] body = request.getBody();
            String code = HexUtils.toHexString(Arrays.copyOfRange(body, 2, 7));
            if (body[1] == (byte) 0xB0) {
                log.info("{} 心跳", code);
                return;
            }
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
            BigDecimal flow = new BigDecimal(sb.toString());
            log.info("站点编码 {} 流量 {}", code, flow);
            Station bySysCode = stationMapper.getBySysCode(code);
            if (bySysCode == null) {
                log.info("站点编码 {} 不存在", code);
                return;
            }
            DeviceStatusInsertReq req = new DeviceStatusInsertReq();
            req.setStationId(bySysCode.getId());
            req.setErrorState(StateEnum.NORMAL);
            req.setValue(flow);
            req.setCreateTime(localDateTime);
            deviceStatusService.insertOrUpdate(req);
            targetRateService.statistic(bySysCode.getId(), true);
        });
    }
}
