package com.gyh.gis;

import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.dto.resp.DeviceStatusResp;
import com.gyh.gis.enums.StateEnum;
import com.gyh.gis.service.DeviceStatusService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        req.setAlarmValue(1.2f);
        req.setCancelAlarm(false);
        req.setCancelTime(new Date());
        req.setScreenshotUrl("baidu.com");
        Integer insert = deviceStatusService.insert(req);
        log.info("id = {}", insert);
        DeviceStatusResp byId = deviceStatusService.getById(insert);
        log.info(byId.toString());
    }

}
