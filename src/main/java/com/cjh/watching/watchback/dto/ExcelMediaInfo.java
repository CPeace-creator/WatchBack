package com.cjh.watching.watchback.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * Excel中的媒体信息实体类
 * 用于批量导入电影/电视剧信息
 * 
 * @author Cjh
 * @date 2025/9/10
 */
@Data
public class ExcelMediaInfo {
    
    @ExcelProperty("标题")
    private String title; // 电影/电视剧标题
    
    @ExcelProperty("类型")
    private String mediaTypeName; // 媒体类型名称（电影/电视剧）
    
    // 非Excel字段，用于存储转换后的媒体类型ID
    private Integer mediaTypeId;
}