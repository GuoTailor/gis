package com.gyh.gis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gyh.gis.domain.Device10minuteHistory;
import com.gyh.gis.domain.DeviceStatus;
import com.gyh.gis.domain.Station;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.dto.resp.DeviceStatusResp;
import com.gyh.gis.enums.StateEnum;
import com.gyh.gis.mapper.Device10minuteHistoryMapper;
import com.gyh.gis.mapper.DeviceStatusMapper;
import com.gyh.gis.mapper.StationMapper;
import com.gyh.gis.support.shardingtable.executor.DetermineTableNameForNewExe;
import com.gyh.gis.support.shardingtable.executor.input.DetermineTableNameForNewInput;
import com.gyh.gis.support.shardingtable.executor.output.DetermineTableNameForNewOutput;
import com.gyh.gis.support.shardingtable.metadata.ShardingTable;
import com.gyh.gis.util.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    @Autowired
    private DetermineTableNameForNewExe determineTableNameForNewExe;
    @Resource
    private Device10minuteHistoryMapper minuteHistoryMapper;

    /**
     * 有就更新，没有就新增
     */
    public int insertOrUpdate(DeviceStatusInsertReq req) {
        QueryWrapper<DeviceStatus> wrapper = Wrappers.<DeviceStatus>query().eq("station_id", req.getStationId());
        Station station = stationMapper.selectById(req.getStationId());
        AssertUtils.notNull(station, "站点信息不存在", req.getStationId());
        // 保存最新数据
        synchronized (DeviceStatusService.class) {
            DeviceStatus oldDevice = deviceStatusMapper.selectOne(wrapper);
            if (oldDevice != null) {
                oldDevice.setTime(LocalDateTime.now());
                oldDevice.setValue(req.getValue());
                oldDevice.setErrorState(req.getErrorState());
                if (StateEnum.ERROR == req.getErrorState()) {
                    oldDevice.setErrorTime(LocalDateTime.now());
                } else if (StateEnum.NORMAL == req.getErrorState()) {
                    oldDevice.setErrorTime(null);
                }
                deviceStatusMapper.updateById(oldDevice);
            } else {
                DeviceStatus deviceStatus = new DeviceStatus();
                deviceStatus.setStationId(req.getStationId());
                deviceStatus.setErrorState(req.getErrorState());
                deviceStatus.setTime(LocalDateTime.now());
                if (req.getValue() != null) {
                    deviceStatus.setValue(req.getValue());
                } else {
                    deviceStatus.setValue(BigDecimal.ZERO);
                }
                if (StateEnum.ERROR == req.getErrorState()){
                    deviceStatus.setErrorTime(LocalDateTime.now());
                } else if (StateEnum.NORMAL == req.getErrorState()) {
                    deviceStatus.setErrorTime(null);
                }
                deviceStatusMapper.insert(deviceStatus);
            }
        }
        if (req.getValue() != null) {
            DetermineTableNameForNewOutput execute = determineTableNameForNewExe.execute(new DetermineTableNameForNewInput()
                    .setOriginTableName(Device10minuteHistory.class));
            Device10minuteHistory minuteHistory = new Device10minuteHistory();
            // 获取全局唯一自增id
            var id = minuteHistoryMapper.nextId();
            minuteHistory.setId(id);
            minuteHistory.setStationId(req.getStationId());
            minuteHistory.setValue(req.getValue());
            minuteHistory.setTime(LocalDateTime.now());
            if (station.getFlow().compareTo(req.getValue()) <= 0) {
                minuteHistory.setAlarmState(StateEnum.ALARM);
                minuteHistory.setCancelAlarm(false);
                minuteHistory.setCancelTime(null);
                minuteHistory.setScreenshotUrl(req.getScreenshotUrl());
            } else {
                minuteHistory.setAlarmState(StateEnum.NORMAL);
                minuteHistory.setCancelAlarm(true);
                minuteHistory.setScreenshotUrl(null);
            }
            return minuteHistoryMapper.insertSelective(minuteHistory, execute.getTableName());
        }
        return 1;
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
            if (deviceStatus != null) {
                BeanUtils.copyProperties(deviceStatus, resp);
                resp.setId(deviceStatus.getStationId());
                resp.setAlarmValue(deviceStatus.getValue());
                resp.setAlarmTime(deviceStatus.getTime());
                if (deviceStatus.getValue().compareTo(station.getFlow()) >= 0) {
                   resp.setAlarmState(StateEnum.ALARM);
                } else {
                    resp.setAlarmState(StateEnum.NORMAL);
                }
            }
            resp.setId(station.getId());
            resp.setStationId(station.getId());
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
    public boolean cancelAlarm(Long id) {
        var tableSharding = determineTableNameForNewExe.getAllSharding(Device10minuteHistory.class);
        if (CollectionUtils.isEmpty(tableSharding)) return true;
        tableSharding.parallelStream().forEach(shardingTable -> {
            Device10minuteHistory minuteHistory = new Device10minuteHistory();
            minuteHistory.setId(id);
            minuteHistory.setCancelAlarm(true);
            minuteHistory.setCancelTime(LocalDateTime.now());
            minuteHistoryMapper.updateByPrimaryKeySelective(minuteHistory, shardingTable.getTableName());
        });
        return true;
    }

    public void update(DeviceStatus status) {
        deviceStatusMapper.updateById(status);
    }
}
