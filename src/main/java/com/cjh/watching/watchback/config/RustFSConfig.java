package com.cjh.watching.watchback.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RustFS 配置类
 *
 * @author: Cjh
 * @date: 2025/9/8
 */
@Configuration
public class RustFSConfig {
    
    @Value("${rustfs.endpoint}")
    private String endpoint;
    
    @Value("${rustfs.accessKey}")
    private String accessKey;
    
    @Value("${rustfs.secretKey}")
    private String secretKey;
    
    @Value("${rustfs.region}")
    private String region;
    
    @Value("${rustfs.defaultBucket}")
    private String defaultBucketName;
    
    private S3Client s3Client;
    private S3Presigner presigner;

    @PostConstruct
    public void init() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .forcePathStyle(true)
                .build();

        this.presigner = S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     * @throws S3Exception 如果创建失败或桶已存在
     */
    public void createBucket(String bucketName) throws S3Exception {
        try {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.createBucket(createBucketRequest);
            System.out.println("Bucket created: " + bucketName);
        } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException e) {
            System.out.println("Bucket already exists: " + bucketName);
            throw e;
        }
    }

    /**
     * 上传文件（从输入流）
     *
     * @param key          对象键
     * @param inputStream  输入流
     * @param contentLength 流内容的长度
     * @param contentType  内容类型（MIME类型）
     * @return 上传结果的 ETag
     */
    public String putObject(String key, InputStream inputStream, long contentLength, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(defaultBucketName)
                .key(key)
                .contentType(contentType)
                .contentLength(contentLength)
                .build();

        PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
        return response.eTag();
    }

    /**
     * 下载文件到输入流（适合大文件或流式处理）
     *
     * @param key 对象键
     * @return 包含对象内容的 ResponseInputStream
     */
    public ResponseInputStream<GetObjectResponse> getObject(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(defaultBucketName)
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

    /**
     * 列出存储桶中的所有对象
     *
     * @param bucketName 存储桶名称
     * @return 对象键列表
     */
    public List<String> listObjects(String bucketName) {
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
        return listObjectsResponse.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    /**
     * 删除对象
     *
     * @param key 对象键
     */
    public void deleteObject(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(defaultBucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    /**
     * 删除存储桶（存储桶必须为空）
     *
     * @param bucketName 存储桶名称
     */
    public void deleteBucket(String bucketName) {
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build();

        s3Client.deleteBucket(deleteBucketRequest);
    }

    /**
     * 生成对象的预签名 URL（用于临时访问，如下载）
     *
     * @param key      对象键
     * @param duration URL有效时长
     * @return 预签名 URL
     */
    public String generatePresignedUrl(String key, Duration duration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(defaultBucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(duration)
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }
    
    /**
     * 获取对象元数据
     *
     * @param key 对象键
     * @return 包含元数据的 HeadObjectResponse
     */
    public HeadObjectResponse getObjectMetadata(String key) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(defaultBucketName)
                .key(key)
                .build();

        return s3Client.headObject(headObjectRequest);
    }

    /**
     * 关闭客户端，释放资源
     */
    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
        if (presigner != null) {
            presigner.close();
        }
    }

    // 提供 getter 方法
    public S3Client getS3Client() {
        return s3Client;
    }

    public S3Presigner getPresigner() {
        return presigner;
    }

    public String getDefaultBucketName() {
        return defaultBucketName;
    }
}