package com.gyh.gis.service;

import com.gyh.gis.domain.ExamineInfo;
import com.gyh.gis.mapper.ExamineInfoMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * create by GYH on 2023/9/7
 */
@Service
public class ExamineInfoService {
    @Resource
    private ExamineInfoMapper examineInfoMapper;

    public int insert(ExamineInfo examineInfo) {
        return examineInfoMapper.insert(examineInfo);
    }
}
