package com.cjh.watching.watchback.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.util.SaResult;
import com.cjh.watching.watchback.service.StatisticsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 统计数据控制器
 * 提供各类统计图表数据的API接口
 * 
 * @author Cjh
 * @date 2025/8/29
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/statistics")
public class StatisticsController {
    
    @Resource
    private StatisticsService statisticsService;
    
    /**
     * 获取月度观影趋势数据
     * 用于展示月度观影趋势图
     */
    @SaCheckLogin
    @GetMapping("/monthly-trend")
    public SaResult getMonthlyTrend() {
        try {
            return SaResult.ok().setData(statisticsService.getMonthlyTrend());
        } catch (Exception e) {
            return SaResult.error("获取月度观影趋势数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取类型分布数据
     * 用于展示类型分布图
     */
    @SaCheckLogin
    @GetMapping("/genre-distribution")
    public SaResult getGenreDistribution() {
        try {
            return SaResult.ok().setData(statisticsService.getGenreDistribution());
        } catch (Exception e) {
            return SaResult.error("获取类型分布数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取评分分布数据
     * 用于展示评分分布图
     */
    @SaCheckLogin
    @GetMapping("/rating-distribution")
    public SaResult getRatingDistribution() {
        try {
            return SaResult.ok().setData(statisticsService.getRatingDistribution());
        } catch (Exception e) {
            return SaResult.error("获取评分分布数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取年度报告数据
     * 用于展示年度报告信息
     */
    @SaCheckLogin
    @GetMapping("/yearly-report")
    public SaResult getYearlyReport() {
        try {
            return SaResult.ok().setData(statisticsService.getYearlyReport());
        } catch (Exception e) {
            return SaResult.error("获取年度报告数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有统计数据
     * 一次性返回所有图表所需的数据
     */
    @SaCheckLogin
    @GetMapping("/all-statistics")
    public SaResult getAllStatistics() {
        try {
            return SaResult.ok().setData(statisticsService.getAllStatistics());
        } catch (Exception e) {
            return SaResult.error("获取统计数据失败: " + e.getMessage());
        }
    }
}