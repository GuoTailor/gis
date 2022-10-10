package com.gyh.gis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gyh.gis.domain.Station;

import java.util.List;

/**
 * create by GYH on 2022/9/23
 */
public interface StationMapper extends BaseMapper<Station> {

    List<String> getAllArea();
}