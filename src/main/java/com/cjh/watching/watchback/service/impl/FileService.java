package com.cjh.watching.watchback.service.impl;

/**
 * - @author Cjh。
 * - @date 2025/9/3 18:15。
 **/

import com.cjh.watching.watchback.adapter.StorageAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 文件存储service
 *
 * @author: ChickenWing
 * @date: 2023/10/14
 */
@Service
public class FileService {

    private final StorageAdapter storageAdapter;

    public FileService(StorageAdapter storageAdapter) {
        this.storageAdapter = storageAdapter;
    }

    /**
     * 列出所有桶
     */
    public List<String> getAllBucket() {
        return storageAdapter.getAllBucket();
    }

    /**
     * 获取文件路径
     */
    public String getUrl(String bucketName,String objectName) {
        return storageAdapter.getUrl(bucketName,objectName);
    }

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile uploadFile, String bucket, String objectName){
        storageAdapter.uploadFile(uploadFile, bucket, objectName);
        objectName = objectName + "/" + uploadFile.getOriginalFilename();
        return storageAdapter.getUrl(bucket, objectName);
    }
    
    /**
     * 从网络链接上传文件
     */
    public String uploadFileFromUrl(String fileUrl, String bucket, String objectName) throws Exception {
        // 从URL下载文件内容
        byte[] fileBytes = downloadFileFromUrl(fileUrl);
        
        // 创建一个模拟的MultipartFile
        String fileName = getFileNameFromUrl(fileUrl);
        MultipartFile multipartFile = new UrlMultipartFile(fileBytes, fileName);
        
        // 使用现有的上传方法
        storageAdapter.uploadFile(multipartFile, bucket, objectName);
        
        // 返回文件访问URL
        if (objectName != null && !objectName.isEmpty()) {
            objectName = objectName + "/" + fileName;
        } else {
            objectName = fileName;
        }
        return storageAdapter.getUrl(bucket, objectName);
    }
    
    /**
     * 删除文件
     */
    public void deleteFile(String bucket, String objectName) {
        storageAdapter.deleteObject(bucket, objectName);
    }
    
    /**
     * 从URL下载文件内容
     */
    private byte[] downloadFileFromUrl(String fileUrl) throws Exception {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        try (InputStream inputStream = connection.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            
            int nRead;
            byte[] data = new byte[1024];
            
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            
            return buffer.toByteArray();
        }
    }
    
    /**
     * 从URL中提取文件名
     */
    private String getFileNameFromUrl(String fileUrl) {
        try {
            String path = new URL(fileUrl).getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            return fileName.isEmpty() ? "unnamed_file" : fileName;
        } catch (Exception e) {
            return "unnamed_file";
        }
    }
    
    /**
     * 简单的MultipartFile实现类，用于从URL下载的文件
     */
    private static class UrlMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String originalFilename;
        
        public UrlMultipartFile(byte[] content, String originalFilename) {
            this.content = content;
            this.originalFilename = originalFilename;
        }
        
        @Override
        public String getName() {
            return originalFilename;
        }
        
        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }
        
        @Override
        public String getContentType() {
            // 简单实现，实际项目中可能需要根据文件内容或扩展名判断
            return "application/octet-stream";
        }
        
        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }
        
        @Override
        public long getSize() {
            return content.length;
        }
        
        @Override
        public byte[] getBytes() {
            return content;
        }
        
        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }
        
        @Override
        public void transferTo(java.io.File dest) {
            try (java.io.FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}