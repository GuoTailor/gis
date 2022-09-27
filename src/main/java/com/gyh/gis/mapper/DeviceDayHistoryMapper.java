package com.gyh.gis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gyh.gis.domain.DeviceDayHistory;

/**
 * create by GYH on 2022/9/27
 */
public interface DeviceDayHistoryMapper extends BaseMapper<DeviceDayHistory> {
    int deleteByPrimaryKey(Long id);

    int insert(DeviceDayHistory record);

    int insertSelective(DeviceDayHistory record);

    DeviceDayHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DeviceDayHistory record);

    int updateByPrimaryKey(DeviceDayHistory record);

    long nextId();
}