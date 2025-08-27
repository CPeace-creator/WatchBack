package com.cjh.watching.watchback.dto;

import com.cjh.watching.watchback.entity.User;
import lombok.Data;

/**
 * - @author Cjh。
 * - @date 2025/8/27 14:24。
 **/
@Data
public class UserDto extends User {
    private Boolean rememberMe;
}
