package com.cjh.watching.watchback.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cjh.watching.watchback.dto.MovieDto;
import com.cjh.watching.watchback.dto.MovieQuery;
import com.cjh.watching.watchback.entity.Movie;
import com.cjh.watching.watchback.utils.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * - @author Cjh。
 * - @date 2025/8/14 13:51。
 **/
public interface MovieService extends IService<Movie> {
    List<MovieDto> getByRecent();
    IPage<MovieDto> getByAllData(PageRequest page, MovieQuery movieQuery);

    SaResult importMovie(MultipartFile file);

    IPage<MovieDto> getAllData(PageRequest page,MovieQuery query);

    SaResult searchMovie(String search);

    SaResult movieDetail(Long query, Integer type);

    SaResult saveMovie(MovieDto movie);
}
