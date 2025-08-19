package com.cjh.watching.watchback.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.cjh.watching.watchback.entity.User;
import com.cjh.watching.watchback.service.UserService;
import com.cjh.watching.watchback.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * - @author Cjh。
 * - @date 2025/8/18 14:50。
 **/
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private UserService userService;
    @PostMapping("/register")
    public SaResult register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public SaResult login(@RequestBody User userLoginDto) {
        return userService.login(userLoginDto);
    }
    @PostMapping("/logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok();
    }
}
