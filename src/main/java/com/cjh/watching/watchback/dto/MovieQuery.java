package com.cjh.watching.watchback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * - @author Cjh。
 * - @date 2025/8/28 13:52。
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieQuery {
    private Integer status;
    private String query;

    private BigDecimal totalWatched;
    private BigDecimal monthlyWatched;
    private BigDecimal averageRating;
    private String favoriteGenre;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public BigDecimal getTotalWatched() {
        return totalWatched;
    }

    public void setTotalWatched(BigDecimal totalWatched) {
        this.totalWatched = totalWatched;
    }

    public BigDecimal getMonthlyWatched() {
        return monthlyWatched;
    }

    public void setMonthlyWatched(BigDecimal monthlyWatched) {
        this.monthlyWatched = monthlyWatched;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    public void setFavoriteGenre(String favoriteGenre) {
        this.favoriteGenre = favoriteGenre;
    }
}
