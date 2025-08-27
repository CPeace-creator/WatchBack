package com.cjh.watching.watchback.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cjh.watching.watchback.dto.MovieDto;
import com.cjh.watching.watchback.entity.Movie;
import com.cjh.watching.watchback.service.MovieService;
import com.cjh.watching.watchback.utils.PageRequest;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * - @author Cjh。
 * - @date 2025/8/15 11:04。
 **/
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/movie")
public class MovieController {
    @Resource
    private MovieService movieService;

    /**
     * 获取最近7天观看的影片
     * @return
     */
    @SaCheckLogin
    @RequestMapping("/getByRecent")
    public List<MovieDto> getByRecent() {
        return movieService.getByRecent();
    }

    /**
     * 获取所有的影片信息
     * @param page
     * @return
     */
    @SaCheckLogin
    @PostMapping(value = "/getAllData")
    public IPage<MovieDto> getAllData(PageRequest page) {
        return movieService.getAllData(page);
    }

    /**
     * 根据execl的文件导入影片信息
     * @param file
     * @return
     */
    @SaCheckLogin
    @PostMapping("/importMovie")
    public SaResult importMovie(@RequestParam("file") MultipartFile file) {
        return movieService.importMovie(file);
    }

    /**
     * 在线搜索影片信息
     * @param search
     * @return
     */
    @SaCheckLogin
    @PostMapping(value = "/searchMovie")
    public SaResult searchMovie(String search) {
        return movieService.searchMovie(search);
    }

    /**
     * 影片详情
     * @param query id
     * @param type 1 电影 2 电视剧
     * @return
     */
    @SaCheckLogin
    @PostMapping(value = "/movieDetail")
    public SaResult movieDetail(Long query,Integer type) {
        return movieService.movieDetail(query,type);
    }

    @SaCheckLogin
    @PostMapping(value = "/saveMovie")
    public SaResult saveMovie(@RequestBody  MovieDto movie) {
        return movieService.saveMovie(movie);
    }
}
