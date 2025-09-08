package com.cjh.watching.watchback.adapter;

/**
 * - @author Cjh。
 * - @date 2025/9/3 18:16。
 **/

import com.cjh.watching.watchback.entity.FileInfo;
import com.cjh.watching.watchback.utils.MinioUtils;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * minioIO存储适配器
 *
 * @author: ChickenWing
 * @date: 2023/10/14
 */
public class MinioStorageAdapter implements StorageAdapter {

    @Resource
    private MinioUtils minioUtil;

    /**
     * minioUrl
     */
    @Value("${minio.endpoint}")
    private String url;
    
    /**
     * minio默认bucket
     */
    @Value("${minio.bucketName}")
    private String defaultBucket;

    @Override
    @SneakyThrows
    public void createBucket(String bucket) {
       try{
           String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
           minioUtil.createBucket(bucketName);
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    @Override
    @SneakyThrows
    public void uploadFile(MultipartFile uploadFile, String bucket, String objectName) {
        try {
            String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
            minioUtil.createBucket(bucketName);
            if (objectName != null) {
                minioUtil.uploadFile(uploadFile.getInputStream(), bucketName, objectName + "/" + uploadFile.getOriginalFilename());
            } else {
                minioUtil.uploadFile(uploadFile.getInputStream(), bucketName, uploadFile.getOriginalFilename());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public List<String> getAllBucket() {
        try{
            return minioUtil.getAllBucket();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @SneakyThrows
    public List<FileInfo> getAllFile(String bucket) {
        try{
            String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
            return minioUtil.getAllFile(bucketName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @SneakyThrows
    public InputStream downLoad(String bucket, String objectName) {
        try{
             String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
             return minioUtil.downLoad(bucketName, objectName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    @Override
    @SneakyThrows
    public void deleteBucket(String bucket) {
        try{
            String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
            minioUtil.deleteBucket(bucketName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public void deleteObject(String bucket, String objectName) {
        try{
            String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
            minioUtil.deleteObject(bucketName, objectName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public String getUrl(String bucket, String objectName) {
        try{
            String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
            return url + "/" + bucketName + "/" + objectName;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

}