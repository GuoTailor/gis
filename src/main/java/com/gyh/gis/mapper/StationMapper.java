package com.gyh.gis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gyh.gis.domain.Station;

import java.util.List;

/**
 * create by GYH on 2024/4/25
 */
public interface StationMapper extends BaseMapper<Station> {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Station record);

    Station selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Station record);

    int updateByPrimaryKey(Station record);

    List<String> getAllArea();

    Station getBySysCode(String sysCode);
}
