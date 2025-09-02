package com.cjh.watching.watchback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjh.watching.watchback.dto.MovieDto;
import com.cjh.watching.watchback.dto.MovieQuery;
import com.cjh.watching.watchback.entity.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MovieMapper extends BaseMapper<Movie> {
    IPage<MovieDto> getAllData(Page<MovieDto> page,@Param("query") MovieQuery query);

    List<MovieDto> getAllDataBySearch(@Param("query") String search);
    
    /**
     * 精确匹配电影标题
     * @param titles 标题列表
     * @return 匹配的电影列表
     */
    List<Movie> findMoviesByExactTitles(@Param("titles") List<String> titles);
    
    /**
     * 模糊匹配电影标题
     * @param title 单个标题
     * @return 匹配的电影列表
     */
    List<Movie> findMoviesByFuzzyTitle(@Param("title") String title);
    
    /**
     * 查询用户对电影的收藏状态
     * @param userId 用户ID
     * @param mediaId 媒体ID
     * @return 收藏状态列表
     */
    List<Integer> getUserMovieCollectionStatuses(@Param("userId") Long userId, @Param("mediaId") Long mediaId);
}
