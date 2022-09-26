package com.gyh.gis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gyh.gis.domain.DeviceStatus;
import com.gyh.gis.domain.Station;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.dto.resp.DeviceStatusResp;
import com.gyh.gis.mapper.DeviceStatusMapper;
import com.gyh.gis.mapper.StationMapper;
import com.gyh.gis.util.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    public void insertOrUpdate(DeviceStatusInsertReq req) {
        DeviceStatus deviceStatus = new DeviceStatus();
        BeanUtils.copyProperties(req, deviceStatus);
        QueryWrapper<DeviceStatus> wrapper = Wrappers.<DeviceStatus>query().eq("station_id", req.getStationId());
        Station station = stationMapper.selectById(req.getStationId());
        AssertUtils.notNull(station, "站点信息不存在", req.getStationId());
        // TODO 保存历史数据
        synchronized (DeviceStatusService.class) {
            DeviceStatus oldDevice = deviceStatusMapper.selectOne(wrapper);
            if (oldDevice != null) {
                if (station.getFlow().compareTo(req.getAlarmValue()) < 0) {

                }
                deviceStatus.setId(oldDevice.getId());
                deviceStatusMapper.updateById(oldDevice);
            } else {
                deviceStatusMapper.insert(deviceStatus);
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

    public DeviceStatusResp getById(Integer id) {
        DeviceStatus deviceStatus = deviceStatusMapper.selectById(id);
        DeviceStatusResp resp = new DeviceStatusResp();
        BeanUtils.copyProperties(deviceStatus, resp);
        return resp;
    }
}
