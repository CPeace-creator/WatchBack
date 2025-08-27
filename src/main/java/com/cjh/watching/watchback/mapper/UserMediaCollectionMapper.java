package com.cjh.watching.watchback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjh.watching.watchback.dto.MovieDto;
import com.cjh.watching.watchback.entity.Movie;
import com.cjh.watching.watchback.entity.UserMediaCollection;

import java.util.List;

/**
 * - @author Cjh。
 * - @date 2025/8/26 21:38。
 **/
public interface UserMediaCollectionMapper extends BaseMapper<UserMediaCollection> {
    List<MovieDto> getByRecent(String userId);

}