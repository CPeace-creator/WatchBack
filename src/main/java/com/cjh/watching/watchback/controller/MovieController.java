package com.cjh.watching.watchback.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cjh.watching.watchback.dto.*;
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
    public IPage<MovieDto> getAllData(PageRequest page,@RequestBody MovieQuery query) {
        return movieService.getAllData(page,query);
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

    /**
     * 获取所有观看的数据 状态过滤
     * @return
     */
    @SaCheckLogin
    @PostMapping("/getByAllData")
    public SaResult getByAllData(PageRequest page,@RequestBody MovieQuery movieQuery) {
        return SaResult.ok().setData(movieService.getByAllData(page,movieQuery));
    }

    /**
     * 统计数据
     */
    @SaCheckLogin
    @PostMapping("/getUserStatistics")
    public SaResult getUserStatistics() {
        return movieService.getUserStatistics();
    }
    
    /**
     * 处理用户选择的模糊匹配项
     * @param request 模糊匹配确认请求
     * @return 处理结果
     */
    @SaCheckLogin
    @PostMapping("/confirmFuzzyMatches")
    public SaResult confirmFuzzyMatches(@RequestBody FuzzyMatchRequest request) {
        return movieService.confirmFuzzyMatches(StpUtil.getLoginIdAsLong(), request.getSelectedMovies(), request.getSelectedTVShows());
    }
    
    /**
     * 批量添加用户媒体收藏
     * @param request 批量添加请求
     * @return 处理结果
     */
    @SaCheckLogin
    @PostMapping("/batchAddMediaCollection")
    public SaResult batchAddMediaCollection(@RequestBody BatchAddMediaRequest request) {
        return movieService.batchAddMediaCollection(request.getMediaIds(), request.getStatus(), request.getMediaType());
    }
    
    /**
     * 管理用户媒体收藏状态（添加/取消标识）
     * @param request 收藏状态管理请求
     * @return 处理结果
     */
    @SaCheckLogin
    @PostMapping("/manageCollectionStatus")
    public SaResult manageCollectionStatus(@RequestBody CollectionStatusRequest request) {
        return movieService.manageCollectionStatus(request.getMediaId(), request.getMediaType(), 
                request.getStatus(), request.getIsAdd());
    }

    /**
     * 通过Python脚本搜索电影/电视剧
     * @param request 搜索请求参数
     * @return 搜索结果
     */
    @PostMapping("/searchByPythonScript")
    public SaResult searchByPythonScript(@RequestBody MovieSearchRequest request) {
        return movieService.searchByPythonScript(request.getTitle(), request.getMediaType());
    }
    
    /**
     * 根据Python脚本返回的搜索结果自动保存电影/电视剧并建立用户关系
     * @param searchResultDto Python脚本返回的搜索结果
     * @return 处理结果
     */
    @PostMapping("/autoSaveFromPythonResult")
    public SaResult autoSaveFromPythonResult(@RequestBody PythonSearchResultDto searchResultDto) {
        return movieService.autoSaveFromPythonResult(searchResultDto);
    }

    /**
     * 批量保存电视剧数据
     * @param request 包含page、results和total_pages等字段的JSON参数
     * @return 处理结果
     */
    @PostMapping("/batchSaveTVShows")
    public SaResult batchSaveTVShows(@RequestBody TVShowBatchRequest request) {
        return movieService.batchSaveTVShows(request);
    }
    
    /**
     * 批量保存电影数据
     * @param request 包含page、results和total_pages等字段的JSON参数
     * @return 处理结果
     */
    @PostMapping("/batchSaveMovies")
    public SaResult batchSaveMovies(@RequestBody MovieBatchRequest request) {
        return movieService.batchSaveMovies(request);
    }
}
