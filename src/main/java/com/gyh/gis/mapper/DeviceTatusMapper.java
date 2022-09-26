package com.gyh.gis.mapper;

import com.gyh.gis.domain.DeviceTatus;

/**
 * create by GYH on 2022/9/26
 */
public interface DeviceTatusMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DeviceTatus record);

    int insertSelective(DeviceTatus record);

    DeviceTatus selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DeviceTatus record);

    int updateByPrimaryKey(DeviceTatus record);
}