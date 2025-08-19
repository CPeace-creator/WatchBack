package com.cjh.watching.watchback.service.impl;

import cn.dev33.satoken.secure.SaBase64Util;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjh.watching.watchback.entity.User;
import com.cjh.watching.watchback.mapper.UserMapper;
import com.cjh.watching.watchback.service.UserService;
import com.cjh.watching.watchback.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * - @author Cjh。
 * - @date 2025/8/14 13:53。
 **/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;
    
    @Resource
    private PasswordEncoder passwordEncoder;
    @Override
    public SaResult register(User user) {
        // 检查用户名是否已存在
        long count = this.count(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, user.getUserName()).or().eq(User::getEmail,user.getEmail()));
        if (count>0) {
            return SaResult.error("用户或邮箱已存在!");
        }

        // 创建用户并使用PasswordEncoder加密密码
        User insertUser = new User();
        insertUser.setUserName(user.getUserName());
        insertUser.setEmail(user.getEmail());
        insertUser.setPassword(passwordEncoder.encode(user.getPassword()));  // 使用BCrypt加密

        // 保存用户
        save(insertUser);
        return SaResult.ok("注册成功");
    }

    @Override
    public SaResult login(User userLoginDto) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, userLoginDto.getEmail()));

        if (user == null || !passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            return SaResult.error("用户不存在或密码错误");
        }

        // 4. 检查用户状态（如是否被禁用、删除等）
        if (user.getIfDel() == 1) {
            return SaResult.error("账号已被禁用");
        }

        // 5. 登录并返回token
        StpUtil.login(user.getId());

        // 6. 返回登录成功信息及token
        return SaResult.ok("登录成功")
                .setData(StpUtil.getTokenInfo());
    }
}
