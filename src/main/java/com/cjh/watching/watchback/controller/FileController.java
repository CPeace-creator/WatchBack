package com.cjh.watching.watchback.controller;

/**
 * - @author Cjh。
 * - @date 2025/9/3 18:18。
 **/
import cn.dev33.satoken.util.SaResult;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson.JSONObject;
import com.cjh.watching.watchback.dto.ExcelMediaInfo;
import com.cjh.watching.watchback.dto.MovieSearchRequest;
import com.cjh.watching.watchback.dto.PythonSearchResultDto;
import com.cjh.watching.watchback.service.MovieService;
import com.cjh.watching.watchback.service.impl.FileService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件操作controller
 *
 * @author: ChickenWing
 * @date: 2023/10/14
 */
@RestController
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Resource
    private FileService fileService;
    
    @Resource
    private MovieService movieService;

    @Value("${rustfs.defaultBucket}")
    private String bucket;

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