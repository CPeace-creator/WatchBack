package com.cjh.watching.watchback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 电视剧批量保存请求DTO
 * 用于接收包含page、results和total_pages等字段的JSON参数
 * -
 */
@Data
public class TVShowBatchRequest {
    
    private Integer page;
    private List<TVShowData> results;
    private Integer totalPages;
    private Integer totalResults;
    
    /**
     * 电视剧数据内部类
     * 对应results数组中的每个元素
     */
    @Data
    public static class TVShowData {
        @JsonProperty("adult")
        private Boolean adult;

        @JsonProperty("backdrop_path")
        private String backdropPath;

        @JsonProperty("genre_ids")
        private List<Integer> genreIds;

        @JsonProperty("id")
        private Integer id;

        @JsonProperty("origin_country")
        private List<String> originCountry;

        @JsonProperty("original_language")
        private String originalLanguage;

        @JsonProperty("original_name")
        private String originalName;

        @JsonProperty("overview")
        private String overview;

        @JsonProperty("popularity")
        private BigDecimal popularity;

        @JsonProperty("poster_path")
        private String posterPath;

        @JsonProperty("first_air_date")
        private LocalDate firstAirDate;

        @JsonProperty("name")
        private String name;

        @JsonProperty("vote_average")
        private BigDecimal voteAverage;

        @JsonProperty("vote_count")
        private Integer voteCount;

        public Boolean getAdult() {
            return adult;
        }

        public void setAdult(Boolean adult) {
            this.adult = adult;
        }

        public String getBackdropPath() {
            return backdropPath;
        }

        public void setBackdropPath(String backdropPath) {
            this.backdropPath = backdropPath;
        }

        public List<Integer> getGenreIds() {
            return genreIds;
        }

        public void setGenreIds(List<Integer> genreIds) {
            this.genreIds = genreIds;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public List<String> getOriginCountry() {
            return originCountry;
        }

        public void setOriginCountry(List<String> originCountry) {
            this.originCountry = originCountry;
        }

        public String getOriginalLanguage() {
            return originalLanguage;
        }

        public void setOriginalLanguage(String originalLanguage) {
            this.originalLanguage = originalLanguage;
        }

        public String getOriginalName() {
            return originalName;
        }

        public void setOriginalName(String originalName) {
            this.originalName = originalName;
        }

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public BigDecimal getPopularity() {
            return popularity;
        }

        public void setPopularity(BigDecimal popularity) {
            this.popularity = popularity;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public LocalDate getFirstAirDate() {
            return firstAirDate;
        }

        public void setFirstAirDate(LocalDate firstAirDate) {
            this.firstAirDate = firstAirDate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getVoteAverage() {
            return voteAverage;
        }

        public void setVoteAverage(BigDecimal voteAverage) {
            this.voteAverage = voteAverage;
        }

        public Integer getVoteCount() {
            return voteCount;
        }

        public void setVoteCount(Integer voteCount) {
            this.voteCount = voteCount;
        }
    }
}