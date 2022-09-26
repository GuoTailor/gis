package com.gyh.gis;

import com.gyh.gis.domain.DeviceStatus;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.dto.resp.DeviceStatusResp;
import com.gyh.gis.enums.StateEnum;
import com.gyh.gis.service.DeviceStatusService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@SpringBootTest
class GisApplicationTests {
    @Autowired
    private DeviceStatusService deviceStatusService;

    @Test
    void contextLoads() {
        DeviceStatusInsertReq req = new DeviceStatusInsertReq();
        req.setStationId(1);
        req.setErrorState(StateEnum.NORMAL);
        req.setAlarmState(StateEnum.ALARM);
        req.setErrorTime(new Date());
        req.setAlarmTime(new Date());
        req.setAlarmValue(BigDecimal.valueOf(1.2));
        req.setCancelAlarm(false);
        req.setCancelTime(new Date());
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

}
