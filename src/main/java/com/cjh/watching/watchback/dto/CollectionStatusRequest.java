package com.cjh.watching.watchback.dto;

import lombok.Data;

/**
 * 收藏状态管理请求DTO
 * - @author Cjh。
 * - @date 2025/8/30。
 **/
@Data
public class CollectionStatusRequest {
    private Long mediaId;        // 媒体ID
    private Integer mediaType;   // 媒体类型：1-电影, 2-电视剧
    private Integer status;      // 状态：1-已收藏, 2-已观看, 3-想看
    private Boolean isAdd;       // 操作类型：true-添加标识, false-取消标识

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public Integer getMediaType() {
        return mediaType;
    }

    public void setMediaType(Integer mediaType) {
        this.mediaType = mediaType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsAdd() {
        return isAdd;
    }

    public void setIsAdd(Boolean add) {
        isAdd = add;
    }
}