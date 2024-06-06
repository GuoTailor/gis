package com.gyh.gis.schedule;

import com.gyh.gis.domain.Station;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.enums.StateEnum;
import com.gyh.gis.netty.NettyServletRequest;
import com.gyh.gis.netty.client.NettyClient;
import com.gyh.gis.service.DeviceStatusService;
import com.gyh.gis.service.StationService;
import com.gyh.gis.service.TargetRateService;
import com.gyh.gis.util.ThreadManager;
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
    @Autowired
    private TargetRateService targetRateService;

    @Scheduled(cron = "1 0/10 * * * ?")
    public void getData() {
        log.info("定时任务开始获取水电站流量》》》》》》》》》》》");
        List<Station> stations = stationService.getAll();
        stations.stream()
                .filter(it -> StringUtils.hasLength(it.getIp()) && it.getPort() != null)
                .filter(it -> !it.getIsPush())
                .forEach(it -> ThreadManager.getWorkPool().execute(() -> {
                    log.info("{} 获取数据", it.getId());
                    GenericFutureListener<? extends Future<? super Void>> listener = future -> {
                        log.info("{} 发送{}", it.getId(), future.isSuccess() ? "成功" : "失败");
                        targetRateService.statistic(it.getId(), future.isSuccess());
                    };
                    if (!nettyClient.exist(it.getIp(), it.getPort())) {
                        try {
                            nettyClient.connect(it.getIp(), it.getPort(),
                                    request -> saveData(false, request, it),
                                    request -> nettyClient.sendAsyncGainValue(it.getIp(), it.getPort())
                                            .addListener(listener)
                            );
                        } catch (Exception e) {
                            saveData(true, null, it);
                            targetRateService.statistic(it.getId(), false);
                            log.error(e.getLocalizedMessage(), e);
                        }
                    } else {
                        ChannelFuture channelFuture = nettyClient.sendAsyncGainValue(it.getIp(), it.getPort());
                        channelFuture.addListener(listener);
                    }
                }));
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
            double flow;
            if (data > 4000) {
                flow = (data * 0.001 - 4) / 16 * station.getRange().doubleValue();
            } else {
                flow = 0;
            }
            req.setErrorState(StateEnum.NORMAL);
            req.setValue(new BigDecimal(flow));
        } else {
            req.setErrorState(StateEnum.ERROR);
        }
        deviceStatusService.insertOrUpdate(req);
    }
}
