package com.gyh.gis.schedule;

import com.gyh.gis.domain.Device10minuteHistory;
import com.gyh.gis.domain.DeviceDayHistory;
import com.gyh.gis.service.DeviceHistoryData;
import com.gyh.gis.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * create by GYH on 2022/9/27
 */
@Slf4j
@Component
public class ToDayDeviceData {
    @Autowired
    private StationService stationService;
    @Autowired
    private DeviceHistoryData deviceHistoryData;

    @Scheduled(cron = "1 0 0 * * ?")
    private void configureTasks() {
        var date = LocalDate.now().minusDays(1);
        log.info("开始统计 {} 每个站点流量", date);
        List<Integer> stationIds = stationService.selectAll();
        stationIds.parallelStream().forEach(it -> {
            var device10minuteHistories = deviceHistoryData.selectByOneDay(date, it);
            if (CollectionUtils.isEmpty(device10minuteHistories)) return;
            double sum = device10minuteHistories
                    .stream()
                    .map(Device10minuteHistory::getValue)
                    .mapToDouble(Float::doubleValue)
                    .sum();
            DeviceDayHistory dayHistory = new DeviceDayHistory();
            dayHistory.setTime(date);
            dayHistory.setValue((float) sum);
            dayHistory.setStationId(device10minuteHistories.get(0).getStationId());
            deviceHistoryData.addDeviceHistoryData(dayHistory);
        });
    }

}
