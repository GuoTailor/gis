package com.gyh.gis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gyh.gis.domain.TargetRate;
import com.gyh.gis.mapper.TargetRateMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 水电站达标率
 * create by GYH on 2023/9/4
 */
@Service
@Slf4j
public class TargetRateService {
    @Resource
    private TargetRateMapper targetRateMapper;

    public TargetRate selectByStationIdAndTime(LocalDateTime time, Integer stationId) {
        return targetRateMapper.selectByStationIdAndTime(stationId, time);
    }

    /**
     * 统计定时任务
     *
     * @param stationId 站点id
     * @param isOnline  是否在线
     */
    public void statistic(Integer stationId, boolean isOnline) {
        LocalDateTime now = LocalDateTime.now();
        // 注意这个时间是按定时任务来划分的
        LocalDateTime formData = LocalDateTime.of(now.toLocalDate(), LocalTime.of(now.getHour(), 0));
        TargetRate targetRate = targetRateMapper.selectByStationIdAndTime(stationId, formData);
        if (targetRate == null) {
            targetRate = new TargetRate();
            targetRate.setStationId(stationId);
            targetRate.setDatatime(formData);
            targetRate.setOnlineCount(0);
        }
        if (isOnline) {
            targetRate.setOnlineCount(targetRate.getOnlineCount() == null ? 1 : targetRate.getOnlineCount() + 1);
        }
        targetRate.setTotalCount(targetRate.getTotalCount() == null ? 1 : targetRate.getTotalCount() + 1);
        targetRate.setTargetRate(targetRate.getOnlineCount() / (float) targetRate.getTotalCount());
        if (targetRate.getId() != null) {
            targetRateMapper.updateByPrimaryKeySelective(targetRate);
        } else {
            targetRateMapper.insertSelective(targetRate);
        }
    }

    public List<TargetRate> selectByRange(LocalDateTime startTime, LocalDateTime endTime, Integer stationId) {
        return targetRateMapper.selectList(new QueryWrapper<TargetRate>().between("datatime", startTime, endTime).eq("station_id", stationId));
    }
}
