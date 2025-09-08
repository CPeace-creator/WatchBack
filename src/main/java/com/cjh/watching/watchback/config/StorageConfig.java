package com.cjh.watching.watchback.config;

import com.cjh.watching.watchback.adapter.MinioStorageAdapter;
import com.cjh.watching.watchback.adapter.RustFSStorageAdapter;
import com.cjh.watching.watchback.adapter.StorageAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class StorageConfig {
    @Value("${storage.service.type}")
    private String type;


    @Bean
    @Primary
    public StorageAdapter storageService(){
        if("minio".equals(type)){
            return new MinioStorageAdapter();
        } else if("rustfs".equals(type)){
            return new RustFSStorageAdapter();
        } else{
            throw new IllegalArgumentException("未找到对应的文件存储处理器");
        }
    }
}