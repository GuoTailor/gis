package com.gyh.gis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gyh.gis.domain.ExamineInfo;
import com.gyh.gis.enums.PeriodEnum;
import com.gyh.gis.mapper.ExamineInfoMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * create by GYH on 2023/9/7
 */
@Service
public class ExamineInfoService {
    @Resource
    private ExamineInfoMapper examineInfoMapper;

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

}
