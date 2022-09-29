package com.gyh.gis.collect;

import com.gyh.gis.dto.DeviceData;
import com.gyh.gis.dto.ResponseInfo;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.dto.resp.DeviceStatusResp;
import com.gyh.gis.service.DeviceHistoryData;
import com.gyh.gis.service.DeviceStatusService;
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

    @Operation(summary = "新增一个设备状态数据")
    @PostMapping("/insert")
    public ResponseInfo<Integer> insert(@RequestBody @Valid DeviceStatusInsertReq req) {
        Integer insert = deviceStatusService.insertOrUpdate(req);
        return ResponseInfo.ok(insert);
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

    @Operation(summary = "取消报警")
    @GetMapping("/cancelAlarm")
    public ResponseInfo<Boolean> cancelAlarm(@RequestParam Integer id) {
        return ResponseInfo.ok(deviceStatusService.cancelAlarm(id));
    }
}
