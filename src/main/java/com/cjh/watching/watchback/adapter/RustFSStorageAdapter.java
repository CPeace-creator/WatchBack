package com.cjh.watching.watchback.adapter;

import com.cjh.watching.watchback.config.RustFSConfig;
import com.cjh.watching.watchback.entity.FileInfo;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * RustFS存储适配器
 *
 * @author: ChickenWing
 * @date: 2025/9/8
 */
public class RustFSStorageAdapter implements StorageAdapter {

    @Resource
    private RustFSConfig rustFSConfig;

    /**
     * rustFSUrl
     */
    @Value("${rustfs.endpoint}")
    private String url;

    @Value("${rustfs.defaultBucket}")
    private String defaultBucket;

    @Override
    @SneakyThrows
    public void createBucket(String bucket) {
        try {
            String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
            rustFSConfig.createBucket(bucketName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public void uploadFile(MultipartFile uploadFile, String bucket, String objectName) {
        try {
            String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
            rustFSConfig.createBucket(bucketName);
            if (objectName != null) {
                rustFSConfig.putObject(objectName + "/" + uploadFile.getOriginalFilename(), uploadFile.getInputStream(), 
                    uploadFile.getSize(), uploadFile.getContentType());
            } else {
                rustFSConfig.putObject(uploadFile.getOriginalFilename(), uploadFile.getInputStream(), 
                    uploadFile.getSize(), uploadFile.getContentType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public List<String> getAllBucket() {
        try {
            // RustFSConfig没有直接提供获取所有bucket的方法，这里返回包含默认bucket的列表
            List<String> buckets = new ArrayList<>();
            buckets.add(defaultBucket);
            return buckets;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    @SneakyThrows
    public List<FileInfo> getAllFile(String bucket) {
        try {
            String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
            List<String> objectKeys = rustFSConfig.listObjects(bucketName);
            List<FileInfo> fileInfos = new ArrayList<>();
            
            for (String key : objectKeys) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(key);
                fileInfo.setDirectoryFlag(false); // 简化处理，假设都是文件
                
                // 获取对象元数据以获取etag
                try {
                    HeadObjectResponse metadata = rustFSConfig.getObjectMetadata(key);
                    fileInfo.setEtag(metadata.eTag());
                } catch (Exception e) {
                    // 如果获取元数据失败，忽略错误
                }
                
                fileInfos.add(fileInfo);
            }
            
            return fileInfos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    @SneakyThrows
    public InputStream downLoad(String bucket, String objectName) {
        try {
            String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
            return rustFSConfig.getObject(objectName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(new byte[0]); // 返回空的输入流而不是null
    }

    @Override
    @SneakyThrows
    public void deleteBucket(String bucket) {
        try {
            String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
            rustFSConfig.deleteBucket(bucketName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public void deleteObject(String bucket, String objectName) {
        try {
            String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
            rustFSConfig.deleteObject(objectName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public String getUrl(String bucket, String objectName) {
        try {
            String bucketName = (bucket != null && !bucket.isEmpty()) ? bucket : defaultBucket;
            return url + "/" + bucketName + "/" + objectName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}