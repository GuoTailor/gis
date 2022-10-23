package com.gyh.gis.collect;

import com.gyh.gis.dto.DeviceData;
import com.gyh.gis.dto.ResponseInfo;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.dto.resp.DeviceAlarmListResp;
import com.gyh.gis.dto.resp.DeviceStatusResp;
import com.gyh.gis.enums.AlarmStationEnum;
import com.gyh.gis.service.DeviceHistoryData;
import com.gyh.gis.service.DeviceStatusService;
import com.gyh.gis.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * create by GYH on 2022/9/26
 */
@RestController
@Tag(name = "设备状态")
public class DeviceStatusController {
    @Autowired
    private DeviceStatusService deviceStatusService;
    @Autowired
    private DeviceHistoryData deviceHistoryData;
    @Autowired
    private StationService stationService;

    @Operation(summary = "新增一个设备状态数据")
    @PostMapping("/insert")
    public ResponseInfo<Integer> insert(@RequestBody @Valid DeviceStatusInsertReq req) {
        Integer insert = deviceStatusService.insertOrUpdate(req);
        return ResponseInfo.ok(insert);
    }

    @Operation(summary = "获取所有站点的数据")
    @PostMapping("/getAllState")
    public ResponseInfo<List<DeviceStatusResp>> getAllState() {
        return ResponseInfo.ok(deviceStatusService.getAllState());
    }

    @Operation(summary = "获取一个设备状态数据")
    @PostMapping("/getById")
    public ResponseInfo<DeviceStatusResp> getById(@RequestParam("id") Integer id) {
        return ResponseInfo.ok(deviceStatusService.getById(id));
    }

    @Operation(summary = "获取一个站点的历史流量数据", description = "如果查询时间范围小于一天则返回10分钟一个点，如果大于一天则以一天为一个点返回")
    @GetMapping("/selectByTime")
    public ResponseInfo<List<DeviceData>> selectByTime(
            @Parameter(description = "设备id") @RequestParam("id") Integer id,
            @Parameter(description = "开始时间") @RequestParam("startTime") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam("endTime") LocalDateTime endTime) {
        return ResponseInfo.ok(deviceHistoryData.selectByTime(id, startTime, endTime));
    }

    @Operation(summary = "获取所有站点的历史流量报警数据")
    @GetMapping("/selectAllByError")
    public ResponseInfo<DeviceAlarmListResp> selectAllByError(
            @Parameter(description = "开始时间") @RequestParam("startTime") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam("endTime") LocalDateTime endTime,
            @Parameter(description = "状态") @RequestParam(value = "state", required = false) AlarmStationEnum state,
            @Parameter(description = "站点") @RequestParam(value = "station", required = false) String station) {
        return ResponseInfo.ok(deviceHistoryData.selectAllByError(startTime, endTime, state, station));
    }

    @Operation(summary = "取消报警")
    @GetMapping("/cancelAlarm")
    public ResponseInfo<Boolean> cancelAlarm(@RequestParam Long id) {
        return ResponseInfo.ok(deviceStatusService.cancelAlarm(id));
    }

    @Operation(summary = "获取所有河流")
    @GetMapping("/getAllArea")
    public ResponseInfo<List<String>> getAllArea() {
        return ResponseInfo.ok(stationService.getAllArea());
    }
}
