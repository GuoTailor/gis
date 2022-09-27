package com.gyh.gis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyh.gis.domain.Station;
import com.gyh.gis.mapper.StationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * create by GYH on 2022/9/23
 */
@Service
@Slf4j
public class StationService {
    @Resource
    private StationMapper stationMapper;

    public Page<Station> page() {
        Page<Station> page = new Page<Station>().setCurrent(1).setSize(10);
        QueryWrapper<Station> query = Wrappers.query();
        return stationMapper.selectPage(page, query);
    }

    public List<Integer> selectAll() {
        var wrapper = Wrappers.<Station>query().select("id");
        return stationMapper.selectObjs(wrapper).stream().map(it -> (Integer) it).collect(Collectors.toList());
    }

}
