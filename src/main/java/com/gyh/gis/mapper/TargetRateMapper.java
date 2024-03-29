package com.gyh.gis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gyh.gis.domain.TargetRate;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * create by GYH on 2023/8/31
 */
public interface TargetRateMapper extends BaseMapper<TargetRate> {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(TargetRate record);

    TargetRate selectByPrimaryKey(Integer id);

    TargetRate selectByStationIdAndTime(@Param("stationId") Integer stationId, @Param("time") LocalDateTime time);

    TargetRate selectByLastStationId(@Param("stationId") Integer stationId);

    int updateByPrimaryKeySelective(TargetRate record);

    int updateByPrimaryKey(TargetRate record);
}