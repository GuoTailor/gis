package com.gyh.gis.support.shardingtable.policy.impl;

import com.gyh.gis.support.shardingtable.TableShardingPolicyTypeEnum;
import com.gyh.gis.support.shardingtable.policy.TableShardingPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class YearMonthTableShardingPolicyImpl implements TableShardingPolicy {


    private static final Pattern tableNameYearPattern = Pattern.compile("^.+\\d{6}$");

    private static final Pattern tableNameYearAndIndexPattern = Pattern.compile("^(.+\\d{6}_)(\\d+)$");

    @Override
    public TableShardingPolicyTypeEnum forPolicy() {
        return TableShardingPolicyTypeEnum.YEAR_MONTH;
    }

    @Override
    public String generateShardingTableName(String baseTableName, LocalDateTime time) {
        var year = time.getYear();
        //月份从1开始
        var month = time.getMonth().getValue();
        if (month >= 10) {
            return baseTableName + year + month;
        }
        return baseTableName + year + "0" + month;

    }

    @Override
    public String generateNextShardingExpandTableName(String tableName) {
        if (tableNameYearPattern.matcher(tableName).matches()) {
            //说明还未扩充
            return tableName + "_1";
        }
        var matchResult = tableNameYearAndIndexPattern.matcher(tableName);
        if (matchResult.matches()) {
            String baseName = matchResult.group(1);
            String seq = matchResult.group(2);
            int seqInt = Integer.parseInt(seq) + 1;
            return baseName + seqInt;
        }
        throw new RuntimeException(String.format("原表名称%s不匹配表模式：%s", tableName, "tablenameYYYY_seq"));

    }
}
