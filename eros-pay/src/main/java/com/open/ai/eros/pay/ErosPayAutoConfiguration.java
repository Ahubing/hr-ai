package com.open.ai.eros.pay;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan(basePackages = {"com.open.ai.eros.pay"})
@Configuration
@EnableAspectJAutoProxy
public class ErosPayAutoConfiguration {
}
