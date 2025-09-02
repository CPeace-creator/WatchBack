package com.cjh.watching.watchback.service;

import java.util.Map;

/**
 * 统计服务接口
 * 定义各类统计数据获取方法
 * 
 * @author Cjh
 * @date 2025/8/29
 */
public interface StatisticsService {
    
    /**
     * 获取月度观影趋势数据
     * @return 包含月度观影数据的Map对象
     */
    Map<String, Object> getMonthlyTrend();
    
    /**
     * 获取类型分布数据
     * @return 包含类型分布数据的Map对象
     */
    Map<String, Object> getGenreDistribution();
    
    /**
     * 获取评分分布数据
     * @return 包含评分分布数据的Map对象
     */
    Map<String, Object> getRatingDistribution();
    
    /**
     * 获取年度报告数据
     * @return 包含年度报告数据的Map对象
     */
    Map<String, Object> getYearlyReport();
    
    /**
     * 获取所有统计数据
     * @return 包含所有统计数据的Map对象
     */
    Map<String, Object> getAllStatistics();
}