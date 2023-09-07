package com.gyh.gis.mapper;

import com.gyh.gis.domain.ExamineInfo;

/**
 * create by GYH on 2023/9/7
 */
public interface ExamineInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ExamineInfo record);

    int insertSelective(ExamineInfo record);

    ExamineInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ExamineInfo record);

    int updateByPrimaryKey(ExamineInfo record);
}