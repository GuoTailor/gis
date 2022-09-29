package com.gyh.gis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gyh.gis.domain.DeviceDayHistory;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * create by GYH on 2022/9/27
 */
public interface DeviceDayHistoryMapper extends BaseMapper<DeviceDayHistory> {
    int deleteByPrimaryKey(Long id);

    int insert(DeviceDayHistory record);

    int insertSelective(@Param("record") DeviceDayHistory record, @Param("tableName") String tableName);

    List<DeviceDayHistory> selectByTime(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("id") Integer id, @Param("tableName") String tableName);

    DeviceDayHistory selectFirst(@Param("id") Integer id, @Param("tableName") String tableName);

    DeviceDayHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DeviceDayHistory record);

    int updateByPrimaryKey(DeviceDayHistory record);

    long nextId();
}