package com.cjh.watching.watchback.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * - @author Cjh。
 * - @date 2025/8/26 17:39。
 **/
@Data
public class MovieDto {
    private String originalLanguage;
    private String originalTitle;
    private String title;
    private BigDecimal voteAverage;
    private Integer voteCount;
    private BigDecimal popularity;
    private String posterPath;
    private String backdropPath;
    private String overview;
    private String genreIds;
    private Integer tmdbId;
    private Integer mediaType;
    private LocalDateTime createdTime;
}
