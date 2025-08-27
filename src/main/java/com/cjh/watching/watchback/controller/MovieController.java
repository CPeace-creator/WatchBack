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
    @SaCheckLogin
    @RequestMapping("/getByRecent")
    public List<Movie> getByRecent() {
        return movieService.getByRecent();
    }

    @SaCheckLogin
    @PostMapping(value = "/getAllData")
    public IPage<MovieDto> getAllData(PageRequest page) {
        return movieService.getAllData(page);
    }

    @SaCheckLogin
    @PostMapping("/importMovie")
    public SaResult importMovie(@RequestParam("file") MultipartFile file) {
        return movieService.importMovie(file);
    }

    @SaCheckLogin
    @PostMapping(value = "/searchMovie")
    public List<MovieDto> searchMovie(String search) {
        return movieService.searchMovie(search);
    }
}
