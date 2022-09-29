package com.gyh.gis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gyh.gis.domain.Device10minuteHistory;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * create by GYH on 2022/9/27
 */
public interface Device10minuteHistoryMapper extends BaseMapper<Device10minuteHistory> {
    int deleteByPrimaryKey(Long id);

    int insert(Device10minuteHistory record);

    int insertSelective(@Param("record") Device10minuteHistory record, @Param("tableName") String tableName);

    List<Device10minuteHistory> selectByTime(@Param("startTime")LocalDateTime startTime, @Param("endTime")LocalDateTime endTime, @Param("id") Integer id, @Param("tableName") String tableName);

    Device10minuteHistory selectFirst(@Param("id") Integer id, @Param("tableName") String tableName);

    Device10minuteHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Device10minuteHistory record);

    int updateByPrimaryKey(Device10minuteHistory record);

    long nextId();
}