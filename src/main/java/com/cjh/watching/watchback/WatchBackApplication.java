package com.cjh.watching.watchback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
@MapperScan("com.cjh.watching.watchback.mapper")
@EnableCaching  // 启用Spring缓存功能
public class WatchBackApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WatchBackApplication.class, args);
    }
}
