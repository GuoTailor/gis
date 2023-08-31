package com.gyh.gis.mapper;

import com.gyh.gis.domain.TargetRate;

/**
 * create by GYH on 2023/8/31
 */
public interface TargetRateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TargetRate record);

    int insertSelective(TargetRate record);

    TargetRate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TargetRate record);

    int updateByPrimaryKey(TargetRate record);
}