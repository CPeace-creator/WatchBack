package com.cjh.watching.watchback.dto;

import lombok.Data;

import java.util.List;

/**
 * 模糊匹配确认请求DTO
 * - @author Cjh。
 * - @date 2025/8/30。
 **/
@Data
public class FuzzyMatchRequest {
    private Long userId;                    // 用户ID
    private List<Long> selectedMovies;      // 用户选择的电影ID列表
    private List<Long> selectedTVShows;     // 用户选择的电视剧ID列表

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getSelectedMovies() {
        return selectedMovies;
    }

    public void setSelectedMovies(List<Long> selectedMovies) {
        this.selectedMovies = selectedMovies;
    }

    public List<Long> getSelectedTVShows() {
        return selectedTVShows;
    }

    public void setSelectedTVShows(List<Long> selectedTVShows) {
        this.selectedTVShows = selectedTVShows;
    }
}