package com.open.ai.eros.permission;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan(basePackages = {"com.open.ai.eros.permission"})
@Configuration
@EnableAspectJAutoProxy
public class ErosPermissionAutoConfiguration {
}
