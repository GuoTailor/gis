package com.gyh.gis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyh.gis.domain.DeviceStatus;
import com.gyh.gis.domain.Station;
import com.gyh.gis.dto.resp.StationVideo;
import com.gyh.gis.enums.StateEnum;
import com.gyh.gis.mapper.DeviceStatusMapper;
import com.gyh.gis.mapper.StationMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * create by GYH on 2022/9/23
 */
@Service
@Slf4j
public class StationService {
    @Resource
    private StationMapper stationMapper;
    @Resource
    private DeviceStatusMapper deviceStatusMapper;

    public Page<Station> page() {
        Page<Station> page = new Page<Station>().setCurrent(1).setSize(10);
        QueryWrapper<Station> query = Wrappers.query();
        return stationMapper.selectPage(page, query);
    }

    public List<Integer> selectAllId() {
        var wrapper = Wrappers.<Station>query().select("id");
        return stationMapper.selectObjs(wrapper).stream().map(it -> (Integer) it).collect(Collectors.toList());
    }

    public Station selectById(Integer id) {
        return stationMapper.selectById(id);
    }

    public List<Station> getAll() {
        var wrapper = Wrappers.<Station>query();
        return stationMapper.selectList(wrapper);
    }

    public Map<String, List<StationVideo>> selectAllByGroup() {
        List<Station> stations = stationMapper.selectList(Wrappers.query());

        return stations.parallelStream()
                .map(it -> {
                    StationVideo video = new StationVideo();
                    BeanUtils.copyProperties(it, video);
                    DeviceStatus deviceStatus = deviceStatusMapper.selectOne(Wrappers.lambdaQuery(DeviceStatus.class).eq(DeviceStatus::getStationId, it.getId()).last("limit 1"));
                    if (deviceStatus != null) {
                        video.setValue(deviceStatus.getValue());
//                        video.setOnLine(deviceStatus.getValue() != null && deviceStatus.getValue().compareTo(BigDecimal.ZERO) > 0 && deviceStatus.getTime().plusMinutes(10).isAfter(LocalDateTime.now()));
                        video.setOnLine(true);
                        if (deviceStatus.getErrorState() == StateEnum.ERROR) {
                            video.setAlarmState(StateEnum.NORMAL);
                            video.setOnLine(false);
                        } else if (deviceStatus.getValue().compareTo(it.getFlow()) < 0) {
                            video.setAlarmState(StateEnum.ALARM);
                        } else {
                            video.setAlarmState(StateEnum.NORMAL);
                        }
                    } else {
                        video.setOnLine(false);
                        video.setAlarmState(StateEnum.ERROR);
                    }
                    return video;
                })
                .collect(Collectors.groupingBy(StationVideo::getArea));
    }

    /**
     * 获取所有河流
     *
     * @return 所有河流
     */
    public List<String> getAllArea() {
        return stationMapper.getAllArea();
    }

}
