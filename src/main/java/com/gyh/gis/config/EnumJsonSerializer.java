package com.gyh.gis.config;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gyh.gis.enums.Messageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.JsonObjectSerializer;

import java.io.IOException;

@Slf4j
public class EnumJsonSerializer extends JsonObjectSerializer<Enum> {

    @Override
    protected void serializeObject(Enum value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStringField("name", value.name());
        if (value instanceof Messageable) {
            generator.writeStringField("code", ((Messageable) value).code()); //todo 语言包的支持
            generator.writeStringField("message", ((Messageable) value).message());
        }
    }

    @Override
    public Class<Enum> handledType() {
        return Enum.class;
    }
}
