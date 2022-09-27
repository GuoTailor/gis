package com.gyh.gis.config;

import com.gyh.gis.support.shardingtable.TableShardingConfig;
import com.gyh.gis.support.shardingtable.TableShardingPolicyTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Component
@ConfigurationProperties("gis.storage-metadata")
public class StorageMetadataTableShardingConfig extends TableShardingConfig {
    protected Map<String, ShardingConfig> configs = new HashMap<>();

}
