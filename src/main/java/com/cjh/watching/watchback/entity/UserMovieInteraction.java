package com.cjh.watching.watchback.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMovieInteraction {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 电影ID
     */
    private Integer movieId;

    /**
     * 用户评分(0-10分)
     */
    private BigDecimal rating;

    /**
     * 用户评论
     */
    private String review;

    /**
     * 观看日期
     */
    private Date watchDate;

    /**
     * 是否在愿望清单
     */
    private Boolean isWishlist;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    // 关联的用户对象
    private User user;

    // 关联的电影对象
    private Movie movie;
}
