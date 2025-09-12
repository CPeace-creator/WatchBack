package com.cjh.watching.watchback.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjh.watching.watchback.dto.MovieBatchRequest;
import com.cjh.watching.watchback.dto.MovieDto;
import com.cjh.watching.watchback.dto.MovieQuery;
import com.cjh.watching.watchback.dto.PythonSearchResultDto;
import com.cjh.watching.watchback.dto.TVShowBatchRequest;
import com.cjh.watching.watchback.entity.Movie;
import com.cjh.watching.watchback.entity.TVShow;
import com.cjh.watching.watchback.entity.UserMediaCollection;
import com.cjh.watching.watchback.enums.MovieStatusEnum;
import com.cjh.watching.watchback.mapper.MovieMapper;
import com.cjh.watching.watchback.mapper.TVShowMapper;
import com.cjh.watching.watchback.mapper.UserMediaCollectionMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cjh.watching.watchback.service.MovieService;
import com.cjh.watching.watchback.service.UserMediaCollectionService;
import com.cjh.watching.watchback.utils.ImportResult;
import com.cjh.watching.watchback.utils.PageRequest;
import com.cjh.watching.watchback.utils.PythonScriptExecutor;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/** 
 * - @author Cjh。
 * - @date 2025/8/14 13:53。
 **/
@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, Movie> implements MovieService {
    
    private static final Logger log = LoggerFactory.getLogger(MovieServiceImpl.class);

    @Resource
    private UserMediaCollectionService userMediaCollectionService;

    @Resource
    private UserMediaCollectionMapper userMediaCollectionMapper;
    
    @Resource
    private TVShowMapper tvShowMapper;

    @Autowired
    private PythonScriptExecutor pythonScriptExecutor;


    @Override
    public List<MovieDto> getByRecent() {

        return userMediaCollectionMapper.getByRecent(StpUtil.getLoginIdAsString());
    }

    @Override
    public IPage<MovieDto> getByAllData(PageRequest page, MovieQuery movieQuery) {
        Page<MovieDto> page1=new Page<>(page.getPageNum(),page.getPageSize());
        IPage<MovieDto> byAllData = userMediaCollectionMapper.getByAllData(page1,StpUtil.getLoginIdAsString(), movieQuery);
        return byAllData;
    }

    @Override
    public SaResult importMovie(MultipartFile file) {
        try {
            // 验证文件类型
            if (!file.getOriginalFilename().endsWith(".xlsx") &&
                    !file.getOriginalFilename().endsWith(".xls")) {
                return SaResult.error("仅支持Excel文件(.xlsx/.xls)");
            }
            
            // 获取当前登录用户ID
            Long userId = Long.valueOf(StpUtil.getLoginIdAsString());
            
            // 获取导入的总数
            int importedCount = getImportedCount(file);
            
            // 读取文件中的所有标题
            List<String> allTitles = read(file);
            
            // 创建导入结果对象
            ImportResult result = new ImportResult();
            result.setTotalCount(importedCount);
            
            // 分别处理电影和电视剧
            try{
                handleMoviesImport(allTitles, userId, result);
                handleTVShowsImport(allTitles, userId, result);
            }catch (Exception e){
                return SaResult.error("导入失败: " + e.getMessage());
            }
            // 优化未找到列表：移除那些在另一类别中已找到的标题
            // 从未找到的电影列表中移除那些在电视剧中已找到的标题
            List<String> refinedNotFoundMovies = result.getNotFoundMovieTitles().stream()
                    .filter(title -> !isTitleFoundInTVShows(title))
                    .collect(Collectors.toList());
            result.setNotFoundMovieTitles(refinedNotFoundMovies);
            
            // 从未找到的电视剧列表中移除那些在电影中已找到的标题
            List<String> refinedNotFoundTVShows = result.getNotFoundTVShowTitles().stream()
                    .filter(title -> !isTitleFoundInMovies(title))
                    .collect(Collectors.toList());
            result.setNotFoundTVShowTitles(refinedNotFoundTVShows);
            
            // 记录导入结果日志
            log.info("用户 {} 导入媒体结果: 总数={}, 成功电影数={}, 成功电视剧数={}, 未找到电影数={}, 未找到电视剧数={}",
                    userId, result.getTotalCount(), result.getSuccessMovieCount(), result.getSuccessTVShowCount(),
                    result.getNotFoundMovieTitles().size(), result.getNotFoundTVShowTitles().size());
            
            // 返回结果
            return SaResult.ok()
                    .setData(result);
        } catch (Exception e) {
            return SaResult.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 处理电影导入
     */
    private void handleMoviesImport(List<String> titles, Long userId, ImportResult result) {
        try {
            // 1. 先进行精确匹配
            List<Movie> exactMatches = baseMapper.findMoviesByExactTitles(titles);
            
            // 2. 提取精确匹配的标题
            Set<String> exactMatchTitles = new HashSet<>();
            Map<String, Movie> titleToMovieMap = new HashMap<>();
            for (Movie movie : exactMatches) {
                exactMatchTitles.add(movie.getTitle());
                exactMatchTitles.add(movie.getOriginalTitle());
                titleToMovieMap.put(movie.getTitle(), movie);
                if (movie.getOriginalTitle() != null) {
                    titleToMovieMap.put(movie.getOriginalTitle(), movie);
                }
            }
            
            // 3. 找出未精确匹配的标题，进行模糊匹配
            List<String> unmatched = titles.stream()
                    .filter(title -> !exactMatchTitles.contains(title))
                    .collect(Collectors.toList());
            
            List<String> notFoundMovies = new ArrayList<>();
            for (String title : unmatched) {
                List<Movie> fuzzyMatches = baseMapper.findMoviesByFuzzyTitle(title);
                if (!fuzzyMatches.isEmpty()) {
                    // 有模糊匹配结果，添加到模糊匹配列表供用户选择
                    ImportResult.MovieFuzzyMatch fuzzyMatch = new ImportResult.MovieFuzzyMatch();
                    fuzzyMatch.setInputTitle(title);
                    fuzzyMatch.setMatchedMovies(fuzzyMatches);
                    result.getMovieFuzzyMatches().add(fuzzyMatch);
                } else {
                    // 无任何匹配结果
                    notFoundMovies.add(title);
                }
            }
            
            // 4. 批量添加精确匹配的用户收藏记录
            List<UserMediaCollection> collections = new ArrayList<>();
            for (String title : titles) {
                if (titleToMovieMap.containsKey(title)) {
                    Movie movie = titleToMovieMap.get(title);
                    
                    // 检查是否已收藏
                    UserMediaCollection existingCollection = userMediaCollectionService.getOne(new LambdaQueryWrapper<UserMediaCollection>()
                            .eq(UserMediaCollection::getUserId, userId)
                            .eq(UserMediaCollection::getMediaType, 1) // 1-电影
                            .eq(UserMediaCollection::getMediaId, movie.getMovieId())
                    );
                    
                    if (existingCollection == null) {
                        UserMediaCollection collection = new UserMediaCollection();
                        collection.setUserId(userId);
                        collection.setMediaType(1); // 1-电影
                        collection.setMediaId(movie.getMovieId());
                        collection.setTitle(movie.getTitle());
                        collection.setTmdbId(movie.getTmdbId());
                        collection.setStatus(MovieStatusEnum.HASWATCHED.getValue());
                        collection.setCreatedTime(LocalDateTime.now());
                        collection.setUpdatedTime(LocalDateTime.now());
                        collections.add(collection);
                    }
                }
            }
            
            // 5. 批量保存精确匹配的收藏
            if (!collections.isEmpty()) {
                userMediaCollectionService.saveBatch(collections);
                result.setSuccessMovieCount(collections.size());
            }
            
            result.setNotFoundMovieTitles(notFoundMovies);
        } catch (Exception e) {
            log.error("处理电影导入失败", e);
            result.setMovieErrorMessage(e.getMessage());
            throw e;
        }
    }
    
    /**
     * 处理电视剧导入
     */
    private void handleTVShowsImport(List<String> titles, Long userId, ImportResult result) {
        try {
            // 1. 先进行精确匹配
            List<TVShow> exactMatches = tvShowMapper.findTVShowsByExactTitles(titles);
            
            // 2. 提取精确匹配的标题
            Set<String> exactMatchTitles = new HashSet<>();
            Map<String, TVShow> titleToTVShowMap = new HashMap<>();
            for (TVShow tvShow : exactMatches) {
                exactMatchTitles.add(tvShow.getName());
                exactMatchTitles.add(tvShow.getOriginalName());
                titleToTVShowMap.put(tvShow.getName(), tvShow);
                if (tvShow.getOriginalName() != null) {
                    titleToTVShowMap.put(tvShow.getOriginalName(), tvShow);
                }
            }
            
            // 3. 找出未精确匹配的标题，进行模糊匹配
            List<String> unmatched = titles.stream()
                    .filter(title -> !exactMatchTitles.contains(title))
                    .collect(Collectors.toList());
            
            List<String> notFoundTVShows = new ArrayList<>();
            for (String title : unmatched) {
                List<TVShow> fuzzyMatches = tvShowMapper.findTVShowsByFuzzyTitle(title);
                if (!fuzzyMatches.isEmpty()) {
                    // 有模糊匹配结果，添加到模糊匹配列表供用户选择
                    ImportResult.TVShowFuzzyMatch fuzzyMatch = new ImportResult.TVShowFuzzyMatch();
                    fuzzyMatch.setInputTitle(title);
                    fuzzyMatch.setMatchedTVShows(fuzzyMatches);
                    result.getTvShowFuzzyMatches().add(fuzzyMatch);
                } else {
                    // 无任何匹配结果
                    notFoundTVShows.add(title);
                }
            }
            
            // 4. 批量添加精确匹配的用户收藏记录
            List<UserMediaCollection> collections = new ArrayList<>();
            for (String title : titles) {
                if (titleToTVShowMap.containsKey(title)) {
                    TVShow tvShow = titleToTVShowMap.get(title);
                    
                    // 检查是否已收藏
                    UserMediaCollection existingCollection = userMediaCollectionService.getOne(new LambdaQueryWrapper<UserMediaCollection>()
                            .eq(UserMediaCollection::getUserId, userId)
                            .eq(UserMediaCollection::getMediaType, 2) // 2-电视剧
                            .eq(UserMediaCollection::getMediaId, tvShow.getShowId().longValue())
                    );
                    
                    if (existingCollection == null) {
                        UserMediaCollection collection = new UserMediaCollection();
                        collection.setUserId(userId);
                        collection.setMediaType(2); // 2-电视剧
                        collection.setMediaId(tvShow.getShowId().longValue());
                        collection.setTmdbId(tvShow.getTmdbId());
                        collection.setTitle(tvShow.getName());
                        collection.setStatus(MovieStatusEnum.HASWATCHED.getValue());
                        collection.setCreatedTime(LocalDateTime.now());
                        collection.setUpdatedTime(LocalDateTime.now());
                        collections.add(collection);
                    }
                }
            }
            
            // 5. 批量保存精确匹配的收藏
            if (!collections.isEmpty()) {
                userMediaCollectionService.saveBatch(collections);
                result.setSuccessTVShowCount(collections.size());
            }
            
            result.setNotFoundTVShowTitles(notFoundTVShows);
        } catch (Exception e) {
            log.error("处理电视剧导入失败", e);
            result.setTvShowErrorMessage(e.getMessage());
            throw e;
        }
    }


    /**
     * 使用EasyExcel读取Excel文件内容
     * @param file 上传的Excel文件
     * @return 二维List，外层是行，内层是单元格内容
     */
    private List<String> read(MultipartFile file) throws IOException {
        List<String> result = new ArrayList<>();

        EasyExcel.read(file.getInputStream())
                .sheet(0) // 读取第一个工作表
                .headRowNumber(0) // 从第1行开始读取
                .registerReadListener(new ReadListener<Map<Integer, String>>() {
                    @Override
                    public void invoke(Map<Integer, String> rowMap, AnalysisContext context) {
                        // 遍历当前行的所有单元格
                        rowMap.values().forEach(cellValue -> {
                            if (cellValue != null && !cellValue.trim().isEmpty()) {
                                result.add(cellValue.trim());
                            }
                        });
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                    }
                })
                .doRead();

        return result;
    }
    
    /**
     * 获取Excel中电影的总数
     */
    private int getImportedCount(MultipartFile file) throws IOException {
        return read(file).size();
    }
    
    /**
     * 检查标题是否在电影中找到
     */
    private boolean isTitleFoundInMovies(String title) {
        try {
            List<Movie> movies = baseMapper.selectList(new LambdaQueryWrapper<Movie>()
                    .eq(Movie::getTitle, title)
                    .or().eq(Movie::getOriginalTitle, title)
            );
            return !movies.isEmpty();
        } catch (Exception e) {
            log.error("检查标题是否在电影中找到失败", e);
            return false;
        }
    }
    
    /**
     * 检查标题是否在电视剧中找到
     */
    private boolean isTitleFoundInTVShows(String title) {
        try {
            List<TVShow> tvShows = tvShowMapper.selectList(new LambdaQueryWrapper<TVShow>()
                    .eq(TVShow::getName, title)
                    .or().eq(TVShow::getOriginalName, title)
            );
            return !tvShows.isEmpty();
        } catch (Exception e) {
            log.error("检查标题是否在电视剧中找到失败", e);
            return false;
        }
    }

    @Override
    @Cacheable(value = "allDataCache", key = "T(com.cjh.watching.watchback.utils.CacheKeyGenerator).generateKey(#page, #query)")
    public IPage<MovieDto> getAllData(PageRequest page,MovieQuery query) {
        Page<MovieDto> page1=new Page<>(page.getPageNum(),page.getPageSize());
        IPage<MovieDto> allData = baseMapper.getAllData(page1,query);

        // 手动计算总数 - 解决UNION ALL查询时total为0的问题
        // 分别查询movies和tv_shows表的记录数并相加
        Long moviesCount = 0L;
        Long tvShowsCount = 0L;

        if (query.getQuery() != null && !query.getQuery().trim().isEmpty()) {
            moviesCount = baseMapper.selectCount(new LambdaQueryWrapper<Movie>().like(Movie::getTitle, "%" + query.getQuery() + "%"));
            tvShowsCount = tvShowMapper.selectCount(new LambdaQueryWrapper<TVShow>().like(TVShow::getName, "%" + query.getQuery() + "%"));
        } else {
            moviesCount = baseMapper.selectCount(new LambdaQueryWrapper<Movie>());
            tvShowsCount = tvShowMapper.selectCount(new LambdaQueryWrapper<TVShow>());
        }

        int totalCount = Math.toIntExact(moviesCount + tvShowsCount);
        
        // 设置总数到返回的分页对象中
        allData.setTotal(totalCount);
        allData.setPages((totalCount + page.getPageSize() - 1) / page.getPageSize());
        
        return allData;
    }

    @Override
    public SaResult searchMovie(String search) {
        List<MovieDto> allDataBySearch = baseMapper.getAllDataBySearch(search);
        return SaResult.ok().setData(allDataBySearch);
    }

    @Override
    public SaResult movieDetail(Long query, Integer type) {
        MovieDto result = null;
        
        // 获取当前用户ID
        String currentUserId = null;
        try {
            currentUserId = StpUtil.getLoginIdAsString();
        } catch (Exception e) {
            // 如果未登录，设置为null，不影响媒体详情的获取
        }
        
        switch (type){
            case 1:
                Movie movies = baseMapper.selectOne(new LambdaQueryWrapper<Movie>().eq(Movie::getMovieId, query));
                if (movies != null) {
                    // 使用BeanUtil.copyProperties复制属性
                    MovieDto movieDtos = BeanUtil.copyProperties(movies, MovieDto.class);
                    movieDtos.setId(movies.getMovieId());
                    movieDtos.setMediaType(1);
                    
                    // 使用XML查询用户收藏状态
                    if (currentUserId != null) {
                        List<Integer> collectionStatuses = baseMapper.getUserMovieCollectionStatuses(
                                Long.valueOf(currentUserId), movies.getMovieId());
                        movieDtos.setCollectionStatuses(collectionStatuses);
                    } else {
                        movieDtos.setCollectionStatuses(new ArrayList<>());
                    }
                    
                    result = movieDtos;
                }
                break;
            case 2:
                TVShow shows = tvShowMapper.selectOne(new LambdaQueryWrapper<TVShow>().eq(TVShow::getShowId,query));
                if (shows != null) {
                    // 使用BeanUtil.copyProperties将TVShow转换为MovieDto
                    MovieDto showDtos = BeanUtil.copyProperties(shows, MovieDto.class);
                    showDtos.setId(shows.getShowId().longValue());
                    showDtos.setMediaType(2);
                    
                    // 使用XML查询用户收藏状态
                    if (currentUserId != null) {
                        List<Integer> collectionStatuses = tvShowMapper.getUserTVShowCollectionStatuses(
                                Long.valueOf(currentUserId), shows.getShowId().longValue());
                        showDtos.setCollectionStatuses(collectionStatuses);
                    } else {
                        showDtos.setCollectionStatuses(new ArrayList<>());
                    }
                    
                    result = showDtos;
                }
                break;
        }
        return SaResult.ok().setData(result);
    }

    @Override
    public SaResult saveMovie(MovieDto movie) {
        // 检查是否已经存在相同的记录
        String userId = StpUtil.getLoginIdAsString();
        UserMediaCollection existingCollection = userMediaCollectionMapper.selectOne(
                new LambdaQueryWrapper<UserMediaCollection>()
                        .eq(UserMediaCollection::getUserId, Long.valueOf(userId))
                        .eq(UserMediaCollection::getMediaType, movie.getMediaType())
                        .eq(UserMediaCollection::getMediaId, movie.getId())
        );
        if (existingCollection != null) {
            // 如果已经存在，返回提示信息
            return SaResult.error("该影片已经看过了");
        }
        UserMediaCollection collection = new UserMediaCollection();
        collection.setUserId(Long.valueOf(userId));
        collection.setCreatedTime(LocalDateTime.now());
        collection.setStatus(MovieStatusEnum.HASWATCHED.getValue());
        collection.setMediaType(movie.getMediaType());
        collection.setMediaId(movie.getId());
        collection.setTmdbId(movie.getTmdbId());
        collection.setTitle(movie.getTitle());
        userMediaCollectionMapper.insert(collection);
        return SaResult.ok("导入影片成功!");

    }

    @Override
    public SaResult getUserStatistics() {
        MovieQuery userStatistics = userMediaCollectionMapper.getUserStatistics(StpUtil.getLoginIdAsString(),2);
        return SaResult.ok().setData(userStatistics);
    }
    
    @Override
    public SaResult confirmFuzzyMatches(Long userId, List<Long> selectedMovies, List<Long> selectedTVShows) {
        try {
            List<UserMediaCollection> collections = new ArrayList<>();
            int successCount = 0;
            
            // 处理电影选择
            if (selectedMovies != null && !selectedMovies.isEmpty()) {
                List<Movie> movies = baseMapper.selectBatchIds(selectedMovies);
                for (Movie movie : movies) {
                    // 检查是否已收藏
                    UserMediaCollection existingCollection = userMediaCollectionService.getOne(new LambdaQueryWrapper<UserMediaCollection>()
                            .eq(UserMediaCollection::getUserId, userId)
                            .eq(UserMediaCollection::getMediaType, 1) // 1-电影
                            .eq(UserMediaCollection::getMediaId, movie.getMovieId())
                    );
                    
                    if (existingCollection == null) {
                        UserMediaCollection collection = new UserMediaCollection();
                        collection.setUserId(userId);
                        collection.setMediaType(1); // 1-电影
                        collection.setMediaId(movie.getMovieId());
                        collection.setTitle(movie.getTitle());
                        Object loginId = StpUtil.getLoginId();
                        collection.setUserId(Long.valueOf(loginId.toString()));
                        collection.setTmdbId(movie.getTmdbId());
                        collection.setStatus(MovieStatusEnum.HASWATCHED.getValue());
                        collection.setCreatedTime(LocalDateTime.now());
                        collection.setUpdatedTime(LocalDateTime.now());
                        collections.add(collection);
                        successCount++;
                    }else{
                        return SaResult.error("该影片已经看过了");
                    }
                }
            }
            
            // 处理电视剧选择
            if (selectedTVShows != null && !selectedTVShows.isEmpty()) {
                // 将Long列表转换为Integer列表，用于TVShow的ID类型
                List<Integer> tvShowIds = selectedTVShows.stream()
                        .map(Long::intValue)
                        .collect(Collectors.toList());
                List<TVShow> tvShows = tvShowMapper.selectBatchIds(tvShowIds);
                for (TVShow tvShow : tvShows) {
                    // 检查是否已收藏
                    UserMediaCollection existingCollection = userMediaCollectionService.getOne(new LambdaQueryWrapper<UserMediaCollection>()
                            .eq(UserMediaCollection::getUserId, userId)
                            .eq(UserMediaCollection::getMediaType, 2) // 2-电视剧
                            .eq(UserMediaCollection::getMediaId, tvShow.getShowId().longValue())
                    );
                    
                    if (existingCollection == null) {
                        UserMediaCollection collection = new UserMediaCollection();
                        collection.setUserId(userId);
                        collection.setMediaType(2); // 2-电视剧
                        collection.setMediaId(tvShow.getShowId().longValue());
                        collection.setTmdbId(tvShow.getTmdbId());
                        collection.setTitle(tvShow.getName());
                        Object loginId = StpUtil.getLoginId();
                        collection.setUserId(Long.valueOf(loginId.toString()));
                        collection.setStatus(MovieStatusEnum.HASWATCHED.getValue());
                        collection.setCreatedTime(LocalDateTime.now());
                        collection.setUpdatedTime(LocalDateTime.now());
                        collections.add(collection);
                        successCount++;
                    }else{
                        return SaResult.error("该影片已经看过了");
                    }
                }
            }
            
            // 批量保存
            if (!collections.isEmpty()) {
                userMediaCollectionService.saveBatch(collections);
            }
            
            return SaResult.ok("成功添加 " + successCount + " 个媒体到收藏列表");
        } catch (Exception e) {
            log.error("处理用户选择失败", e);
            return SaResult.error("处理失败: " + e.getMessage());
        }
    }
    
    @Override
    public SaResult batchAddMediaCollection(List<Long> mediaIds, Integer status, Integer mediaType) {
        try {
            String currentUserId = StpUtil.getLoginIdAsString();
            Long userId = Long.valueOf(currentUserId);
            
            if (mediaIds == null || mediaIds.isEmpty()) {
                return SaResult.error("媒体ID列表不能为空");
            }
            
            if (status == null || (status != 1 && status != 2 && status != 3)) {
                return SaResult.error("状态参数错误，必须为 1-已收藏, 2-已观看, 3-想看");
            }
            
            if (mediaType == null || (mediaType != 1 && mediaType != 2)) {
                return SaResult.error("媒体类型参数错误，必须为 1-电影, 2-电视剧");
            }
            
            List<UserMediaCollection> collections = new ArrayList<>();
            int successCount = 0;
            int duplicateCount = 0;
            
            for (Long mediaId : mediaIds) {
                // 检查是否已存在
                UserMediaCollection existingCollection = userMediaCollectionService.getOne(new LambdaQueryWrapper<UserMediaCollection>()
                        .eq(UserMediaCollection::getUserId, userId)
                        .eq(UserMediaCollection::getMediaType, mediaType)
                        .eq(UserMediaCollection::getMediaId, mediaId)
                );
                
                if (existingCollection != null) {
                    duplicateCount++;
                    continue;
                }
                
                // 获取媒体信息
                String title = "";
                Integer tmdbId = null;
                
                if (mediaType == 1) {
                    // 电影
                    Movie movie = baseMapper.selectById(mediaId);
                    if (movie != null) {
                        title = movie.getTitle();
                        tmdbId = movie.getTmdbId();
                    } else {
                        log.warn("未找到ID为 {} 的电影", mediaId);
                        continue;
                    }
                } else {
                    // 电视剧
                    TVShow tvShow = tvShowMapper.selectById(mediaId.intValue());
                    if (tvShow != null) {
                        title = tvShow.getName();
                        tmdbId = tvShow.getTmdbId();
                    } else {
                        log.warn("未找到ID为 {} 的电视剧", mediaId);
                        continue;
                    }
                }
                
                // 创建收藏记录
                UserMediaCollection collection = new UserMediaCollection();
                collection.setUserId(userId);
                collection.setMediaType(mediaType);
                collection.setMediaId(mediaId);
                collection.setTmdbId(tmdbId);
                collection.setTitle(title);
                collection.setStatus(status);
                collection.setCreatedTime(LocalDateTime.now());
                collection.setUpdatedTime(LocalDateTime.now());
                
                collections.add(collection);
                successCount++;
            }
            
            // 批量保存
            if (!collections.isEmpty()) {
                userMediaCollectionService.saveBatch(collections);
            }
            
            String message = String.format("批量添加完成！成功添加 %d 个媒体", successCount);
            if (duplicateCount > 0) {
                message += String.format("，跳过 %d 个已存在的媒体", duplicateCount);
            }
            
            return SaResult.ok(message);
        } catch (Exception e) {
            log.error("批量添加媒体收藏失败", e);
            return SaResult.error("批量添加失败: " + e.getMessage());
        }
    }
    
    @Override
    public SaResult batchSaveTVShows(TVShowBatchRequest request) {
        try {
            // 参数验证
            if (request == null || request.getResults() == null || request.getResults().isEmpty()) {
                return SaResult.error("电视剧数据列表不能为空");
            }
            
            List<TVShow> tvShows = new ArrayList<>();
            int successCount = 0;
            int duplicateCount = 0;
            
            for (TVShowBatchRequest.TVShowData data : request.getResults()) {
                // 检查是否已存在相同tmdbId的电视剧
                TVShow existingTVShow = tvShowMapper.selectOne(new LambdaQueryWrapper<TVShow>()
                        .eq(TVShow::getTmdbId, data.getId())
                );
                
                if (existingTVShow != null) {
                    duplicateCount++;
                    continue;
                }
                
                // 创建新的电视剧记录
                TVShow tvShow = new TVShow();
                tvShow.setTmdbId(data.getId());
                tvShow.setName(data.getName());
                tvShow.setOriginalName(data.getOriginalName());
                tvShow.setOverview(data.getOverview());
                tvShow.setFirstAirDate(data.getFirstAirDate());
                
                tvShow.setOriginalLanguage(data.getOriginalLanguage());

                // 处理genreIds，转换为字符串格式
                if (data.getGenreIds() != null && !data.getGenreIds().isEmpty()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        String genreIdsJson = objectMapper.writeValueAsString(data.getGenreIds());
                        tvShow.setGenreIds(genreIdsJson);
                    } catch (Exception e) {
                        log.warn("转换genreIds为JSON格式时出错，使用默认值", e);
                        tvShow.setGenreIds("[]");
                    }
                } else {
                    tvShow.setGenreIds("[]");
                }

                
                tvShow.setPopularity(data.getPopularity());
                tvShow.setVoteAverage(data.getVoteAverage());
                tvShow.setVoteCount(data.getVoteCount());
                tvShow.setPosterPath(data.getPosterPath());
                tvShow.setBackdropPath(data.getBackdropPath());
                tvShow.setSourceApi("batch_import");
                
                tvShows.add(tvShow);
                successCount++;
            }
            
            // 批量保存
            if (!tvShows.isEmpty()) {
                tvShowMapper.insert(tvShows); // 使用MyBatis-Plus的批量插入方法
            }
            
            return SaResult.ok("成功保存 " + successCount + " 部电视剧，跳过 " + duplicateCount + " 部已存在的电视剧");
        } catch (Exception e) {
            log.error("批量保存电视剧数据失败", e);
            return SaResult.error("处理失败: " + e.getMessage());
        }
    }
    
    @Override
    public SaResult manageCollectionStatus(Long mediaId, Integer mediaType, Integer status, Boolean isAdd) {
        try {
            String currentUserId = StpUtil.getLoginIdAsString();
            Long userId = Long.valueOf(currentUserId);
            
            // 参数验证
            if (mediaId == null) {
                return SaResult.error("媒体ID不能为空");
            }
            
            if (mediaType == null || (mediaType != 1 && mediaType != 2)) {
                return SaResult.error("媒体类型参数错误，必须为 1-电影, 2-电视剧");
            }
            
            if (status == null || (status != 1 && status != 2 && status != 3)) {
                return SaResult.error("状态参数错误，必须为 1-已收藏, 2-已观看, 3-想看");
            }
            
            if (isAdd == null) {
                return SaResult.error("操作类型不能为空");
            }
            
            // 检查媒体是否存在
            String mediaTitle = "";
            Integer tmdbId = null;
            
            if (mediaType == 1) {
                // 电影
                Movie movie = baseMapper.selectById(mediaId);
                if (movie == null) {
                    return SaResult.error("电影不存在");
                }
                mediaTitle = movie.getTitle();
                tmdbId = movie.getTmdbId();
            } else {
                // 电视剧
                TVShow tvShow = tvShowMapper.selectById(mediaId.intValue());
                if (tvShow == null) {
                    return SaResult.error("电视剧不存在");
                }
                mediaTitle = tvShow.getName();
                tmdbId = tvShow.getTmdbId();
            }
            
            // 检查是否已存在该状态的收藏记录
            UserMediaCollection existingCollection = userMediaCollectionService.getOne(new LambdaQueryWrapper<UserMediaCollection>()
                    .eq(UserMediaCollection::getUserId, userId)
                    .eq(UserMediaCollection::getMediaType, mediaType)
                    .eq(UserMediaCollection::getMediaId, mediaId)
                    .eq(UserMediaCollection::getStatus, status)
            );
            
            if (isAdd) {
                // 添加标识
                if (existingCollection != null) {
                    return SaResult.error("已经有该状态的标识了");
                }
                
                // 创建新的收藏记录
                UserMediaCollection collection = new UserMediaCollection();
                collection.setUserId(userId);
                collection.setMediaType(mediaType);
                collection.setMediaId(mediaId);
                collection.setTmdbId(tmdbId);
                collection.setTitle(mediaTitle);
                collection.setStatus(status);
                collection.setCreatedTime(LocalDateTime.now());
                collection.setUpdatedTime(LocalDateTime.now());
                
                userMediaCollectionService.save(collection);
                
                String statusText = getStatusText(status);
                return SaResult.ok(String.format("成功添加「%s」标识", statusText));
            } else {
                // 取消标识
                if (existingCollection == null) {
                    return SaResult.error("该状态的标识不存在");
                }
                
                userMediaCollectionService.removeById(existingCollection.getId());
                
                String statusText = getStatusText(status);
                return SaResult.ok(String.format("成功取消「%s」标识", statusText));
            }
        } catch (Exception e) {
            log.error("管理收藏状态失败", e);
            return SaResult.error("操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取状态文本描述
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 1:
                return "已收藏";
            case 2:
                return "已观看";
            case 3:
                return "想看";
            default:
                return "未知状态";
        }
    }

    @Override
    public SaResult searchByPythonScript(String title, Integer mediaType) {
        try {
            if (title == null || title.trim().isEmpty()) {
                return SaResult.error("搜索标题不能为空");
            }

            if (mediaType == null || (mediaType != 1 && mediaType != 2)) {
                return SaResult.error("媒体类型参数错误，必须为 2-电视剧, 1-电影");
            }

            // 调用Python脚本执行器执行movie_scraper_test.py
            String pythonScriptPath = "py/movie_scraper_test.py";
            String[] params = {title, mediaType.toString()};
            int timeoutMs = 30000; // 30秒超时

            String result = pythonScriptExecutor.executePythonScriptSafe(pythonScriptPath, params, timeoutMs);

            // 检查结果是否为空
            if (result == null || result.trim().isEmpty()) {
                return SaResult.error("Python脚本执行失败，返回结果为空");
            }

            // 返回执行结果
            return SaResult.ok().setData(JSONObject.parse(result));
        } catch (Exception e) {
            log.error("Python脚本执行失败", e);
            return SaResult.error("脚本执行失败: " + e.getMessage());
        }
    }
    
    /**
     * 通过标题获取电影
     */
    private Movie getMovieByTitle(String title) {
        try {
            return baseMapper.selectOne(new LambdaQueryWrapper<Movie>()
                    .eq(Movie::getTitle, title)
                    .or().eq(Movie::getOriginalTitle, title)
                    .last("LIMIT 1")
            );
        } catch (Exception e) {
            log.error("通过标题获取电影失败", e);
            return null;
        }
    }
    
    /**
     * 通过标题获取电视剧
     */
    private TVShow getTVShowByTitle(String title) {
        try {
            return tvShowMapper.selectOne(new LambdaQueryWrapper<TVShow>()
                    .eq(TVShow::getName, title)
                    .or().eq(TVShow::getOriginalName, title)
                    .last("LIMIT 1")
            );
        } catch (Exception e) {
            log.error("通过标题获取电视剧失败", e);
            return null;
        }
    }
    
    @Override
    public SaResult autoSaveFromPythonResult(PythonSearchResultDto searchResultDto) {
        try {
            // 参数校验
            if (searchResultDto == null) {
                return SaResult.error("搜索结果不能为空");
            }
            if (searchResultDto.getMedia_type() == null || (searchResultDto.getMedia_type() != 1 && searchResultDto.getMedia_type() != 2)) {
                return SaResult.error("媒体类型无效，必须是1（电视剧）或2（电影）");
            }
            if (searchResultDto.getResults() == null || searchResultDto.getResults().isEmpty()) {
                return SaResult.error("搜索结果列表为空");
            }

            // 获取第一个搜索结果进行保存
            PythonSearchResultDto.SearchResultItem resultItem = searchResultDto.getResults().get(0);
            String title = resultItem.getTitle();
            Integer mediaType = searchResultDto.getMedia_type();
            
            // 创建保存结果对象
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("original_title", title);
            resultMap.put("media_type", mediaType);
            
            // 根据媒体类型执行不同的保存逻辑
            if (mediaType == 2) { // 电视剧
                // 检查电视剧是否已存在
                TVShow existingTVShow = getTVShowByTitle(title);
                if (existingTVShow != null) {
                    // 电视剧已存在，直接使用已存在的ID建立用户关系
                    resultMap.put("status", "exists_and_saved");
                    resultMap.put("message", "电视剧已存在，已添加到用户收藏");
                    resultMap.put("tv_show_id", existingTVShow.getShowId());
                    
                    // 创建MovieDto用于saveMovie方法
                    MovieDto movieDto = new MovieDto();
                    movieDto.setId(existingTVShow.getShowId().longValue());
                    movieDto.setTitle(title);
                    movieDto.setMediaType(2);
                    movieDto.setTmdbId(existingTVShow.getTmdbId());
                    
                    // 调用saveMovie方法建立用户关系
                    SaResult saveResult = saveMovie(movieDto);
                    if (saveResult.getCode() != 200) {
                        return saveResult;
                    }
                    
                    return SaResult.ok().setData(resultMap);
                }
                
                // 创建新的电视剧记录
                TVShow tvShow = new TVShow();
                tvShow.setName(title);
                tvShow.setOriginalName(title);
                tvShow.setOverview(resultItem.getDescription());
                
                // 处理发布日期
                if (resultItem.getRelease_date() != null && !resultItem.getRelease_date().isEmpty()) {
                    try {
                        // 尝试解析日期，格式可能是 "2019-06-05(中国大陆)"，需要提取日期部分
                        String dateStr = resultItem.getRelease_date().split("\\(")[0].trim();
                        tvShow.setFirstAirDate(LocalDate.parse(dateStr));
                    } catch (Exception e) {
                        log.warn("解析电视剧发布日期失败: {}", e.getMessage());
                    }
                }
                
                // 设置其他属性
                if (resultItem.getRating() != null) {
                    tvShow.setVoteAverage(BigDecimal.valueOf(resultItem.getRating()));
                }
                tvShow.setPosterPath(resultItem.getCover_image());
                tvShow.setTmdbId(0); // 根据要求设置为0
                tvShow.setSourceApi("python_script");
                // 添加getByRecent查询中需要的字段
                tvShow.setPopularity(BigDecimal.valueOf(0.0));
                tvShow.setVoteCount(0);
                // 设置genres作为genreIds
                if (resultItem.getGenres() != null) {
                    tvShow.setGenreIds(resultItem.getGenres().toString());
                } else {
                    tvShow.setGenreIds("[]");
                }
                tvShow.setOriginalLanguage("zh");
                tvShow.setBackdropPath(resultItem.getCover_image());
                
                // 保存电视剧
                tvShowMapper.insert(tvShow);    
                
                // 创建MovieDto用于saveMovie方法
                MovieDto movieDto = new MovieDto();
                movieDto.setId(tvShow.getShowId().longValue());
                movieDto.setTitle(title);
                movieDto.setMediaType(2);
                movieDto.setTmdbId(0);
                
                // 调用saveMovie方法建立用户关系
                SaResult saveResult = saveMovie(movieDto);
                if (saveResult.getCode() != 200) {
                    return saveResult;
                }
                
                resultMap.put("status", "success");
                resultMap.put("message", "电视剧保存成功并添加到用户收藏");
                resultMap.put("tv_show_id", tvShow.getShowId());
            } else if (mediaType == 1) { // 电影
                // 检查电影是否已存在
                Movie existingMovie = getMovieByTitle(title);
                if (existingMovie != null) {
                    // 电影已存在，直接使用已存在的ID建立用户关系
                    resultMap.put("status", "exists_and_saved");
                    resultMap.put("message", "电影已存在，已添加到用户收藏");
                    resultMap.put("movie_id", existingMovie.getMovieId());
                    
                    // 创建MovieDto用于saveMovie方法
                    MovieDto movieDto = new MovieDto();
                    movieDto.setId(existingMovie.getMovieId());
                    movieDto.setTitle(title);
                    movieDto.setMediaType(1);
                    movieDto.setTmdbId(existingMovie.getTmdbId());
                    
                    // 调用saveMovie方法建立用户关系
                    SaResult saveResult = saveMovie(movieDto);
                    if (saveResult.getCode() != 200) {
                        return saveResult;
                    }
                    
                    return SaResult.ok().setData(resultMap);
                }
                
                // 创建新的电影记录
                Movie movie = new Movie();
                movie.setTitle(title);
                movie.setOriginalTitle(title);
                movie.setOverview(resultItem.getDescription());
                
                // 处理发布日期
                if (resultItem.getRelease_date() != null && !resultItem.getRelease_date().isEmpty()) {
                    try {
                        // 尝试解析日期，格式可能是 "2019-06-05(中国大陆)"，需要提取日期部分
                        String dateStr = resultItem.getRelease_date().split("\\(")[0].trim();
                        movie.setReleaseDate(LocalDateTime.parse(dateStr + "T00:00:00"));
                    } catch (Exception e) {
                        log.warn("解析电影发布日期失败: {}", e.getMessage());
                    }
                }
                
                // 设置其他属性
                if (resultItem.getRating() != null) {
                    movie.setVoteAverage(BigDecimal.valueOf(resultItem.getRating()));
                }
                movie.setPosterPath(resultItem.getCover_image());
                movie.setTmdbId(0); // 根据要求设置为0
                movie.setIfDel(0);
                // 添加getByRecent查询中需要的字段
                movie.setPopularity(BigDecimal.valueOf(0.0));
                movie.setVoteCount(0);
                // 设置genres作为genreIds
                if (resultItem.getGenres() != null) {
                    movie.setGenreIds(resultItem.getGenres().toString());
                } else {
                    movie.setGenreIds("[]");
                }
                movie.setOriginalLanguage("zh");
                movie.setBackdropPath(resultItem.getCover_image());
                
                // 保存电影
                baseMapper.insert(movie);
                
                // 创建MovieDto用于saveMovie方法
                MovieDto movieDto = new MovieDto();
                movieDto.setId(movie.getMovieId());
                movieDto.setTitle(title);
                movieDto.setMediaType(1);
                movieDto.setTmdbId(0);
                
                // 调用saveMovie方法建立用户关系
                SaResult saveResult = saveMovie(movieDto);
                if (saveResult.getCode() != 200) {
                    return saveResult;
                }
                
                resultMap.put("status", "success");
                resultMap.put("message", "电影保存成功并添加到用户收藏");
                resultMap.put("movie_id", movie.getMovieId());
            }
            
            return SaResult.ok().setData(resultMap);
        } catch (Exception e) {
            log.error("自动保存搜索结果失败", e);
            return SaResult.error("保存失败：" + e.getMessage());
        }
    }

    @Override
    public SaResult batchSaveMovies(MovieBatchRequest request) {
        try {
            // 参数验证
            if (request == null || request.getResults() == null || request.getResults().isEmpty()) {
                return SaResult.error("电影数据列表不能为空");
            }

            List<Movie> movies = new ArrayList<>();
            int successCount = 0;
            int duplicateCount = 0;

            for (MovieBatchRequest.MovieData data : request.getResults()) {
                // 检查是否已存在相同tmdbId的电影
                Movie existingMovie = baseMapper.selectOne(new LambdaQueryWrapper<Movie>()
                        .eq(Movie::getTmdbId, data.getId())
                );

                if (existingMovie != null) {
                    duplicateCount++;
                    continue;
                }

                // 创建新的电影记录
                Movie movie = new Movie();
                movie.setTmdbId(data.getId());
                movie.setAdult(data.getAdult() ? 1 : 0);
                movie.setOriginalLanguage(data.getOriginalLanguage());
                movie.setOriginalTitle(data.getOriginalTitle());
                movie.setTitle(data.getTitle());
                movie.setOverview(data.getOverview());
                movie.setPosterPath(data.getPosterPath());
                movie.setBackdropPath(data.getBackdropPath());
                movie.setPopularity(data.getPopularity());
                movie.setVoteAverage(data.getVoteAverage());
                movie.setVoteCount(data.getVoteCount());
                movie.setVideo(data.getVideo() ? 1 : 0);
                movie.setIfDel(0); // 默认为不删除

                // 处理releaseDate，转换为LocalDateTime格式
                if (data.getReleaseDate() != null && !data.getReleaseDate().isEmpty()) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate releaseDate = LocalDate.parse(data.getReleaseDate(), formatter);
                        movie.setReleaseDate(releaseDate.atStartOfDay());
                    } catch (DateTimeParseException e) {
                        log.warn("解析上映日期失败: {}", data.getReleaseDate(), e);
                    }
                }

                // 处理genreIds，转换为字符串格式
                if (data.getGenreIds() != null && !data.getGenreIds().isEmpty()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        String genreIdsJson = objectMapper.writeValueAsString(data.getGenreIds());
                        movie.setGenreIds(genreIdsJson);
                    } catch (Exception e) {
                        log.warn("转换genreIds为JSON格式时出错，使用默认值", e);
                        movie.setGenreIds("[]");
                    }
                } else {
                    movie.setGenreIds("[]");
                }

                movies.add(movie);
                successCount++;
            }

            // 批量保存
            if (!movies.isEmpty()) {
                this.saveBatch(movies);
            }

            return SaResult.ok("成功保存 " + successCount + " 部电影，跳过 " + duplicateCount + " 部已存在的电影");
        } catch (Exception e) {
            log.error("批量保存电影数据失败", e);
            return SaResult.error("处理失败: " + e.getMessage());
        }
    }
}
