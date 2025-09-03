package com.cjh.watching.watchback.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cjh.watching.watchback.dto.MovieDto;
import com.cjh.watching.watchback.dto.MovieQuery;
import com.cjh.watching.watchback.dto.PythonSearchResultDto;
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

    SaResult getUserStatistics();
    
    /**
     * 处理用户选择的模糊匹配项
     * @param userId 用户ID
     * @param selectedMovies 用户选择的电影ID列表
     * @param selectedTVShows 用户选择的电视剧ID列表
     * @return 处理结果
     */
    SaResult confirmFuzzyMatches(Long userId, List<Long> selectedMovies, List<Long> selectedTVShows);
    
    /**
     * 批量添加用户媒体收藏
     * @param mediaIds 媒体ID列表
     * @param status 状态 1-已收藏, 2-已观看, 3-想看
     * @param mediaType 媒体类型 1-电影, 2-电视剧
     * @return 处理结果
     */
    SaResult batchAddMediaCollection(List<Long> mediaIds, Integer status, Integer mediaType);
    
    /**
     * 管理用户媒体收藏状态（添加/取消标识）
     * @param mediaId 媒体ID
     * @param mediaType 媒体类型 1-电影, 2-电视剧
     * @param status 状态 1-已收藏, 2-已观看, 3-想看
     * @param isAdd 操作类型 true-添加标识, false-取消标识
     * @return 处理结果
     */
    SaResult manageCollectionStatus(Long mediaId, Integer mediaType, Integer status, Boolean isAdd);

    /**
     * 通过Python脚本搜索电影/电视剧
     * @param title 搜索的标题
     * @param mediaType 媒体类型：1表示电视剧，2表示电影
     * @return 搜索结果
     */
    SaResult searchByPythonScript(String title, Integer mediaType);
    
    /**
     * 根据Python脚本返回的搜索结果自动保存电影/电视剧并建立用户关系
     * @param searchResultDto Python脚本返回的搜索结果
     * @return 处理结果
     */
    SaResult autoSaveFromPythonResult(PythonSearchResultDto searchResultDto);

}
