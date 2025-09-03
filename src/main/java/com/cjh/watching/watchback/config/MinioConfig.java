package com.cjh.watching.watchback.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * minio配置管理
 */
@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}")
    private String url;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * 构建minioClient
     */
    @Bean
    public MinioClient getMinioClient(){
        return MinioClient.builder().endpoint(url).credentials(accessKey,secretKey).build();
    }

}
