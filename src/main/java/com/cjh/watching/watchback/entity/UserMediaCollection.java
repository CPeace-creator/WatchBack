package com.cjh.watching.watchback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * - @author Cjh。
 * - @date 2025/8/26 21:37。
 **/
@Data
@TableName("user_media_collection")
public class UserMediaCollection {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer mediaType; // 1-电影, 2-电视剧
    private Long mediaId;
    private Integer tmdbId;
    private String title;
    private Integer status; // 1-已收藏, 2-已观看, 3-想看
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}