package com.open.ai.eros.creator;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan(basePackages = {"com.open.ai.eros.creator"})
@Configuration
@EnableAspectJAutoProxy
public class ErosCreatorAutoConfiguration {
}
