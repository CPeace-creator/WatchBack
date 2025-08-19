package com.cjh.watching.watchback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * - @author Cjh。
 * - @date 2025/8/18 14:54。
 **/
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userName;
    private String email;
    private String password;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer ifDel;
}
