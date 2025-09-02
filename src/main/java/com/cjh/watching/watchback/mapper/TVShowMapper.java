package com.cjh.watching.watchback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjh.watching.watchback.entity.TVShow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 电视剧Mapper接口
 * - @author Cjh。
 * - @date 2025/8/26 22:45。
 **/
@Mapper
public interface TVShowMapper extends BaseMapper<TVShow> {
    // BaseMapper已经提供了基础的CRUD操作
    // 如有特殊查询需求，可以在这里添加自定义方法
    
    /**
     * 精确匹配电视剧标题
     * @param titles 标题列表
     * @return 匹配的电视剧列表
     */
    List<TVShow> findTVShowsByExactTitles(@Param("titles") List<String> titles);
    
    /**
     * 模糊匹配电视剧标题
     * @param title 单个标题
     * @return 匹配的电视剧列表
     */
    List<TVShow> findTVShowsByFuzzyTitle(@Param("title") String title);
    
    /**
     * 查询用户对电视剧的收藏状态
     * @param userId 用户ID
     * @param mediaId 媒体ID
     * @return 收藏状态列表
     */
    List<Integer> getUserTVShowCollectionStatuses(@Param("userId") Long userId, @Param("mediaId") Long mediaId);
}