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
}
