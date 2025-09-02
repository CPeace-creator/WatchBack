package com.cjh.watching.watchback.utils;

import com.cjh.watching.watchback.entity.Movie;
import com.cjh.watching.watchback.entity.TVShow;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 媒体导入结果类
 */
@Data
@AllArgsConstructor
@Getter
@Setter
public class ImportResult {
    private int totalCount = 0;                  // 总导入数量
    private int successMovieCount = 0;           // 成功导入的电影数量
    private int successTVShowCount = 0;          // 成功导入的电视剧数量
    private List<String> notFoundMovieTitles = new ArrayList<>(); // 未找到的电影标题列表
    private List<String> notFoundTVShowTitles = new ArrayList<>(); // 未找到的电视剧标题列表
    private List<MovieFuzzyMatch> movieFuzzyMatches = new ArrayList<>(); // 电影模糊匹配结果
    private List<TVShowFuzzyMatch> tvShowFuzzyMatches = new ArrayList<>(); // 电视剧模糊匹配结果
    private String movieErrorMessage;        // 电影导入错误信息
    private String tvShowErrorMessage;       // 电视剧导入错误信息

    // 构造函数，初始化所有列表字段
    public ImportResult() {
        this.notFoundMovieTitles = new ArrayList<>();
        this.notFoundTVShowTitles = new ArrayList<>();
        this.movieFuzzyMatches = new ArrayList<>();
        this.tvShowFuzzyMatches = new ArrayList<>();
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getSuccessMovieCount() {
        return successMovieCount;
    }

    public void setSuccessMovieCount(int successMovieCount) {
        this.successMovieCount = successMovieCount;
    }

    public int getSuccessTVShowCount() {
        return successTVShowCount;
    }

    public void setSuccessTVShowCount(int successTVShowCount) {
        this.successTVShowCount = successTVShowCount;
    }

    public List<String> getNotFoundMovieTitles() {
        return notFoundMovieTitles;
    }

    public void setNotFoundMovieTitles(List<String> notFoundMovieTitles) {
        this.notFoundMovieTitles = notFoundMovieTitles;
    }

    public List<String> getNotFoundTVShowTitles() {
        return notFoundTVShowTitles;
    }

    public void setNotFoundTVShowTitles(List<String> notFoundTVShowTitles) {
        this.notFoundTVShowTitles = notFoundTVShowTitles;
    }

    public String getMovieErrorMessage() {
        return movieErrorMessage;
    }

    public void setMovieErrorMessage(String movieErrorMessage) {
        this.movieErrorMessage = movieErrorMessage;
    }

    public String getTvShowErrorMessage() {
        return tvShowErrorMessage;
    }

    public void setTvShowErrorMessage(String tvShowErrorMessage) {
        this.tvShowErrorMessage = tvShowErrorMessage;
    }

    public List<MovieFuzzyMatch> getMovieFuzzyMatches() {
        return movieFuzzyMatches;
    }

    public void setMovieFuzzyMatches(List<MovieFuzzyMatch> movieFuzzyMatches) {
        this.movieFuzzyMatches = movieFuzzyMatches;
    }

    public List<TVShowFuzzyMatch> getTvShowFuzzyMatches() {
        return tvShowFuzzyMatches;
    }

    public void setTvShowFuzzyMatches(List<TVShowFuzzyMatch> tvShowFuzzyMatches) {
        this.tvShowFuzzyMatches = tvShowFuzzyMatches;
    }

    /**
     * 电影模糊匹配结果
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MovieFuzzyMatch {
        private String inputTitle;           // 用户输入的标题
        private List<Movie> matchedMovies;   // 匹配到的电影列表

        public List<Movie> getMatchedMovies() {
            return matchedMovies;
        }

        public void setMatchedMovies(List<Movie> matchedMovies) {
            this.matchedMovies = matchedMovies;
        }

        public String getInputTitle() {
            return inputTitle;
        }

        public void setInputTitle(String inputTitle) {
            this.inputTitle = inputTitle;
        }
    }

    /**
     * 电视剧模糊匹配结果
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TVShowFuzzyMatch {
        private String inputTitle;           // 用户输入的标题
        private List<TVShow> matchedTVShows; // 匹配到的电视剧列表

        public String getInputTitle() {
            return inputTitle;
        }

        public void setInputTitle(String inputTitle) {
            this.inputTitle = inputTitle;
        }

        public List<TVShow> getMatchedTVShows() {
            return matchedTVShows;
        }

        public void setMatchedTVShows(List<TVShow> matchedTVShows) {
            this.matchedTVShows = matchedTVShows;
        }
    }
}
