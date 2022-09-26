package com.gyh.gis.mapper;

import com.gyh.gis.domain.DeviceStatus;

/**
 * create by GYH on 2022/9/26
 */
public interface DeviceStatusMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DeviceStatus record);

    int insertSelective(DeviceStatus record);

    DeviceStatus selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DeviceStatus record);

    int updateByPrimaryKey(DeviceStatus record);
}