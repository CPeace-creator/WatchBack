package com.cjh.watching.watchback.dto;

import lombok.Data;

/**
 * 电影/电视剧搜索请求DTO
 * 用于通过POST请求调用Python搜索脚本
 * 
 * @author Cjh
 * @date 2025/8/31
 */
@Data
public class MovieSearchRequest {
    private String title;      // 搜索的标题
    private Integer mediaType; // 媒体类型：1表示电视剧，2表示电影

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getMediaType() {
        return mediaType;
    }

    public void setMediaType(Integer mediaType) {
        this.mediaType = mediaType;
    }
}