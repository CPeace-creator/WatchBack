package com.cjh.watching.watchback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
