package com.cjh.watching.watchback.controller;

/**
 * - @author Cjh。
 * - @date 2025/9/3 18:18。
 **/
import cn.dev33.satoken.util.SaResult;
import com.cjh.watching.watchback.service.impl.FileService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 文件操作controller
 *
 * @author: ChickenWing
 * @date: 2023/10/14
 */
@RestController
public class FileController {

    @Resource
    private FileService fileService;

    @RequestMapping("/testGetAllBuckets")
    public String testGetAllBuckets() throws Exception {
        List<String> allBucket = fileService.getAllBucket();
        return allBucket.get(0);
    }

    @RequestMapping("/getUrl")
    public String getUrl(String bucketName, String objectName) throws Exception {
        return fileService.getUrl(bucketName, objectName);
    }

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public SaResult upload(MultipartFile uploadFile, String bucket, String objectName) throws Exception {
        String url = fileService.uploadFile(uploadFile, bucket, objectName);
        return SaResult.ok().setData( url);
    }
    
    /**
     * 从网络链接上传文件
     */
    @PostMapping("/uploadFromUrl")
    public SaResult uploadFromUrl(String fileUrl, String bucket, String objectName) throws Exception {
        String url = fileService.uploadFileFromUrl(fileUrl, bucket, objectName);
        return SaResult.ok().setData(url);
    }
    
    /**
     * 删除文件
     */
    @PostMapping("/deleteFile")
    public SaResult deleteFile(String bucket, String objectName) {
        try {
            fileService.deleteFile(bucket, objectName);
            return SaResult.ok("文件删除成功");
        } catch (Exception e) {
            return SaResult.error("文件删除失败: " + e.getMessage());
        }
    }

}