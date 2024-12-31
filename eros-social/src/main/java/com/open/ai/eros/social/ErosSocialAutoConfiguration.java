package com.open.ai.eros.social;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan(basePackages = {"com.open.ai.eros.social"})
@Configuration
@EnableAspectJAutoProxy
public class ErosSocialAutoConfiguration {
}
