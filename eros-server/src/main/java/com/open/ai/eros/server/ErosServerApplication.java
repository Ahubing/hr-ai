package com.open.ai.eros.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class ErosServerApplication {


    public static void main(String[] args) {
        // 设置代理地址、端口		你们调试的时候替换成自己的代理地址端口
        SpringApplication.run(ErosServerApplication.class, args);
    }

}
