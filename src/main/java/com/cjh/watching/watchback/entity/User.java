package com.cjh.watching.watchback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * - @author Cjh。
 * - @date 2025/8/18 14:54。
 **/
@Data
@TableName("user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userName;
    private String email;
    private String password;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer ifDel;
    // 手动添加符合字段名的getter
    public String getUserName() {
        return this.userName;
    }

    // 手动添加setter
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return this.email;
    }

    // 手动添加setter
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    // 手动添加setter
    public void setPassword(String password) {
        this.password = password;
    }
    public Integer getIfDel() {
        return this.ifDel;
    }

    public void setIfDel(Integer ifDel) {
        this.ifDel = ifDel;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
