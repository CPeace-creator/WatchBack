package com.cjh.watching.watchback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 电视剧实体类，对应tv_shows表
 * - @author Cjh。
 * - @date 2025/8/26 22:40。
 **/
@Data
@TableName("tv_shows")
public class TVShow {
    
    @TableId(type = IdType.AUTO)
    private Integer showId;
    
    private Integer tmdbId;
    
    private String name;
    
    private String originalName;
    
    private String overview;
    
    private LocalDate firstAirDate;
    
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private JsonNode originCountry;
    
    private String originalLanguage;
    
    private String genreIds;
    
    private BigDecimal popularity;
    
    private BigDecimal voteAverage;
    
    private Integer voteCount;
    
    private String posterPath;
    
    private String backdropPath;
    
    private String sourceApi;
}