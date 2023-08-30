package com.gyh.gis.schedule;

import com.gyh.gis.domain.Station;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.enums.StateEnum;
import com.gyh.gis.netty.NettyClient;
import com.gyh.gis.netty.NettyServletRequest;
import com.gyh.gis.service.DeviceStatusService;
import com.gyh.gis.service.StationService;
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
        List<Station> stations = stationService.getAll();
        stations.parallelStream()
                .filter(it -> StringUtils.hasLength(it.getIp()) && it.getPort() != null)
                .forEach(it -> {
                    if (!nettyClient.exist(it.getIp(), it.getPort())) {
                        try {
                            nettyClient.connect(it.getIp(), it.getPort(),
                                    request -> saveData(false, request, it),
                                    request -> nettyClient.sendAsyncGainValue(it.getIp(), it.getPort()));
                        } catch (Exception e) {
                            saveData(true, null, it);
                            e.printStackTrace();
                        }
                    } else {
                        nettyClient.sendAsyncGainValue(it.getIp(), it.getPort());
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
