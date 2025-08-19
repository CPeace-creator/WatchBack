package com.cjh.watching.watchback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjh.watching.watchback.entity.Movie;
import com.cjh.watching.watchback.mapper.MovieMapper;
import com.cjh.watching.watchback.service.MovieService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * - @author Cjh。
 * - @date 2025/8/14 13:53。
 **/
@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, Movie> implements MovieService {


    @Override
    public List<Movie> getMovieRecent() {
        return this.list(new LambdaQueryWrapper<Movie>()
                .gt(Movie::getCreatedTime, LocalDateTime.now().minusDays(7)));
    }

}
