package com.open.ai.eros.ai;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan(basePackages = {"com.open.ai.eros.ai"})
@Configuration
@EnableAspectJAutoProxy
public class ErosAIAutoConfiguration {
}
