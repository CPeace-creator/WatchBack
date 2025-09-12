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
    
    /**
     * 批量导入网络搜索Excel文件
     * 从Excel文件中读取电影/电视剧信息，执行搜索、上传和保存操作
     * 
     * @param file Excel文件
     * @return 导入结果
     */
    @PostMapping("/batchImportFromExcel")
    public SaResult batchImportFromExcel(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return SaResult.error("Excel文件不能为空");
        }
        
        if (bucket == null || bucket.trim().isEmpty()) {
            return SaResult.error("存储桶名称不能为空");
        }
        
        List<ExcelMediaInfo> mediaList = new ArrayList<>();
        List<String> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        
        try {
            // 读取Excel文件
            EasyExcel.read(file.getInputStream(), ExcelMediaInfo.class, new PageReadListener<ExcelMediaInfo>(dataList -> {
                for (ExcelMediaInfo info : dataList) {
                    // 转换媒体类型名称为ID
                    if ("电影".equals(info.getMediaTypeName())) {
                        info.setMediaTypeId(1);
                    } else if ("电视剧".equals(info.getMediaTypeName())) {
                        info.setMediaTypeId(2);
                    } else {
                        failList.add(info.getTitle() + "(媒体类型无效)");
                        continue;
                    }
                    mediaList.add(info);
                }
            })).sheet().doRead();
            
            // 对读取的媒体信息进行批量处理
            for (ExcelMediaInfo mediaInfo : mediaList) {
                try {
                    // 1. 调用Python脚本搜索电影/电视剧
                    MovieSearchRequest searchRequest = new MovieSearchRequest();
                    searchRequest.setTitle(mediaInfo.getTitle());
                    searchRequest.setMediaType(mediaInfo.getMediaTypeId());
                    
                    SaResult searchResult = movieService.searchByPythonScript(mediaInfo.getTitle(), mediaInfo.getMediaTypeId());
                    
                    if (searchResult.getCode() != 200) {
                        failList.add(mediaInfo.getTitle() + "(搜索失败: " + searchResult.getMsg() + ")");
                        continue;
                    }
                    
                    // 2. 解析搜索结果，准备上传图片
                    Object searchData = searchResult.getData();
                    if (searchData == null) {
                        failList.add(mediaInfo.getTitle() + "(搜索结果为空)");
                        continue;
                    }
                    
                    // 将搜索结果转换为PythonSearchResultDto
                    PythonSearchResultDto searchResultDto;
                    if (searchData instanceof PythonSearchResultDto) {
                        searchResultDto = (PythonSearchResultDto) searchData;
                    } else {
                        String jsonStr = JSONObject.toJSONString(searchData);
                        searchResultDto = JSONObject.parseObject(jsonStr, PythonSearchResultDto.class);
                    }
                    
                    if (searchResultDto.getResults() == null || searchResultDto.getResults().isEmpty()) {
                        failList.add(mediaInfo.getTitle() + "(搜索结果列表为空)");
                        continue;
                    }
                    
                    // 3. 上传封面图片
                    PythonSearchResultDto.SearchResultItem resultItem = searchResultDto.getResults().get(0);
                    String coverImage = resultItem.getCover_image();
                    
                    if (coverImage != null && !coverImage.isEmpty()) {
                        try {
                            // 生成唯一的对象名
                            String objectName = UUID.randomUUID().toString() + ".jpg";
                            uploadFromUrl(coverImage, bucket, objectName);
                        } catch (Exception e) {
                            log.warn("上传图片失败: {}", e.getMessage());
                            // 图片上传失败不影响后续保存操作
                        }
                    }
                    
                    // 4. 自动保存搜索结果
                    SaResult saveResult = movieService.autoSaveFromPythonResult(searchResultDto);
                    
                    if (saveResult.getCode() == 200) {
                        successList.add(mediaInfo.getTitle());
                    } else {
                        failList.add(mediaInfo.getTitle() + "(保存失败: " + saveResult.getMsg() + ")");
                    }
                    
                } catch (Exception e) {
                    log.error("处理媒体信息失败: {}", e.getMessage(), e);
                    failList.add(mediaInfo.getTitle() + "(处理异常: " + e.getMessage() + ")");
                }
            }
            
            // 构建返回结果
            JSONObject result = new JSONObject();
            result.put("total", mediaList.size());
            result.put("successCount", successList.size());
            result.put("failCount", failList.size());
            result.put("successList", successList);
            result.put("failList", failList);
            
            return SaResult.ok().setData(result);
            
        } catch (IOException e) {
            log.error("读取Excel文件失败: {}", e.getMessage(), e);
            return SaResult.error("读取Excel文件失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("批量导入失败: {}", e.getMessage(), e);
            return SaResult.error("批量导入失败: " + e.getMessage());
        }
    }

}