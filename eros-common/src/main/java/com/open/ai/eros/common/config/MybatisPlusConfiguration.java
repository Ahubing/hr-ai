package com.open.ai.eros.common.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfiguration {
    // 分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return  new PaginationInterceptor();
    }

}
