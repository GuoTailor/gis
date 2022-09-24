package com.gyh.gis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gyh.gis.domain.Test;
import org.apache.ibatis.annotations.Param;

/**
 * create by GYH on 2022/9/24
 */
public interface TestMapper extends BaseMapper<Test> {
    int deleteByPrimaryKey(Integer id);

    int insert(Test record);

    int insertSelective(@Param("record") Test record,@Param("tableName") String tableName);

    Test selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Test record);

    int updateByPrimaryKey(Test record);
}