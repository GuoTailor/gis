package com.gyh.gis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gyh.gis.domain.DeviceStatus;
import com.gyh.gis.domain.Station;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.dto.resp.DeviceStatusResp;
import com.gyh.gis.enums.StateEnum;
import com.gyh.gis.mapper.DeviceStatusMapper;
import com.gyh.gis.mapper.StationMapper;
import com.gyh.gis.util.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * create by GYH on 2022/9/26
 */
@Slf4j
@Service
public class DeviceStatusService {
    @Resource
    private DeviceStatusMapper deviceStatusMapper;
    @Resource
    private StationMapper stationMapper;

    /**
     * 有就更新，没有就新增
     */
    public int insertOrUpdate(DeviceStatusInsertReq req) {
        QueryWrapper<DeviceStatus> wrapper = Wrappers.<DeviceStatus>query().eq("station_id", req.getStationId());
        Station station = stationMapper.selectById(req.getStationId());
        AssertUtils.notNull(station, "站点信息不存在", req.getStationId());
        // TODO 保存历史数据
        // 保存最新数据
        synchronized (DeviceStatusService.class) {
            DeviceStatus oldDevice = deviceStatusMapper.selectOne(wrapper);
            if (oldDevice != null) {
                oldDevice.setAlarmValue(req.getAlarmValue());
                if (station.getFlow().compareTo(req.getAlarmValue()) <= 0) {
                    oldDevice.setAlarmState(StateEnum.ALARM);
                    oldDevice.setAlarmTime(LocalDateTime.now());
                    oldDevice.setCancelAlarm(false);
                    oldDevice.setCancelTime(null);
                    oldDevice.setScreenshotUrl(req.getScreenshotUrl());
                } else {
                    oldDevice.setAlarmState(StateEnum.NORMAL);
                    oldDevice.setCancelAlarm(true);
                    oldDevice.setScreenshotUrl(null);
                }
                return deviceStatusMapper.updateById(oldDevice);
            } else {
                DeviceStatus deviceStatus = new DeviceStatus();
                BeanUtils.copyProperties(req, deviceStatus);
                if (station.getFlow().compareTo(req.getAlarmValue()) <= 0) {
                    deviceStatus.setAlarmState(StateEnum.ALARM);
                    deviceStatus.setAlarmTime(LocalDateTime.now());
                    deviceStatus.setCancelAlarm(false);
                } else {
                    deviceStatus.setAlarmState(StateEnum.NORMAL);
                    deviceStatus.setCancelAlarm(true);
                }
                return deviceStatusMapper.insert(deviceStatus);
            }
        }
    }

    public Integer insert(DeviceStatusInsertReq req) {
        DeviceStatus deviceStatus = new DeviceStatus();
        BeanUtils.copyProperties(req, deviceStatus);
        int insert = deviceStatusMapper.insert(deviceStatus);
        log.info("插入数据影响行数 {}", insert);
        return deviceStatus.getId();
    }

    /**
     * 获取设备的最新数据
     *
     * @param id 设备id
     * @return {@link DeviceStatusResp}
     */
    public DeviceStatusResp getById(Integer id) {
        DeviceStatus deviceStatus = deviceStatusMapper.selectOne(Wrappers.<DeviceStatus>query().eq("station_id", id));
        AssertUtils.notNull(deviceStatus, "查询不到id为" + id + "的数据");
        DeviceStatusResp resp = new DeviceStatusResp();
        BeanUtils.copyProperties(deviceStatus, resp);
        Station station = stationMapper.selectById(deviceStatus.getStationId());
        resp.setId(station.getId());
        resp.setStationName(station.getStation());
        resp.setArea(station.getArea());
        resp.setEvaluate(station.getFlow());
        resp.setLongitude(station.getLongitude());
        resp.setLatitude(station.getLatitude());
        return resp;
    }

    public List<DeviceStatusResp> getAllState() {
        List<Station> stations = stationMapper.selectList(Wrappers.query());
        List<Integer> ids = stations.stream().map(Station::getId).collect(Collectors.toList());
        List<DeviceStatus> deviceStatusList = deviceStatusMapper.selectList(Wrappers.<DeviceStatus>query().in("station_id", ids));
        Map<Integer, DeviceStatus> cache = deviceStatusList.stream().collect(Collectors.toMap(DeviceStatus::getStationId, it -> it));
        return stations.stream().map(station -> {
            DeviceStatus deviceStatus = cache.get(station.getId());
            DeviceStatusResp resp = new DeviceStatusResp();
            BeanUtils.copyProperties(deviceStatus, resp);
            resp.setId(deviceStatus.getStationId());
            resp.setStationName(station.getStation());
            resp.setArea(station.getArea());
            resp.setEvaluate(station.getFlow());
            resp.setLongitude(station.getLongitude());
            resp.setLatitude(station.getLatitude());
            return resp;
        }).collect(Collectors.toList());
    }

    /**
     * 取消报警
     *
     * @param id 设备id
     */
    public boolean cancelAlarm(Integer id) {
        DeviceStatus deviceStatus = deviceStatusMapper.selectOne(Wrappers.<DeviceStatus>query().eq("station_id", id));
        AssertUtils.notNull(deviceStatus, "查询不到id为" + id + "的数据");
        var set = Wrappers.<DeviceStatus>update().eq("station_id", id).set("cancel_alarm", true).set("cancel_time", new Date());
        int update = deviceStatusMapper.update(null, set);
        return update == 1;
    }

    public void update(DeviceStatus status) {
        deviceStatusMapper.updateById(status);
    }
}
