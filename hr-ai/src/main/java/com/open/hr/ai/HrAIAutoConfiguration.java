package com.open.hr.ai;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan(basePackages = {"com.open.hr.ai"})
@Configuration
@EnableAspectJAutoProxy
public class HrAIAutoConfiguration {
}
