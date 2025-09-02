package com.cjh.watching.watchback.config;

import com.cjh.watching.watchback.dto.MovieQuery;
import com.cjh.watching.watchback.service.MovieService;
import com.cjh.watching.watchback.utils.PageRequest;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 缓存预热任务组件
 * 在应用启动时预热关键接口的缓存，提升首次访问性能
 * 
 * @author Cjh
 * @date 2025/8/30
 */
@Component
public class CacheWarmupTask implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CacheWarmupTask.class);
    
    @Resource
    private MovieService movieService;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始执行缓存预热任务...");
        
        // 预热getAllData接口的缓存，使用默认参数：pageNum=1, pageSize=20
        try {
            // 创建默认分页参数
            PageRequest defaultPage = new PageRequest(1, 20);
            // 创建空查询条件
            MovieQuery emptyQuery = new MovieQuery();
            
            log.info("正在预热getAllData接口缓存，参数: pageNum={}, pageSize={}", defaultPage.getPageNum(), defaultPage.getPageSize());
            
            // 调用接口预热缓存
            // 注意：这里不需要实际处理返回结果，只是为了触发缓存
            movieService.getAllData(defaultPage, emptyQuery);
            
            log.info("getAllData接口缓存预热完成");
        } catch (Exception e) {
            log.error("getAllData接口缓存预热失败: {}", e.getMessage());
            // 预热失败不应影响应用启动，记录日志后继续
        }
        
        log.info("缓存预热任务执行完成");
    }
}