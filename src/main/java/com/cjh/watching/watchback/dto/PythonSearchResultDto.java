package com.cjh.watching.watchback.dto;

import lombok.Data;

import java.util.List;

/**
 * 接收Python脚本返回的搜索结果DTO
 * - @author Cjh
 */
@Data
public class PythonSearchResultDto {
    private Integer media_type; // 1-电视剧，2-电影
    private String title;
    private String message;
    private List<SearchResultItem> results;
    private String status;
    private Integer total_results;

    public Integer getMedia_type() {
        return media_type;
    }

    public void setMedia_type(Integer media_type) {
        this.media_type = media_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SearchResultItem> getResults() {
        return results;
    }

    public void setResults(List<SearchResultItem> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotal_results() {
        return total_results;
    }

    public void setTotal_results(Integer total_results) {
        this.total_results = total_results;
    }

    @Data
    public static class SearchResultItem {
        private String release_date;
        private String scraped_at;
        private Double rating;
        private String link;
        private Integer rank;
        private String description;
        private String cover_image;
        private String source;
        private String title;
        private String type;

        public String getRelease_date() {
            return release_date;
        }

        public void setRelease_date(String release_date) {
            this.release_date = release_date;
        }

        public String getScraped_at() {
            return scraped_at;
        }

        public void setScraped_at(String scraped_at) {
            this.scraped_at = scraped_at;
        }

        public Double getRating() {
            return rating;
        }

        public void setRating(Double rating) {
            this.rating = rating;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCover_image() {
            return cover_image;
        }

        public void setCover_image(String cover_image) {
            this.cover_image = cover_image;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}