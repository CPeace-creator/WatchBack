package com.cjh.watching.watchback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjh.watching.watchback.dto.MovieDto;
import com.cjh.watching.watchback.dto.MovieQuery;
import com.cjh.watching.watchback.entity.Movie;
import com.cjh.watching.watchback.entity.UserMediaCollection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * - @author Cjh。
 * - @date 2025/8/26 21:38。
 **/
public interface UserMediaCollectionMapper extends BaseMapper<UserMediaCollection> {
    List<MovieDto> getByRecent(String userId);
    IPage<MovieDto> getByAllData(Page<MovieDto> page, @Param("userId") String userId, @Param("query") MovieQuery movieQuery);

}