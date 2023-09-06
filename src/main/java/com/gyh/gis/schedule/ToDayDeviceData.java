package com.gyh.gis.schedule;

import com.gyh.gis.domain.*;
import com.gyh.gis.enums.PeriodEnum;
import com.gyh.gis.service.DeviceHistoryData;
import com.gyh.gis.service.StationService;
import com.gyh.gis.service.TargetRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

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
    @Autowired
    private TargetRateService targetRateService;

    @Scheduled(cron = "1 0 0 * * ?")
    public void configureTasks() {
        var date = LocalDate.now().minusDays(1);
        log.info("开始统计 {} 每个站点流量", date);
        List<Integer> stationIds = stationService.selectAllId();
        stationIds.parallelStream().forEach(it -> {
            var device10minuteHistories = deviceHistoryData.selectByOneDay(date, it);
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
                int i = deviceHistoryData.addDeviceHistoryData(dayHistory);
                if (i == 0) log.warn("统计站点 {} 数据出错 value:{}", dayHistory.getId(), flow);
            }
        });

    }

    @Scheduled(cron = "2 0 0 * * ?")
    public void processingData() {
        var date = LocalDate.now().minusDays(1);
        log.info("开始统计 {} 每个站点达标率", date);
        List<Station> stationIds = stationService.getAll();
        stationIds.parallelStream().forEach(it -> {
            ExamineInfo examineInfo = new ExamineInfo();
            examineInfo.setStationId(it.getId());
            examineInfo.setAssPer(PeriodEnum.DAY);
            LocalDateTime now = LocalDateTime.now();
            examineInfo.setRecTime(now);
            Consumer<LocalDateTime> consumer = (time) -> {
                if (examineInfo.getAssStart() == null) {
                    examineInfo.setAssStart(time);
                } else if (time.isBefore(examineInfo.getAssStart())) {
                    examineInfo.setAssStart(time);
                }
                if (examineInfo.getAssEnd() == null) {
                    examineInfo.setAssEnd(time);
                } else if (time.isAfter(examineInfo.getAssEnd())) {
                    examineInfo.setAssEnd(time);
                }
            };
            var device10minuteHistories = deviceHistoryData.selectByOneDay(date, it.getId());
            if (!CollectionUtils.isEmpty(device10minuteHistories)) {
                BigDecimal sum = device10minuteHistories
                        .stream()
                        .peek(device10minuteHistory -> consumer.accept(device10minuteHistory.getTime()))
                        .map(Device10minuteHistory::getValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal flow = sum.divide(new BigDecimal(device10minuteHistories.size()), 3, RoundingMode.HALF_UP);
                examineInfo.setEcoFlow(it.getFlow().compareTo(flow) <= 0);
            }
            List<TargetRate> targetRates = targetRateService.selectByOneDay(date, it.getId());
            if (!CollectionUtils.isEmpty(targetRates)) {
                Float sum = targetRates.stream()
                        .peek(targetRate -> consumer.accept(targetRate.getDatatime()))
                        .map(TargetRate::getTargetRate)
                        .reduce(0F, Float::sum);
                Float targetRate = sum / targetRates.size();
                examineInfo.setEcoOnline(targetRate.compareTo(100F) >= 0);
            }
        });
    }

    

}
