package com.cjh.watching.watchback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 电影批量导入请求DTO
 */
@Data
public class MovieBatchRequest {
    private Integer page;
    private List<MovieData> results;
    private Integer totalPages;
    private Integer totalResults;

    @Data
    public static class MovieData {
        @JsonProperty("adult")
        private Boolean adult;

        @JsonProperty("backdrop_path")
        private String backdropPath;

        @JsonProperty("genre_ids")
        private List<Integer> genreIds;

        @JsonProperty("id")
        private Integer id;

        @JsonProperty("original_language")
        private String originalLanguage;

        @JsonProperty("original_title")
        private String originalTitle;

        @JsonProperty("overview")
        private String overview;

        @JsonProperty("popularity")
        private BigDecimal popularity;

        @JsonProperty("poster_path")
        private String posterPath;

        @JsonProperty("release_date")
        private String releaseDate;

        @JsonProperty("title")
        private String title;

        @JsonProperty("video")
        private Boolean video;

        @JsonProperty("vote_average")
        private BigDecimal voteAverage;

        @JsonProperty("vote_count")
        private Integer voteCount;
    }
}