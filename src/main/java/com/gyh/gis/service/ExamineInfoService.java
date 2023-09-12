package com.gyh.gis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gyh.gis.domain.Device10minuteHistory;
import com.gyh.gis.domain.ExamineInfo;
import com.gyh.gis.domain.Station;
import com.gyh.gis.domain.TargetRate;
import com.gyh.gis.dto.req.ExamineReq;
import com.gyh.gis.dto.req.TrendReq;
import com.gyh.gis.dto.resp.ExamineResp;
import com.gyh.gis.dto.resp.StatisticResp;
import com.gyh.gis.enums.PeriodEnum;
import com.gyh.gis.mapper.ExamineInfoMapper;
import com.gyh.gis.mapper.StationMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * create by GYH on 2023/9/7
 */
@Service
public class ExamineInfoService {
    @Resource
    private ExamineInfoMapper examineInfoMapper;
    @Autowired
    private TargetRateService targetRateService;
    @Autowired
    private DeviceHistoryData deviceHistoryData;
    @Resource
    private StationMapper stationMapper;

    public int insert(ExamineInfo examineInfo) {
        return examineInfoMapper.insert(examineInfo);
    }

    public int update(ExamineInfo examineInfo) {
        return examineInfoMapper.updateById(examineInfo);
    }

    public ExamineInfo selectByTime(LocalDateTime startTime, LocalDateTime endTime, PeriodEnum per, Integer stationId) {
        return examineInfoMapper.selectOne(new QueryWrapper<ExamineInfo>()
                .ge("ass_start", startTime)
                .le("ass_end", endTime)
                .eq("ass_per", per)
                .eq("station_id", stationId));
    }

    /**
     * 查询小时统计
     */
    public List<StatisticResp> selectStatisticByHour(ExamineReq req) {
        List<Station> stations = stationMapper.selectList(new LambdaQueryWrapper<>(Station.class)
                .eq(StringUtils.hasText(req.getStation()), Station::getStation, req.getStation())
                .eq(StringUtils.hasText(req.getArea()), Station::getArea, req.getArea()));
        var startTime = req.getTime().withMinute(0).withSecond(0).withNano(0);
        var enTime = startTime.plusHours(1).minusNanos(1);
        return stations.parallelStream().map(station -> {
            TargetRate targetRates = targetRateService.selectByStationIdAndTime(startTime, station.getId());
            List<Device10minuteHistory> device10minuteHistories = deviceHistoryData.selectByRange(startTime, enTime, station.getId());
            BigDecimal flow = device10minuteHistories.stream().map(Device10minuteHistory::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
            StatisticResp statisticResp = new StatisticResp();
            statisticResp.setArea(station.getArea());
            statisticResp.setStation(station.getStation());
            statisticResp.setStationId(station.getId());
            statisticResp.setTime(startTime);
            statisticResp.setOnlineTargetRate(BigDecimal.valueOf(targetRates.getTargetRate()));
            statisticResp.setFlowTargetRate(flow.divide(new BigDecimal(device10minuteHistories.size()), 3, RoundingMode.HALF_UP));
            return statisticResp;
        }).collect(Collectors.toList());
    }

    public List<StatisticResp> selectStatisticByDay(ExamineReq req) {
        var startTime = req.getTime().toLocalDate();
        var endTime = startTime.plusDays(1);
        return selectStatistic(req, startTime, endTime, PeriodEnum.DAY);
    }

    public List<StatisticResp> selectStatisticByMonth(ExamineReq req) {
        var startTime = req.getTime().toLocalDate().withDayOfMonth(1);
        var endTime = startTime.plusMonths(1);
        return selectStatistic(req, startTime, endTime, PeriodEnum.MONTH);
    }

    public List<StatisticResp> selectStatisticByYear(ExamineReq req) {
        var startTime = req.getTime().toLocalDate().withDayOfYear(1);
        var endTime = startTime.plusYears(1);
        return selectStatistic(req, startTime, endTime, PeriodEnum.YEAR);
    }

    public List<StatisticResp> selectStatistic(ExamineReq req, LocalDate startTime, LocalDate endTime, PeriodEnum per) {
        List<Station> stations = stationMapper.selectList(new LambdaQueryWrapper<>(Station.class)
                .eq(StringUtils.hasText(req.getStation()), Station::getStation, req.getStation())
                .eq(StringUtils.hasText(req.getArea()), Station::getArea, req.getArea()));
        Map<Integer, Station> idMap = stations.stream().collect(Collectors.toMap(Station::getId, Function.identity()));
        List<ExamineInfo> examineInfos = examineInfoMapper.selectList(new LambdaQueryWrapper<>(ExamineInfo.class)
                .between(ExamineInfo::getAssStart, startTime, endTime)
                .eq(ExamineInfo::getAssPer, per)
                .in(ExamineInfo::getStationId, idMap.keySet()));
        if (CollectionUtils.isEmpty(examineInfos)) {
            return List.of();
        } else {
            return examineInfos.stream().map(it -> {
                StatisticResp statisticResp = new StatisticResp();
                statisticResp.setStationId(it.getStationId());
                Station station = idMap.get(it.getStationId());
                statisticResp.setStation(station.getStation());
                statisticResp.setArea(station.getArea());
                statisticResp.setFlow(station.getFlow());
                statisticResp.setFlowTargetRate(it.getFlowTargetRate());
                statisticResp.setOnlineTargetRate(it.getOnlineTargetRate());
                statisticResp.setTime(it.getAssStart());
                return statisticResp;
            }).collect(Collectors.toList());
        }
    }

    /**
     * 查询小时趋势
     */
    public List<StatisticResp> selectTrendByHour(TrendReq req) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        List<TargetRate> targetRates = targetRateService.selectByRange(req.getStartTime(), req.getEndTime(), req.getStationId());
        // 不同小时的在线率
        Map<String, Float> online = targetRates.stream().collect(Collectors.toMap(it -> fmt.format(it.getDatatime()), TargetRate::getTargetRate));
        List<Device10minuteHistory> device10minuteHistories = deviceHistoryData.selectByRange(req.getStartTime(), req.getEndTime(), req.getStationId());
        Map<String, List<Device10minuteHistory>> flow = device10minuteHistories.stream().collect(Collectors.groupingBy(it -> fmt.format(it.getTime())));
        List<StatisticResp> resp = new LinkedList<>();
        LocalDateTime time = req.getStartTime().withMinute(0).withSecond(0).withNano(0);
        while (!req.getEndTime().isBefore(time)) {
            String formatTime = fmt.format(time);
            StatisticResp statisticResp = new StatisticResp();
            statisticResp.setTime(time);
            statisticResp.setStationId(req.getStationId());
            Float onlineRate = online.get(formatTime);
            statisticResp.setOnlineTargetRate(BigDecimal.valueOf(onlineRate));
            List<Device10minuteHistory> historyList = flow.get(formatTime);
            BigDecimal flowRate = historyList.stream().map(Device10minuteHistory::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
            statisticResp.setFlowTargetRate(flowRate.divide(new BigDecimal(historyList.size()), 4, RoundingMode.HALF_UP));
            resp.add(statisticResp);
            time = time.plusHours(1);
        }
        return resp;
    }

    public List<StatisticResp> selectTrendByDay(TrendReq req) {
        return selectTrend(req, PeriodEnum.DAY);
    }

    public List<StatisticResp> selectTrendByMonth(TrendReq req) {
        return selectTrend(req, PeriodEnum.MONTH);
    }

    public List<StatisticResp> selectTrendByYear(TrendReq req) {
        return selectTrend(req, PeriodEnum.YEAR);
    }


    public List<StatisticResp> selectTrend(TrendReq req, PeriodEnum per) {
        List<ExamineInfo> examineInfos = examineInfoMapper.selectList(new LambdaQueryWrapper<>(ExamineInfo.class)
                .between(ExamineInfo::getAssStart, req.getStartTime(), req.getEndTime())
                .eq(ExamineInfo::getAssPer, per)
                .in(ExamineInfo::getStationId, req.getStationId()));
        if (CollectionUtils.isEmpty(examineInfos)) {
            return List.of();
        } else {
            return examineInfos.stream().map(it -> {
                StatisticResp statisticResp = new StatisticResp();
                statisticResp.setStationId(it.getStationId());
                statisticResp.setFlowTargetRate(it.getFlowTargetRate());
                statisticResp.setOnlineTargetRate(it.getOnlineTargetRate());
                statisticResp.setTime(it.getAssStart());
                return statisticResp;
            }).collect(Collectors.toList());
        }
    }

    /**
     * 查询天的考核
     */
    public List<ExamineResp> selectExamineByDay(ExamineReq req) {
        List<StatisticResp> statisticResps = selectStatisticByDay(req);
        return statisticResps.parallelStream().map(it -> {
            ExamineResp examineResp = new ExamineResp();
            BeanUtils.copyProperties(it, examineResp);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            var startTime = req.getTime().toLocalDate().atStartOfDay();
            var endTime = startTime.plusDays(1).minusNanos(1);
            List<TargetRate> targetRates = targetRateService.selectByRange(startTime, endTime, it.getStationId());
            // 不同小时的在线率
            Map<String, Float> online = targetRates.stream().collect(Collectors.toMap(targetRate -> fmt.format(targetRate.getDatatime()), TargetRate::getTargetRate));
            List<Device10minuteHistory> device10minuteHistories = deviceHistoryData.selectByRange(startTime, endTime, it.getStationId());
            Map<String, List<Device10minuteHistory>> flow = device10minuteHistories.stream().collect(Collectors.groupingBy(history -> fmt.format(history.getTime())));
            List<ExamineResp.Target> resp = new LinkedList<>();
            LocalDateTime time = startTime;
            while (!endTime.isBefore(time)) {
                String formatTime = fmt.format(time);
                ExamineResp.Target target = new ExamineResp.Target();
                target.setTime(time);
                Float onlineRate = online.get(formatTime);
                target.setEcoFlow(onlineRate.compareTo(100F) >= 0);
                List<Device10minuteHistory> historyList = flow.get(formatTime);
                BigDecimal flowRate = historyList.stream().map(Device10minuteHistory::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
                target.setEcoFlow(flowRate.divide(new BigDecimal(historyList.size()), 4, RoundingMode.HALF_UP).compareTo(it.getFlow()) <= 0);
                resp.add(target);
                time = time.plusHours(1);
            }
            examineResp.setTargets(resp);
            return examineResp;
        }).collect(Collectors.toList());
    }

    public List<ExamineResp> selectExamineByMonth(ExamineReq req) {
        List<StatisticResp> statisticResps = selectStatisticByMonth(req);
        return statisticResps.parallelStream().map(it -> {
            ExamineResp examineResp = new ExamineResp();
            BeanUtils.copyProperties(it, examineResp);
            var startTime = req.getTime().toLocalDate().withDayOfMonth(1);
            var endTime = startTime.plusMonths(1).minusDays(1);
            List<ExamineInfo> examineInfos = examineInfoMapper.selectList(new LambdaQueryWrapper<>(ExamineInfo.class)
                    .between(ExamineInfo::getAssStart, startTime,endTime)
                    .eq(ExamineInfo::getAssPer, PeriodEnum.DAY)
                    .in(ExamineInfo::getStationId, it.getStationId()));
            List<ExamineResp.Target> collect = examineInfos.stream().map(examineInfo -> {
                ExamineResp.Target target = new ExamineResp.Target();
                target.setTime(examineInfo.getAssStart());
                target.setEcoOnline(examineInfo.getEcoOnline());
                target.setEcoFlow(examineInfo.getEcoFlow());
                return target;
            }).collect(Collectors.toList());
            examineResp.setTargets(collect);
            return examineResp;
        }).collect(Collectors.toList());
    }

    public List<ExamineResp> selectExamineByYear(ExamineReq req) {
        List<StatisticResp> statisticResps = selectStatisticByYear(req);
        return statisticResps.parallelStream().map(it -> {
            ExamineResp examineResp = new ExamineResp();
            BeanUtils.copyProperties(it, examineResp);
            var startTime = req.getTime().toLocalDate().withDayOfYear(1);
            var endTime = startTime.plusYears(1).minusDays(1);
            List<ExamineInfo> examineInfos = examineInfoMapper.selectList(new LambdaQueryWrapper<>(ExamineInfo.class)
                    .between(ExamineInfo::getAssStart, startTime,endTime)
                    .eq(ExamineInfo::getAssPer, PeriodEnum.MONTH)
                    .in(ExamineInfo::getStationId, it.getStationId()));
            List<ExamineResp.Target> collect = examineInfos.stream().map(examineInfo -> {
                ExamineResp.Target target = new ExamineResp.Target();
                target.setTime(examineInfo.getAssStart());
                target.setEcoOnline(examineInfo.getEcoOnline());
                target.setEcoFlow(examineInfo.getEcoFlow());
                return target;
            }).collect(Collectors.toList());
            examineResp.setTargets(collect);
            return examineResp;
        }).collect(Collectors.toList());
    }
}
