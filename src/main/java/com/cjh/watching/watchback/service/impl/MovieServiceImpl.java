package com.cjh.watching.watchback.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjh.watching.watchback.dto.MovieDto;
import com.cjh.watching.watchback.dto.MovieQuery;
import com.cjh.watching.watchback.entity.Movie;
import com.cjh.watching.watchback.entity.TVShow;
import com.cjh.watching.watchback.entity.UserMediaCollection;
import com.cjh.watching.watchback.enums.MovieStatusEnum;
import com.cjh.watching.watchback.mapper.MovieMapper;
import com.cjh.watching.watchback.mapper.TVShowMapper;
import com.cjh.watching.watchback.mapper.UserMediaCollectionMapper;
import com.cjh.watching.watchback.service.MovieService;
import com.cjh.watching.watchback.service.UserMediaCollectionService;
import com.cjh.watching.watchback.utils.ImportResult;
import com.cjh.watching.watchback.utils.PageRequest;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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
            // 查找数据库中已存在的电影
            List<Movie> existingMovies = baseMapper.selectList(new LambdaQueryWrapper<Movie>()
                    .in(Movie::getTitle, titles)
                    .or().in(Movie::getOriginalTitle, titles)
            );
            
            // 提取已存在电影的标题（包括原始标题）
            Set<String> existingMovieTitles = new HashSet<>();
            Map<String, Movie> titleToMovieMap = new HashMap<>();
            for (Movie movie : existingMovies) {
                existingMovieTitles.add(movie.getTitle());
                existingMovieTitles.add(movie.getOriginalTitle());
                titleToMovieMap.put(movie.getTitle(), movie);
                titleToMovieMap.put(movie.getOriginalTitle(), movie);
            }
            
            // 找出不存在的电影标题
            List<String> notFoundMovies = titles.stream()
                    .filter(title -> !existingMovieTitles.contains(title))
                    .collect(Collectors.toList());
            
            // 批量添加用户收藏记录
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
            
            // 批量保存
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
            // 查找数据库中已存在的电视剧
            List<TVShow> existingTVShows = tvShowMapper.selectList(new LambdaQueryWrapper<TVShow>()
                    .in(TVShow::getName, titles)
                    .or().in(TVShow::getOriginalName, titles)
            );
            
            // 提取已存在电视剧的标题（包括原始标题）
            Set<String> existingTVShowTitles = new HashSet<>();
            Map<String, TVShow> titleToTVShowMap = new HashMap<>();
            for (TVShow tvShow : existingTVShows) {
                existingTVShowTitles.add(tvShow.getName());
                existingTVShowTitles.add(tvShow.getOriginalName());
                titleToTVShowMap.put(tvShow.getName(), tvShow);
                titleToTVShowMap.put(tvShow.getOriginalName(), tvShow);
            }
            
            // 找出不存在的电视剧标题
            List<String> notFoundTVShows = titles.stream()
                    .filter(title -> !existingTVShowTitles.contains(title))
                    .collect(Collectors.toList());
            
            // 批量添加用户收藏记录
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
            
            // 批量保存
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
        switch (type){
            case 1:
                Movie movies = baseMapper.selectOne(new LambdaQueryWrapper<Movie>().eq(Movie::getMovieId, query));
                // 使用BeanUtil.copyToList复制列表
                MovieDto movieDtos = BeanUtil.copyProperties(movies, MovieDto.class);
                movieDtos.setId(movies.getMovieId());
                movieDtos.setMediaType(1);
                result = movieDtos;
                break;
            case 2:
                TVShow shows = tvShowMapper.selectOne(new LambdaQueryWrapper<TVShow>().eq(TVShow::getShowId,query));
                // 使用BeanUtil.copyToList将TVShow转换为MovieDto
                MovieDto showDtos = BeanUtil.copyProperties(shows, MovieDto.class);
                showDtos.setId(shows.getShowId().longValue());
                showDtos.setMediaType(2);
                result=showDtos;
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
}
