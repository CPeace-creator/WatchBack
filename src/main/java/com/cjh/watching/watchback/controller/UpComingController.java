package com.cjh.watching.watchback.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cjh.watching.watchback.entity.Upcoming;
import com.cjh.watching.watchback.service.UpcomingService;
import com.cjh.watching.watchback.utils.PageRequest;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Python脚本调用控制器
 * 提供Python脚本调用的REST API接口
 * 
 * @author Cjh
 * @date 2025/8/29
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/movies/")
public class UpComingController {

    @Resource
    private UpcomingService upcomingService;
    /**
     * 获取即将上映的电影数据
     */
    @SaCheckLogin
    @PostMapping("upcoming")
    public SaResult getUpcomingMovies(@RequestBody PageRequest page) {
        try{
            // 获取电影数据
            IPage<Upcoming> movieData = upcomingService.getPages(page);
            
            return SaResult.ok("获取即将上映电影数据成功").setData(movieData);
            
        } catch (Exception e) {
            return SaResult.error("获取电影数据失败: " + e.getMessage());
        }
    }

}