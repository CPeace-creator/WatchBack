package com.cjh.watching.watchback.entity;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* 
* @TableName upcoming
*/
@Data
@TableName("upcoming")
public class Upcoming implements Serializable {

    /**
    * 
    */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
    * 
    */
    
    private Integer adult;
    /**
    * 
    */
    private String backdropPath;
    /**
    * 
    */
    
    private JSON genreIds;
    /**
    * 
    */
    private String originalLanguage;
    /**
    * 
    */
    private String originalTitle;
    /**
    * 
    */
    private String overview;
    /**
    * 
    */
    
    private Double popularity;
    /**
    * 
    */
    private String posterPath;
    /**
    * 
    */
    
    private Date releaseDate;
    /**
    * 
    */
    private String title;
    /**
    * 
    */
    
    private Integer video;
    /**
    * 
    */
    
    private Double voteAverage;
    /**
    * 
    */
    
    private Integer voteCount;
    /**
    * 
    */
    
    private Date minimum;
    /**
    * 
    */
    
    private Date maximum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdult() {
        return adult;
    }

    public void setAdult(Integer adult) {
        this.adult = adult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public JSON getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(JSON genreIds) {
        this.genreIds = genreIds;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getVideo() {
        return video;
    }

    public void setVideo(Integer video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Date getMinimum() {
        return minimum;
    }

    public void setMinimum(Date minimum) {
        this.minimum = minimum;
    }

    public Date getMaximum() {
        return maximum;
    }

    public void setMaximum(Date maximum) {
        this.maximum = maximum;
    }

    // Lombok @Data annotation will automatically generate all getter and setter methods

}
