package com.cjh.watching.watchback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * - @author Cjh。
 * - @date 2025/8/14 13:52。
 **/
// Movie.java
@Data
@TableName("movies")
public class Movie {
    @TableId(type = IdType.AUTO)
    private Long movieId;
    private String adult;
    private String originalLanguage;
    private String originalTitle;
    private String title;
    private LocalDateTime releaseDate;
    private BigDecimal voteAverage;
    private Integer voteCount;
    private BigDecimal popularity;
    private String posterPath;
    private String backdropPath;
    private String overview;
    private Integer video;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String genreIds;
    private Integer ifDel;
}
