package com.gyh.gis.schedule;

import com.gyh.gis.domain.*;
import com.gyh.gis.enums.PeriodEnum;
import com.gyh.gis.service.DeviceHistoryData;
import com.gyh.gis.service.ExamineInfoService;
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
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * create by GYH on 2022/9/27
 */
@Slf4j
@Component
public class StationDataTask {
    @Autowired
    private StationService stationService;
    @Autowired
    private DeviceHistoryData deviceHistoryData;
    @Autowired
    private TargetRateService targetRateService;
    @Autowired
    private ExamineInfoService examineInfoService;

    @Scheduled(cron = "5 0 4 * * ?")
    public void configureTasks() {
        var date = LocalDate.now().minusDays(1);
        log.info("开始统计 {} 每个站点流量》》》》》》》》》》》", date);
        List<Station> stationIds = stationService.getAll();
        for (Station it : stationIds) {
            try {
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
                    int i = deviceHistoryData.addDeviceHistoryData(dayHistory);
                    if (i == 0) log.warn("统计站点 {} 数据出错 value:{}", dayHistory.getId(), flow);
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
                stationRange(startTime, endTime, examineInfo, it.getFlow());
                examineInfo.setStationCount(1);
                examineInfoService.insert(examineInfo);
                processingMonth(examineInfo, it.getFlow());
                processingYear(examineInfo, it.getFlow());
            } catch (Exception exception) {
                log.info("统计站点 {} 数据出错", it.getStation(), exception);
            }
        }
        log.info("结束统计 {} 每个站点流量》》》》》》》》》》》", date);
    }

    /**
     * 处理统计月
     */
    public void processingMonth(ExamineInfo examineInfo, BigDecimal vouchFlow) {
        var data = LocalDate.now().minusDays(1);
        int dayOfMonth = data.getDayOfMonth();
        var startTime = data.minusDays(dayOfMonth - 1).atStartOfDay();
        var endTime = startTime.plusDays(data.lengthOfMonth()).minusNanos(1);
        processing(startTime, endTime, examineInfo, vouchFlow, PeriodEnum.MONTH);
    }

    /**
     * 处理统计年
     */
    public void processingYear(ExamineInfo examineInfo, BigDecimal vouchFlow) {
        var data = LocalDate.now().minusDays(1);
        int dayOfYear = data.getDayOfYear();
        var startTime = data.minusDays(dayOfYear - 1).atStartOfDay();
        var endTime = startTime.plusDays(data.lengthOfYear()).minusNanos(1);
        processing(startTime, endTime, examineInfo, vouchFlow, PeriodEnum.YEAR);
    }

    private void processing(LocalDateTime startTime, LocalDateTime endTime, ExamineInfo examineInfo, BigDecimal vouchFlow, PeriodEnum per) {
        ExamineInfo examineInfoByYear = examineInfoService.selectByTime(startTime, endTime, per, examineInfo.getStationId());
        // 如果为null说明这个周期一次都还没有生成
        if (examineInfoByYear == null) {
            examineInfoByYear = new ExamineInfo();
            examineInfoByYear.setAssPer(per);
            examineInfoByYear.setStationCount(1);
            examineInfoByYear.setStationId(examineInfo.getStationId());
            examineInfoByYear.setHystCode(examineInfo.getHystCode());
            examineInfoByYear.setAssStart(examineInfo.getAssStart());
            examineInfoByYear.setAssEnd(examineInfo.getAssEnd());
            examineInfoByYear.setRecTime(LocalDateTime.now());
            int dayOfPer = 0;
            if (per == PeriodEnum.MONTH) {
                dayOfPer = LocalDate.now().getDayOfMonth();
            } else if (per == PeriodEnum.YEAR) {
                dayOfPer = LocalDate.now().getDayOfYear();
            }
            // 如果是第一天，就不用重复统计了，当前examineInfo就是这一天的
            // 否则就是之前的没统计到，需要重新从第一天开始统计
            if (dayOfPer == 1) {
                examineInfoByYear.setEcoFlow(examineInfo.getEcoFlow());
                examineInfoByYear.setEcoOnline(examineInfo.getEcoOnline());
            } else {
                stationRange(startTime, endTime, examineInfoByYear, vouchFlow);
            }
            examineInfoService.insert(examineInfoByYear);
        } else {
            examineInfoByYear.setAssEnd(examineInfo.getAssEnd());
            examineInfoByYear.setRecTime(LocalDateTime.now());
            if (examineInfoByYear.getFlowTargetRate() == null) {
                examineInfoByYear.setFlowTargetRate(BigDecimal.ZERO);
            }
            BigDecimal flowTargetRate = examineInfoByYear.getFlowTargetRate()
                    .multiply(new BigDecimal(examineInfoByYear.getStationCount()))
                    .add(examineInfo.getFlowTargetRate())
                    .divide(new BigDecimal(examineInfoByYear.getStationCount() + 1), 4, RoundingMode.HALF_UP);
            examineInfoByYear.setFlowTargetRate(flowTargetRate);
            examineInfoByYear.setEcoFlow(flowTargetRate.compareTo(new BigDecimal("0.997")) >= 0);

            BigDecimal onlineTargetRate = examineInfoByYear.getOnlineTargetRate()
                    .multiply(new BigDecimal(examineInfoByYear.getStationCount()))
                    .add(examineInfo.getOnlineTargetRate())
                    .divide(new BigDecimal(examineInfoByYear.getStationCount() + 1), 4, RoundingMode.HALF_UP);
            examineInfoByYear.setOnlineTargetRate(onlineTargetRate);
            examineInfoByYear.setEcoOnline(onlineTargetRate.compareTo(new BigDecimal("0.997")) >= 0);

            examineInfoByYear.setStationCount(examineInfoByYear.getStationCount() + 1);
            examineInfoService.update(examineInfoByYear);
        }
    }

    /**
     * 从最小单位周期统计某个范围的数据
     */
    private void stationRange(LocalDateTime startTime, LocalDateTime endTime, ExamineInfo examineInfo, BigDecimal vouchFlow) {
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
        var device10minuteHistories = deviceHistoryData.selectByRange(startTime, endTime, examineInfo.getStationId());
        if (!CollectionUtils.isEmpty(device10minuteHistories)) {
            long count = device10minuteHistories
                    .stream()
                    .peek(device10minuteHistory -> consumer.accept(device10minuteHistory.getTime()))
                    .filter(it -> it.getValue().compareTo(vouchFlow) >= 0)
                    .count();
            BigDecimal flow = new BigDecimal(count).divide(new BigDecimal(device10minuteHistories.size()), 4, RoundingMode.HALF_UP);
            examineInfo.setEcoFlow(count == device10minuteHistories.size());
            examineInfo.setFlowTargetRate(flow);
        }
        List<TargetRate> targetRates = targetRateService.selectByRange(startTime, endTime, examineInfo.getStationId());
        if (!CollectionUtils.isEmpty(targetRates)) {
            Float sum = targetRates.stream()
                    .peek(targetRate -> consumer.accept(targetRate.getDatatime()))
                    .map(TargetRate::getTargetRate)
                    .reduce(0F, Float::sum);
            Float targetRate = sum / targetRates.size();
            examineInfo.setEcoOnline(targetRate.compareTo(1F) >= 0);
            examineInfo.setOnlineTargetRate(new BigDecimal(targetRate).setScale(4, RoundingMode.HALF_UP));
        } else {
            consumer.accept(LocalDateTime.now());
            examineInfo.setEcoOnline(false);
            examineInfo.setOnlineTargetRate(BigDecimal.ZERO);
        }
    }

}
