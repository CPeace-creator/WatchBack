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

    // Lombok @Data annotation will automatically generate all getter and setter methods

}
