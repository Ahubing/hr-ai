package com.open.ai.eros.user;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan(basePackages = {"com.open.ai.eros.user"})
@Configuration
@EnableAspectJAutoProxy
public class ErosUserAutoConfiguration {
}
