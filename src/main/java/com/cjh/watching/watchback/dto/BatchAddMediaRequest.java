package com.cjh.watching.watchback.dto;

import lombok.Data;

import java.util.List;

/**
 * 批量添加用户媒体收藏请求DTO
 * - @author Cjh。
 * - @date 2025/8/30。
 **/
@Data
public class BatchAddMediaRequest {
    private Integer status;          // 1-已收藏, 2-已观看, 3-想看
    private Integer mediaType;       // 1-电影, 2-电视剧
    private List<Long> mediaIds;     // 媒体ID列表

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMediaType() {
        return mediaType;
    }

    public void setMediaType(Integer mediaType) {
        this.mediaType = mediaType;
    }

    public List<Long> getMediaIds() {
        return mediaIds;
    }

    public void setMediaIds(List<Long> mediaIds) {
        this.mediaIds = mediaIds;
    }
}