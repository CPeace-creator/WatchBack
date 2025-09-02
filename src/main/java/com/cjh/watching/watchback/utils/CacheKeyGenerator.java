package com.cjh.watching.watchback.utils;

import com.cjh.watching.watchback.dto.MovieQuery;
import java.util.Objects;
import java.util.UUID;

/**
 * 缓存键生成器类
 * 用于生成统一格式的缓存键，确保缓存的准确性和一致性
 */
public class CacheKeyGenerator {

    /**
     * 生成getAllData方法的缓存键
     * @param page 分页参数
     * @param query 查询条件
     * @return 缓存键字符串
     */
    public static String generateKey(PageRequest page, MovieQuery query) {
        StringBuilder keyBuilder = new StringBuilder("allData_");
        
        // 添加分页信息
        keyBuilder.append(page.getPageNum()).append("_")
                .append(page.getPageSize());
        
        // 添加查询条件信息
        if (query != null) {
            // 处理查询关键词
            if (query.getQuery() != null && !query.getQuery().trim().isEmpty()) {
                keyBuilder.append("_query_")
                        .append(query.getQuery().trim().hashCode());
            }
            
            // 处理状态条件
            if (query.getStatus() != null) {
                keyBuilder.append("_status_")
                        .append(query.getStatus());
            }
            
            // 处理其他可能的查询条件（如果需要）
            // 例如totalWatched, monthlyWatched等
        }
        
        return keyBuilder.toString();
    }
}