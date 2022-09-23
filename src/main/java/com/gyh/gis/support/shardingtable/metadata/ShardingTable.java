package com.gyh.gis.support.shardingtable.metadata;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gyh.gis.support.shardingtable.TableShardingPolicyTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


@Schema(description = "分表")
@Data
@Accessors(chain = true)
@TableName("sharding_table")
public class ShardingTable {

    @TableId(type = IdType.AUTO)
    Long id;

    @Schema(description = "原表名称")
    String originName;

    @Schema(description = "分表策略")
    TableShardingPolicyTypeEnum policyType;

    @Schema(description = "当前分片子表名称")
    String tableName;
    @Schema(description = "创建时间")
    LocalDateTime createTime;
    @Schema(description = "子表粗略行数")
    long totalRows;


}
