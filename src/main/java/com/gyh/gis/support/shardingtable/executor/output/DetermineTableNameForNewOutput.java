package com.gyh.gis.support.shardingtable.executor.output;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DetermineTableNameForNewOutput {
    String tableName;
}
