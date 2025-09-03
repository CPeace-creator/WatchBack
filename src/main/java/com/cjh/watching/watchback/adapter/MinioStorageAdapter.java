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

    @Override
    @SneakyThrows
    public void createBucket(String bucket) {
       try{
           minioUtil.createBucket(bucket);
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    @Override
    @SneakyThrows
    public void uploadFile(MultipartFile uploadFile, String bucket, String objectName) {
        try {
            minioUtil.createBucket(bucket);
            if (objectName != null) {
                minioUtil.uploadFile(uploadFile.getInputStream(), bucket, objectName + "/" + uploadFile.getOriginalFilename());
            } else {
                minioUtil.uploadFile(uploadFile.getInputStream(), bucket, uploadFile.getOriginalFilename());
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
            return minioUtil.getAllFile(bucket);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @SneakyThrows
    public InputStream downLoad(String bucket, String objectName) {
        try{
             return minioUtil.downLoad(bucket, objectName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    @Override
    @SneakyThrows
    public void deleteBucket(String bucket) {
        try{
            minioUtil.deleteBucket(bucket);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public void deleteObject(String bucket, String objectName) {
        try{
            minioUtil.deleteObject(bucket, objectName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public String getUrl(String bucket, String objectName) {
        try{
            return url + "/" + bucket + "/" + objectName;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

}

