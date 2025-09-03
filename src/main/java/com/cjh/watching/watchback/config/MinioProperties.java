package com.cjh.watching.watchback.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * - @author Cjh。
 * - @date 2025/9/3 17:50。
 **/
/**
 * minio 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /**
     * Minio 连接地址
     */
    private String endpoint;

    /**
     * accessKey 或 账号
     */
    private String accessKey;

    /**
     * secretKey 或 密码
     */
    private String secretKey;

    /**
     * 桶名称
     */
    private String bucketName;

    /**
     * 默认是秒 地址过期时间，设置默认值7200秒
     */
    private int expire = 7200;

}

