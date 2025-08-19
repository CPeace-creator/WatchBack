package com.cjh.watching.watchback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * - @author Cjh。
 * - @date 2025/8/14 14:02。
 **/
@Data
@TableName("movie_genre")
public class MovieGenre {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long movieId;
    private Integer genreId;
}
