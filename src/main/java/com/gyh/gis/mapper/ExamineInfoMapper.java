package com.gyh.gis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gyh.gis.domain.ExamineInfo;

/**
 * create by GYH on 2023/9/10
 */
public interface ExamineInfoMapper extends BaseMapper<ExamineInfo> {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(ExamineInfo record);

    ExamineInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ExamineInfo record);

    int updateByPrimaryKey(ExamineInfo record);
}