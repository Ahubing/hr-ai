package com.open.ai.eros.db;

import com.open.ai.eros.db.privacy.interceptor.CryptoInterceptor;
import com.open.ai.eros.db.privacy.interceptor.DesensitizeInterceptor;
import com.open.ai.eros.db.privacy.properties.CryptoProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@ComponentScan(value = {"com.open.ai.eros.db.mysql","com.open.ai.eros.db.redis"})
@MapperScan("com.open.ai.eros.db.mysql.**.mapper.**")
@Configuration
@EnableConfigurationProperties(CryptoProperties.class)
public class ErosDbAutoConfiguration {

    @Bean
    public CryptoInterceptor cryptoInterceptor(){
        return new CryptoInterceptor();
    }

    @Bean
    public DesensitizeInterceptor desensitizeInterceptor(){
        return new DesensitizeInterceptor();
    }


}
