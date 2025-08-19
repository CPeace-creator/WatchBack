package com.cjh.watching.watchback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * - @author Cjh。
 * - @date 2025/8/14 14:02。
 **/
// Genre.java
@Data
@TableName("genre")
public class Genre {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
}
