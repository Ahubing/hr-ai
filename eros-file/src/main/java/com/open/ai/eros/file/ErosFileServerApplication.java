package com.open.ai.eros.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableAspectJAutoProxy
public class ErosFileServerApplication {


    public static void main(String[] args) {
        // 设置代理地址、端口		你们调试的时候替换成自己的代理地址端口
        SpringApplication.run(ErosFileServerApplication.class, args);
    }

}
