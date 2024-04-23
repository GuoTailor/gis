package com.gyh.gis;

import com.gyh.gis.domain.*;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.dto.resp.DeviceStatusResp;
import com.gyh.gis.enums.PeriodEnum;
import com.gyh.gis.enums.StateEnum;
import com.gyh.gis.schedule.StationDataTask;
import com.gyh.gis.service.DeviceHistoryData;
import com.gyh.gis.service.DeviceStatusService;
import com.gyh.gis.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@SpringBootTest
class GisApplicationTests {
    @Autowired
    private DeviceStatusService deviceStatusService;
    @Autowired
    private StationService stationService;
    @Autowired
    private StationDataTask stationDataTask;
    @Autowired
    private DeviceHistoryData deviceHistoryData;

    @Test
    void contextLoads() {
        DeviceStatusInsertReq req = new DeviceStatusInsertReq();
        req.setStationId(1);
        req.setErrorState(StateEnum.NORMAL);
        req.setValue(BigDecimal.valueOf(1.2));
        req.setScreenshotUrl("baidu.com");
        Integer insert = deviceStatusService.insert(req);
        log.info("id = {}", insert);
        DeviceStatusResp byId = deviceStatusService.getById(insert);
        log.info(byId.toString());

        DeviceStatus deviceStatus = new DeviceStatus();
        BeanUtils.copyProperties(byId, deviceStatus);
        deviceStatusService.update(deviceStatus);
    }

    @Test
    public void testSelect() {
        var date = LocalDate.now().minusDays(2);
        log.info("开始统计 {} 每个站点流量》》》》》》》》》》》", date);
        List<Station> stationIds = stationService.getAll();
        stationIds.parallelStream().forEach(it -> {
            var device10minuteHistories = deviceHistoryData.selectByOneDay(date, it.getId());
            log.info("开始统计 {} 站 {} 天流量 size {}", date, it.getId(), device10minuteHistories.size());
            if (!CollectionUtils.isEmpty(device10minuteHistories)) {
                BigDecimal sum = device10minuteHistories
                        .stream()
                        .map(Device10minuteHistory::getValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal flow = sum.divide(new BigDecimal(device10minuteHistories.size()), 3, RoundingMode.HALF_UP);
                DeviceDayHistory dayHistory = new DeviceDayHistory();
                dayHistory.setTime(date);
                dayHistory.setValue(flow);
                dayHistory.setStationId(device10minuteHistories.get(0).getStationId());
//                int i = deviceHistoryData.addDeviceHistoryData(dayHistory);
//                if (i == 0) log.warn("统计站点 {} 数据出错 value:{}", dayHistory.getId(), flow);
            }
            log.info("开始统计 {} 站 {}天达标率", date, it.getStation());
            ExamineInfo examineInfo = new ExamineInfo();
            examineInfo.setStationId(it.getId());
            examineInfo.setHystCode(it.getCode());
            examineInfo.setAssPer(PeriodEnum.DAY);
            LocalDateTime now = LocalDateTime.now();
            examineInfo.setRecTime(now);
            var startTime = date.atStartOfDay();
            var endTime = date.atTime(LocalTime.MAX);
        });
    }

    @Test
    public void testCount() {
        stationDataTask.configureTasks();
    }
}
