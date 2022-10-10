package com.gyh.gis;

import com.gyh.gis.domain.DeviceStatus;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.dto.resp.DeviceStatusResp;
import com.gyh.gis.enums.StateEnum;
import com.gyh.gis.service.DeviceStatusService;
import com.gyh.gis.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@SpringBootTest
class GisApplicationTests {
    @Autowired
    private DeviceStatusService deviceStatusService;
    @Autowired
    private StationService stationService;

    @Test
    void contextLoads() {
        DeviceStatusInsertReq req = new DeviceStatusInsertReq();
        req.setStationId(1);
        req.setErrorState(StateEnum.NORMAL);
        req.setAlarmState(StateEnum.ALARM);
        req.setErrorTime(LocalDateTime.now());
        req.setAlarmTime(LocalDateTime.now());
        req.setValue(BigDecimal.valueOf(1.2));
        req.setCancelAlarm(false);
        req.setCancelTime(LocalDateTime.now());
        req.setScreenshotUrl("baidu.com");
        Integer insert = deviceStatusService.insert(req);
        log.info("id = {}", insert);
        DeviceStatusResp byId = deviceStatusService.getById(insert);
        log.info(byId.toString());

        DeviceStatus deviceStatus = new DeviceStatus();
        BeanUtils.copyProperties(byId, deviceStatus);
        deviceStatus.setCancelAlarm(null);
        deviceStatus.setScreenshotUrl(null);
        deviceStatusService.update(deviceStatus);
    }

    @Test
    public void testSelect() {
        List<Integer> objects = stationService.selectAll();
        System.out.println(objects);
        System.out.println(objects.get(0).getClass().getName());
    }

}
