package com.open.ai.eros.db.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;

@Configuration
@AutoConfigureAfter(JacksonAutoConfiguration.class)
public class JacksonConfiguration {

    public JacksonConfiguration(ObjectMapper objectMapper) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new ToStringSerializer(Long.TYPE));
        module.addSerializer(new ToStringSerializer(Long.class));
        module.addSerializer(new ToStringSerializer(BigInteger.class));
        objectMapper.registerModule(module);
    }
}
