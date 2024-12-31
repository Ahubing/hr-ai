package com.open.ai.eros.bot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan(basePackages = {"com.open.ai.eros.bot"})
@Configuration
@EnableAspectJAutoProxy
public class ErosBotAutoConfiguration {
}
