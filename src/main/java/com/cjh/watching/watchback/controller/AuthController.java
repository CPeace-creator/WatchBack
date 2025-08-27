package com.cjh.watching.watchback.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.cjh.watching.watchback.dto.UserDto;
import com.cjh.watching.watchback.entity.User;
import com.cjh.watching.watchback.service.UserService;
import com.cjh.watching.watchback.utils.Result;
import com.fasterxml.jackson.databind.util.JSONPObject;
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
    public SaResult login(@RequestBody UserDto userLoginDto) {
        return userService.login(userLoginDto);
    }
    @PostMapping("/logout")
    public SaResult logout(@RequestBody String loginId) {
        JSONObject parse = JSONObject.parse(loginId);
        StpUtil.logout(parse.getString("loginId"));
        return SaResult.ok();
    }
}
