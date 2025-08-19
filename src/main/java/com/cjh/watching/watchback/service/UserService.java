package com.cjh.watching.watchback.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cjh.watching.watchback.entity.User;
import com.cjh.watching.watchback.utils.Result;


/**
 * - @author Cjh。
 * - @date 2025/8/14 13:51。
 **/
public interface UserService extends IService<User> {
    SaResult register(User user);
    SaResult login(User user);

}
