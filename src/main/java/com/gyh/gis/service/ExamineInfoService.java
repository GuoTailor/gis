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
import com.gyh.gis.enums.PeriodEnum;
import com.gyh.gis.mapper.ExamineInfoMapper;
import com.gyh.gis.mapper.StationMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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
    public List<ExamineResp> selectStatisticByHour(ExamineReq req) {
        List<Station> stations = stationMapper.selectList(new LambdaQueryWrapper<>(Station.class)
                .eq(StringUtils.hasText(req.getStation()), Station::getStation, req.getStation())
                .eq(StringUtils.hasText(req.getArea()), Station::getArea, req.getArea()));
        var startTime = req.getTime().withMinute(0).withSecond(0).withNano(0);
        var enTime = startTime.plusHours(1).minusNanos(1);
        return stations.parallelStream().map(station -> {
            TargetRate targetRates = targetRateService.selectByStationIdAndTime(startTime, station.getId());
            List<Device10minuteHistory> device10minuteHistories = deviceHistoryData.selectByRange(startTime, enTime, station.getId());
            BigDecimal flow = device10minuteHistories.stream().map(Device10minuteHistory::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
            ExamineResp examineResp = new ExamineResp();
            examineResp.setArea(station.getArea());
            examineResp.setStation(station.getStation());
            examineResp.setStationId(station.getId());
            examineResp.setTime(startTime);
            examineResp.setOnlineTargetRate(BigDecimal.valueOf(targetRates.getTargetRate()));
            examineResp.setFlowTargetRate(flow.divide(new BigDecimal(device10minuteHistories.size()), 3, RoundingMode.HALF_UP));
            return examineResp;
        }).collect(Collectors.toList());
    }

    public List<ExamineResp> selectTrendByHour(TrendReq resp) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        List<TargetRate> targetRates = targetRateService.selectByRange(resp.getStartTime(), resp.getEndTime(), resp.getStationId());
        Map<String, Float> online = targetRates.stream().collect(Collectors.toMap(it -> fmt.format(it.getDatatime()), TargetRate::getTargetRate));
        List<Device10minuteHistory> device10minuteHistories = deviceHistoryData.selectByRange(resp.getStartTime(), resp.getEndTime(), resp.getStationId());
        Map<String, List<Device10minuteHistory>> flow = device10minuteHistories.stream().collect(Collectors.groupingBy(it -> fmt.format(it.getTime())));

    }

}
