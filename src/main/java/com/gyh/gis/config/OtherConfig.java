package com.gyh.gis.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by gyh on 2021/2/10
 */
@Configuration
public class OtherConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder
                .serializerByType(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                    @Override // 序列化
                    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                        long timestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        jsonGenerator.writeNumber(timestamp);
                    }
                })
                .deserializerByType(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                    @Override // 反序列化
                    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                        long timestamp = jsonParser.getValueAsLong();
                        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
                    }
                });
    }

    /**
     * 注入Money、枚举类的序列化反序列化
     *
     * @return {@link Module}
     */
    @Bean
    public Module jacksonModule() {
        SimpleModule module = new SimpleModule();

        module.addDeserializer(Enum.class, new EnumJsonDeserializer());
        module.addSerializer(Enum.class, new EnumJsonSerializer());
//        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());

        // 解决List<XyzEnum>类型反序列化的问题
        module.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type,
                                                              BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                return new EnumJsonDeserializer(type);
            }
        });

        return module;
    }
}
