package com.cjh.watching.watchback.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 统计数据Mapper接口
 * 定义各类统计数据查询方法
 * 
 * @author Cjh
 * @date 2025/8/29
 */
@Mapper
public interface StatisticsMapper {
    
    /**
     * 获取月度观影趋势数据
     * @param userId 用户ID
     * @return 包含月度观影数据的列表
     */
    List<Map<String, Object>> getMonthlyTrend(@Param("userId") String userId);
    
    /**
     * 获取类型分布数据
     * @param userId 用户ID
     * @return 包含类型分布数据的列表
     */
    List<Map<String, Object>> getGenreDistribution(@Param("userId") String userId);
    
    /**
     * 获取评分分布数据
     * @param userId 用户ID
     * @return 包含评分分布数据的列表
     */
    List<Map<String, Object>> getRatingDistribution(@Param("userId") String userId);
    
    /**
     * 获取年度报告数据
     * @param userId 用户ID
     * @return 包含年度报告数据的Map
     */
    Map<String, Object> getYearlyReport(@Param("userId") String userId);
    
    /**
     * 获取收藏的电影总数
     * @param userId 用户ID
     * @return 收藏的电影总数
     */
    int getMovieCount(@Param("userId") String userId);
    
    /**
     * 获取收藏的电视剧总数
     * @param userId 用户ID
     * @return 收藏的电视剧总数
     */
    int getTvShowCount(@Param("userId") String userId);
}