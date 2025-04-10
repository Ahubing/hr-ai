package com.open.ai.eros.admin;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan(basePackages = {"com.open.ai.eros.admin"})
@Configuration
@EnableAspectJAutoProxy
public class ErosAdminAutoConfiguration {
}
