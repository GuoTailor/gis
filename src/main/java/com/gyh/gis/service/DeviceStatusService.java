package com.gyh.gis.service;

import com.gyh.gis.domain.DeviceStatus;
import com.gyh.gis.dto.req.DeviceStatusInsertReq;
import com.gyh.gis.dto.resp.DeviceStatusResp;
import com.gyh.gis.mapper.DeviceStatusMapper;
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
