package com.gyh.gis.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gyh.gis.domain.Device10minuteHistory;
import com.gyh.gis.domain.DeviceDayHistory;
import com.gyh.gis.dto.DeviceData;
import com.gyh.gis.mapper.Device10minuteHistoryMapper;
import com.gyh.gis.mapper.DeviceDayHistoryMapper;
import com.gyh.gis.support.shardingtable.executor.DetermineTableNameForNewExe;
import com.gyh.gis.support.shardingtable.executor.input.DetermineTableNameForNewInput;
import com.gyh.gis.support.shardingtable.executor.output.DetermineTableNameForNewOutput;
import com.gyh.gis.support.shardingtable.metadata.ShardingTable;
import com.gyh.gis.util.AssertUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * create by GYH on 2022/9/27
 */
@Service
public class DeviceHistoryData {
    @Resource
    private Device10minuteHistoryMapper minuteHistoryMapper;

    @Resource
    private DeviceDayHistoryMapper dayHistoryMapper;

    @Autowired
    private DetermineTableNameForNewExe determineTableNameForNewExe;

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
        var wrapper = Wrappers.<Device10minuteHistory>query()
                .between("time", startTime, endTime)
                .eq("station_id", stationId);
        return minuteHistoryMapper.selectList(wrapper);
    }

    public List<DeviceData> selectByTime(Integer id, LocalDateTime startTime, LocalDateTime endTime) {
        AssertUtils.isTrue(startTime.isBefore(endTime), "结束时间不能早于开始时间");
        ArrayList<DeviceData> result = new ArrayList<>();
        // 如果他们相差小于一天
        if (startTime.plusDays(1).isAfter(endTime)) {
            var tableSharding = determineTableNameForNewExe.getAllSharding(Device10minuteHistory.class);
            if (CollectionUtils.isEmpty(tableSharding)) return List.of();
            for (ShardingTable shardingTable : tableSharding) {
                List<Device10minuteHistory> deviceData = minuteHistoryMapper.selectByTime(startTime, endTime, id, shardingTable.getTableName());
                if (CollectionUtils.isEmpty(deviceData)) continue;
                deviceData.stream().map(it -> {
                    DeviceData data = new DeviceData();
                    BeanUtils.copyProperties(it, data);
                    return data;
                }).collect(() -> result, ArrayList::add, ArrayList::addAll);
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
                deviceData.stream().map(it -> {
                    DeviceData data = new DeviceData();
                    BeanUtils.copyProperties(it, data);
                    data.setTime(it.getTime().atTime(LocalTime.MIN));
                    return data;
                }).collect(() -> result, ArrayList::add, ArrayList::addAll);
                DeviceDayHistory first = dayHistoryMapper.selectFirst(id, shardingTable.getTableName());
                if (first == null) continue;
                // 如果开始时间在表的第一条时间之后就认为数据查找完毕，没有必要查询下一张表
                if (startTime.toLocalDate().isAfter(first.getTime())) break;
            }
        }
        return result;
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

}
