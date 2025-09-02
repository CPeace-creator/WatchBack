package com.cjh.watching.watchback.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.cjh.watching.watchback.mapper.StatisticsMapper;
import com.cjh.watching.watchback.mapper.UserMediaCollectionMapper;
import com.cjh.watching.watchback.service.StatisticsService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 统计服务实现类
 * 实现各类统计数据获取方法
 * 
 * @author Cjh
 * @date 2025/8/29
 */
@Service
@CacheConfig(cacheNames = "statistics")
public class StatisticsServiceImpl implements StatisticsService {
    
    @Resource
    private StatisticsMapper statisticsMapper;
    
    @Resource
    private UserMediaCollectionMapper userMediaCollectionMapper;
    
    @Override
    @Cacheable(key = "'monthly_trend_' + #root.target.getLoginUserId()")
    public Map<String, Object> getMonthlyTrend() {
        String userId = StpUtil.getLoginIdAsString();
        List<Map<String, Object>> data = statisticsMapper.getMonthlyTrend(userId);
        
        // 处理数据以适应ECharts折线图
        List<String> months = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        
        // 填充最近6个月的数据，确保每个月都有数据点
        Calendar calendar = Calendar.getInstance();
        List<String> expectedMonths = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            Calendar temp = (Calendar) calendar.clone();
            temp.add(Calendar.MONTH, -i);
            expectedMonths.add(String.format("%d-%02d", temp.get(Calendar.YEAR), temp.get(Calendar.MONTH) + 1));
        }
        
        // 创建月份到计数的映射
        Map<String, Integer> monthCountMap = new HashMap<>();
        for (Map<String, Object> item : data) {
            String month = (String) item.get("month");
            Integer count = ((Number) item.get("count")).intValue();
            monthCountMap.put(month, count);
        }
        
        // 按顺序添加数据，确保每个月都有数据
        for (String month : expectedMonths) {
            months.add(month);
            counts.add(monthCountMap.getOrDefault(month, 0));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("months", months);
        result.put("counts", counts);
        
        return result;
    }
    
    @Override
    @Cacheable(key = "'genre_distribution_' + #root.target.getLoginUserId()")
    public Map<String, Object> getGenreDistribution() {
        String userId = StpUtil.getLoginIdAsString();
        List<Map<String, Object>> data = statisticsMapper.getGenreDistribution(userId);
        
        // 处理数据以适应ECharts饼图
        List<String> genres = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        List<Double> percentages = new ArrayList<>();
        
        for (Map<String, Object> item : data) {
            genres.add((String) item.get("genre_name"));
            counts.add(((Number) item.get("count")).intValue());
            percentages.add(((Number) item.get("percentage")).doubleValue());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("genres", genres);
        result.put("counts", counts);
        result.put("percentages", percentages);
        
        return result;
    }
    
    @Override
    @Cacheable(key = "'rating_distribution_' + #root.target.getLoginUserId()")
    public Map<String, Object> getRatingDistribution() {
        String userId = StpUtil.getLoginIdAsString();
        List<Map<String, Object>> data = statisticsMapper.getRatingDistribution(userId);
        
        // 处理数据以适应ECharts柱状图
        List<String> ratingRanges = Arrays.asList("0-1", "1-2", "2-3", "3-4", "4-5", "5-6", "6-7", "7-8", "8-9", "9-10");
        List<Integer> counts = new ArrayList<>(Collections.nCopies(ratingRanges.size(), 0));
        
        // 创建评分范围到索引的映射
        Map<String, Integer> rangeIndexMap = new HashMap<>();
        for (int i = 0; i < ratingRanges.size(); i++) {
            rangeIndexMap.put(ratingRanges.get(i), i);
        }
        
        // 填充实际数据
        for (Map<String, Object> item : data) {
            String range = (String) item.get("rating_range");
            Integer count = ((Number) item.get("count")).intValue();
            if (rangeIndexMap.containsKey(range)) {
                counts.set(rangeIndexMap.get(range), count);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("ratingRanges", ratingRanges);
        result.put("counts", counts);
        
        return result;
    }
    
    @Override
    @Cacheable(key = "'yearly_report_' + #root.target.getLoginUserId()")
    public Map<String, Object> getYearlyReport() {
        String userId = StpUtil.getLoginIdAsString();
        Map<String, Object> data = statisticsMapper.getYearlyReport(userId);
        
        // 处理年度报告数据
        Map<String, Object> result = new HashMap<>();
        result.put("totalWatched", data.getOrDefault("totalWatched", 0));
        result.put("averageRating", data.getOrDefault("averageRating", 0.0));
        result.put("movieCount", data.getOrDefault("movieCount", 0));
        result.put("tvShowCount", data.getOrDefault("tvShowCount", 0));

        // 处理最喜欢的类型
        Object favoriteGenre = data.get("favoriteGenre");
        result.put("favoriteGenre", favoriteGenre);
        
        return result;
    }
    
    @Override
    @Cacheable(key = "'all_statistics_' + #root.target.getLoginUserId()")
    public Map<String, Object> getAllStatistics() {
        // 一次性获取所有统计数据
        Map<String, Object> result = new HashMap<>();
        result.put("monthlyTrend", getMonthlyTrend());
        result.put("genreDistribution", getGenreDistribution());
        result.put("ratingDistribution", getRatingDistribution());
        result.put("yearlyReport", getYearlyReport());
        
        return result;
    }
    
    /**
     * 获取当前登录用户ID，用于缓存key生成
     * 这个方法会被缓存表达式调用
     */
    public String getLoginUserId() {
        return StpUtil.getLoginIdAsString();
    }
}