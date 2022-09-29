package com.gyh.gis.config;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class EnumJsonDeserializer extends StdDeserializer<Enum> {
    private static final String ENUM_VALUE_KEY = "name";

    public EnumJsonDeserializer() {
        super((Class<?>) null);
    }

    protected EnumJsonDeserializer(Class<?> vc) {
        super(vc);
    }

    public EnumJsonDeserializer(JavaType valueType) {
        super(valueType);
    }

    protected EnumJsonDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public Enum deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        Class<? extends Enum> type = (Class<? extends Enum>) handledType();
        //字符串转枚举,比如前端post传参:type=LOCAL转成LOCAL(枚举实例)
        if (StringUtils.isNotEmpty(node.asText())) {
            return Enum.valueOf(type, node.asText());
        }
        //json对象转枚举,比如:"type":{"name":"LOCAL"}转成LOCAL(枚举实例)
        if (node.get(ENUM_VALUE_KEY) != null) {
            return Enum.valueOf(type, node.get(ENUM_VALUE_KEY).asText());
        }
        throw new RuntimeException("反序列化枚举出错!");
    }

    @Override
    public boolean isCachable() {
        // 缓存避免每次都new
        return true;
    }
}
