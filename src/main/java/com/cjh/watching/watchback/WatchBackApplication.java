package com.cjh.watching.watchback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
@MapperScan("com.cjh.watching.watchback.mapper")
public class WatchBackApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WatchBackApplication.class, args);
    }
}
