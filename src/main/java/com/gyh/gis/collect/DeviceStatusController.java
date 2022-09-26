package com.gyh.gis.collect;

import com.gyh.gis.dto.ResponseInfo;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.dto.resp.DeviceStatusResp;
import com.gyh.gis.service.DeviceStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * create by GYH on 2022/9/26
 */
@RestController
@Tag(name = "设备状态")
public class DeviceStatusController {
    @Autowired
    private DeviceStatusService deviceStatusService;

    @Operation(summary = "新增一个设备状态数据")
    @PostMapping("/insert")
    public ResponseInfo<Integer> insert(@RequestBody @Valid DeviceStatusInsertReq req) {
        Integer insert = deviceStatusService.insert(req);
        return ResponseInfo.ok(insert);
    }

    @Operation(summary = "获取一个设备状态数据")
    @PostMapping("/getById")
    public ResponseInfo<DeviceStatusResp> getById(@RequestParam("id") Integer id) {
        return ResponseInfo.ok(deviceStatusService.getById(id));
    }
}
