package com.gyh.gis.support.shardingtable.executor.input;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class DetermineTableNameForNewInput {
    LocalDateTime createTime;
    /**
     * 原表名
     */
    String originTableName;


}
