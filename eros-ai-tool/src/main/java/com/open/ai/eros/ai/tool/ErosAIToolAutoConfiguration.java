package com.open.ai.eros.ai.tool;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan(basePackages = {"com.open.ai.eros.ai.tool"})
@Configuration
@EnableAspectJAutoProxy
public class ErosAIToolAutoConfiguration {
}
