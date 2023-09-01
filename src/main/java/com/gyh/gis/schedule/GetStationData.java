package com.gyh.gis.schedule;

import com.gyh.gis.domain.Station;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.enums.StateEnum;
import com.gyh.gis.netty.NettyClient;
import com.gyh.gis.netty.NettyServletRequest;
import com.gyh.gis.service.DeviceStatusService;
import com.gyh.gis.service.StationService;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * create by GYH on 2022/10/12
 */
@Slf4j
@Component
public class GetStationData {
    @Autowired
    private StationService stationService;
    @Autowired
    private NettyClient nettyClient;
    @Autowired
    private DeviceStatusService deviceStatusService;

    @Scheduled(cron = "1 0/10 * * * ?")
    public void getData() {
        GenericFutureListener<? extends Future<? super Void>> listener = future -> {
            if (future.isSuccess()) {
                log.info("发送成功");
            } else {
                log.error("发送失败");
            }
        };

        List<Station> stations = stationService.getAll();
        stations.parallelStream()
                .filter(it -> StringUtils.hasLength(it.getIp()) && it.getPort() != null)
                .forEach(it -> {
                    if (!nettyClient.exist(it.getIp(), it.getPort())) {
                        try {
                            nettyClient.connect(it.getIp(), it.getPort(),
                                    request -> saveData(false, request, it),
                                    request -> nettyClient.sendAsyncGainValue(it.getIp(), it.getPort())
                                            .addListener(listener)
                            );
                        } catch (Exception e) {
                            saveData(true, null, it);
                            log.error(e.getLocalizedMessage(), e);
                        }
                    } else {
                        ChannelFuture channelFuture = nettyClient.sendAsyncGainValue(it.getIp(), it.getPort());
                        channelFuture.addListener(listener);
                    }
                });
    }

    private void saveData(boolean isError, NettyServletRequest request, Station station) {
        DeviceStatusInsertReq req = new DeviceStatusInsertReq();
        req.setStationId(station.getId());
        if (!isError) {
            byte[] body = request.getBody();
            int length = body[8];
            int data = 0;
            for (int i = 1; i <= length; i++) {
                data = (data << 8) + body[8 + i];
            }
            double flow = (data * 0.001 - 4) / 16 * station.getRange().doubleValue();
            req.setErrorState(StateEnum.NORMAL);
            req.setValue(new BigDecimal(flow));
        } else {
            req.setErrorState(StateEnum.ERROR);
        }
        deviceStatusService.insertOrUpdate(req);
    }
}
