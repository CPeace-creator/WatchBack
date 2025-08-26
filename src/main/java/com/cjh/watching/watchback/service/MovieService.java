package com.cjh.watching.watchback.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cjh.watching.watchback.entity.Movie;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * - @author Cjh。
 * - @date 2025/8/14 13:51。
 **/
public interface MovieService extends IService<Movie> {
    List<Movie> getByRecent();

    SaResult importMovie(MultipartFile file);
}
