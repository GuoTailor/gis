package com.gyh.gis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gyh.gis.domain.Device10minuteHistory;
import com.gyh.gis.domain.DeviceDayHistory;
import com.gyh.gis.domain.Station;
import com.gyh.gis.dto.DeviceData;
import com.gyh.gis.dto.req.AlarmImgReq;
import com.gyh.gis.dto.resp.AlarmImgResp;
import com.gyh.gis.dto.resp.DeviceAlarmInfoResp;
import com.gyh.gis.dto.resp.DeviceAlarmListResp;
import com.gyh.gis.enums.AlarmStationEnum;
import com.gyh.gis.enums.StateEnum;
import com.gyh.gis.mapper.Device10minuteHistoryMapper;
import com.gyh.gis.mapper.DeviceDayHistoryMapper;
import com.gyh.gis.mapper.StationMapper;
import com.gyh.gis.support.shardingtable.executor.DetermineTableNameForNewExe;
import com.gyh.gis.support.shardingtable.executor.input.DetermineTableNameForNewInput;
import com.gyh.gis.support.shardingtable.executor.output.DetermineTableNameForNewOutput;
import com.gyh.gis.support.shardingtable.metadata.ShardingTable;
import com.gyh.gis.util.AssertUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * create by GYH on 2022/9/27
 */
@Service
public class DeviceHistoryData {
    @Resource
    private Device10minuteHistoryMapper minuteHistoryMapper;
    @Value("${gis.pic-path}")
    private String picPath;
    @Resource
    private DeviceDayHistoryMapper dayHistoryMapper;

    @Autowired
    private DetermineTableNameForNewExe determineTableNameForNewExe;

    @Resource
    private StationMapper stationMapper;

    /**
     * 添加一个10分钟节点的数据
     *
     * @param deviceData 节点数据
     */
    public int addDeviceData(DeviceData deviceData) {
        Device10minuteHistory device10minuteHistory = new Device10minuteHistory();
        BeanUtils.copyProperties(deviceData, device10minuteHistory);
        // 获取全局唯一自增id
        var id = minuteHistoryMapper.nextId();
        device10minuteHistory.setId(id);
        // 获取分表表名
        DetermineTableNameForNewOutput execute = determineTableNameForNewExe.execute(new DetermineTableNameForNewInput()
                .setOriginTableName(Device10minuteHistory.class)
                .setCreateTime(LocalDateTime.now()));
        String tableName = execute.getTableName();
        return minuteHistoryMapper.insertSelective(device10minuteHistory, tableName);
    }

    /**
     * 查询某个站点某天的全部数据
     *
     * @param time 某一天
     * @return 列表
     */
    public List<Device10minuteHistory> selectByOneDay(LocalDate time, Integer stationId) {
        var startTime = time.atStartOfDay();
        var endTime = time.atTime(LocalTime.MAX);
        return selectByRange(startTime, endTime, stationId);
    }

    public List<Device10minuteHistory> selectByRange(LocalDateTime startTime, LocalDateTime endTime, Integer stationId) {
        var tableSharding = determineTableNameForNewExe.getAllSharding(Device10minuteHistory.class);
        if (CollectionUtils.isEmpty(tableSharding)) return List.of();
        ArrayList<Device10minuteHistory> result = new ArrayList<>();
        for (ShardingTable shardingTable : tableSharding) {
            List<Device10minuteHistory> deviceData = minuteHistoryMapper.selectByTime(startTime, endTime, stationId, shardingTable.getTableName());
            if (CollectionUtils.isEmpty(deviceData)) continue;
            result.addAll(deviceData);
            Device10minuteHistory first = minuteHistoryMapper.selectFirst(stationId, shardingTable.getTableName());
            if (first == null) continue;
            // 如果开始时间在表的第一条时间之后就认为数据查找完毕，没有必要查询下一张表
            if (startTime.isAfter(first.getTime())) break;
        }
        return result;
    }

    public List<DeviceData> selectByTime(Integer id, LocalDateTime startTime, LocalDateTime endTime) {
        AssertUtils.isTrue(startTime.isBefore(endTime), "结束时间不能早于开始时间");
        ArrayList<DeviceData> result = new ArrayList<>();
        Station station = stationMapper.selectById(id);
        // 如果他们相差小于一天
        if (startTime.plusDays(1).isAfter(endTime)) {
            var tableSharding = determineTableNameForNewExe.getAllSharding(Device10minuteHistory.class);
            if (CollectionUtils.isEmpty(tableSharding)) return List.of();
            for (ShardingTable shardingTable : tableSharding) {
                List<Device10minuteHistory> deviceData = minuteHistoryMapper.selectByTime(startTime, endTime, id, shardingTable.getTableName());
                if (CollectionUtils.isEmpty(deviceData)) continue;
                deviceData.forEach(it -> {
                    DeviceData data = new DeviceData();
                    BeanUtils.copyProperties(it, data);
                    data.setFlow(station.getFlow());
                    result.add(data);
                });
                Device10minuteHistory first = minuteHistoryMapper.selectFirst(id, shardingTable.getTableName());
                if (first == null) continue;
                // 如果开始时间在表的第一条时间之后就认为数据查找完毕，没有必要查询下一张表
                if (startTime.isAfter(first.getTime())) break;
            }
        } else {
            var tableSharding = determineTableNameForNewExe.getAllSharding(DeviceDayHistory.class);
            if (CollectionUtils.isEmpty(tableSharding)) return List.of();
            for (ShardingTable shardingTable : tableSharding) {
                List<DeviceDayHistory> deviceData = dayHistoryMapper.selectByTime(startTime, endTime, id, shardingTable.getTableName());
                if (CollectionUtils.isEmpty(deviceData)) continue;
                deviceData.forEach(it -> {
                    DeviceData data = new DeviceData();
                    BeanUtils.copyProperties(it, data);
                    data.setFlow(station.getFlow());
                    data.setTime(it.getTime().atTime(LocalTime.MIN));
                    result.add(data);
                });
                DeviceDayHistory first = dayHistoryMapper.selectFirst(id, shardingTable.getTableName());
                if (first == null) continue;
                // 如果开始时间在表的第一条时间之后就认为数据查找完毕，没有必要查询下一张表
                if (startTime.toLocalDate().isAfter(first.getTime())) break;
            }
        }
        return result;
    }

    /**
     * 获取历史报警数据
     *
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param state       站点
     * @param stationName 站名
     */
    public DeviceAlarmListResp selectAllByError(LocalDateTime startTime, LocalDateTime endTime, AlarmStationEnum state, String stationName) {
        DeviceAlarmListResp alarmList = new DeviceAlarmListResp();
        ArrayList<DeviceAlarmInfoResp> result = new ArrayList<>();
        alarmList.setAlarmInfoResps(result);
        var tableSharding = determineTableNameForNewExe.getAllSharding(Device10minuteHistory.class);
        if (CollectionUtils.isEmpty(tableSharding)) return alarmList;
        QueryWrapper<Station> wrapper = Wrappers.query();
        if (StringUtils.hasLength(stationName)) {
            wrapper = wrapper.eq("station", stationName);
        }
        List<Station> stations = stationMapper.selectList(wrapper);
        Map<Integer, Station> collect = stations.stream().collect(Collectors.toMap(Station::getId, it -> it));
        for (ShardingTable shardingTable : tableSharding) {
            Boolean cancelAlarm = null;
            if (state != null) {
                cancelAlarm = state == AlarmStationEnum.CANCELED;
            }
            List<Device10minuteHistory> deviceData = minuteHistoryMapper.selectAllByTime(startTime, endTime, StateEnum.ALARM, cancelAlarm, collect.keySet(), shardingTable.getTableName());
            if (CollectionUtils.isEmpty(deviceData)) continue;
            deviceData.forEach(it -> {
                DeviceAlarmInfoResp data = new DeviceAlarmInfoResp();
                BeanUtils.copyProperties(it, data);
                data.setAlarmValue(it.getValue());
                data.setAlarmTime(it.getTime());
                Station station = collect.get(it.getStationId());
                data.setArea(station.getArea());
                data.setEvaluate(station.getFlow());
                data.setStationName(station.getStation());
                data.setLatitude(station.getLatitude());
                data.setLongitude(station.getLongitude());
                result.add(data);
            });
            Device10minuteHistory first = minuteHistoryMapper.selectFirst(null, shardingTable.getTableName());
            if (first == null) continue;
            // 如果开始时间在表的第一条时间之后就认为数据查找完毕，没有必要查询下一张表
            if (startTime.isAfter(first.getTime())) break;
        }
        int count = (int) result.stream().filter(DeviceAlarmInfoResp::getCancelAlarm).count();
        result.sort((o1, o2) -> o2.getAlarmTime().compareTo(o1.getAlarmTime()));
        alarmList.setCanceled(count);
        alarmList.setAlarm(result.size() - count);
        return alarmList;
    }

    /**
     * 添加一个站点的一天的平均流量
     *
     * @param dayHistory 节点数据
     * @return 影响行数
     */
    public int addDeviceHistoryData(DeviceDayHistory dayHistory) {
        // 获取全局唯一自增id
        long id = dayHistoryMapper.nextId();
        dayHistory.setId(id);
        // 获取分表表名
        DetermineTableNameForNewOutput execute = determineTableNameForNewExe.execute(new DetermineTableNameForNewInput()
                .setOriginTableName(DeviceDayHistory.class)
                .setCreateTime(LocalDateTime.now()));
        String tableName = execute.getTableName();
        return dayHistoryMapper.insertSelective(dayHistory, tableName);
    }

    /**
     * 查询报警截图
     */
    public List<AlarmImgResp> selectAlarmImg(AlarmImgReq req) {
        var tableSharding = determineTableNameForNewExe.getAllSharding(Device10minuteHistory.class);
        if (CollectionUtils.isEmpty(tableSharding)) return List.of();
        LambdaQueryWrapper<Station> wrapper = Wrappers.lambdaQuery(Station.class)
                .eq(StringUtils.hasText(req.getArea()), Station::getArea, req.getArea())
                .like(StringUtils.hasText(req.getStation()), Station::getStation, req.getStation());

        List<Station> stations = stationMapper.selectList(wrapper);
        Map<Integer, Station> collect = stations.stream().collect(Collectors.toMap(Station::getId, Function.identity()));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH-mm-ss-");
        List<AlarmImgResp> result = new LinkedList<>();
        for (ShardingTable shardingTable : tableSharding) {
            List<Device10minuteHistory> deviceData = minuteHistoryMapper.selectAllByTime(req.getStartTime(), req.getEndTime(), StateEnum.ALARM, null, collect.keySet(), shardingTable.getTableName());
            if (CollectionUtils.isEmpty(deviceData)) continue;
            deviceData.forEach(it -> {
                AlarmImgResp data = new AlarmImgResp();
                data.setId(it.getId());
                data.setAlarmValue(it.getValue());
                data.setAlarmTime(it.getTime());
                Station station = collect.get(it.getStationId());
                data.setArea(station.getArea());
                data.setEvaluate(station.getFlow());
                data.setStation(station.getStation());
                String format = fmt.format(it.getTime());
                data.setLocalFilePath(picPath + format + station.getId() + ".jpg");
                data.setImgUrl(it.getScreenshotUrl());
                data.setImgDescribe("超标报警");
                result.add(data);
            });
            Device10minuteHistory first = minuteHistoryMapper.selectFirst(null, shardingTable.getTableName());
            if (first == null) continue;
            // 如果开始时间在表的第一条时间之后就认为数据查找完毕，没有必要查询下一张表
            if (req.getStartTime().isAfter(first.getTime())) break;
        }
        result.sort((o1, o2) -> o2.getAlarmTime().compareTo(o1.getAlarmTime()));
        return result;
    }

}
