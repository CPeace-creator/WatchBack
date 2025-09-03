package com.cjh.watching.watchback.dto;

import lombok.Data;

import java.util.List;

/**
 * 接收Python脚本返回的搜索结果DTO
 * - @author Cjh
 */
@Data
public class PythonSearchResultDto {
    private Integer media_type; // 1-电视剧，2-电影
    private String title;
    private String message;
    private List<SearchResultItem> results;
    private String status;
    private Integer total_results;
    
    @Data
    public static class SearchResultItem {
        private String release_date;
        private String scraped_at;
        private Double rating;
        private String link;
        private Integer rank;
        private String description;
        private String cover_image;
        private String source;
        private String title;
        private String type;
    }
}