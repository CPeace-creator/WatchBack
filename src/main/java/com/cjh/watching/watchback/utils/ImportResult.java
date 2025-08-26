package com.cjh.watching.watchback.utils;

/**
 * - @author Cjh。
 * - @date 2025/8/26 16:53。
 **/

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 媒体导入结果类
 */
@Data
@AllArgsConstructor
public class ImportResult {
    private int totalCount = 0;                  // 总导入数量
    private int successMovieCount = 0;           // 成功导入的电影数量
    private int successTVShowCount = 0;          // 成功导入的电视剧数量
    private List<String> notFoundMovieTitles = new ArrayList<>(); // 未找到的电影标题列表
    private List<String> notFoundTVShowTitles = new ArrayList<>(); // 未找到的电视剧标题列表
    private String movieErrorMessage;        // 电影导入错误信息
    private String tvShowErrorMessage;       // 电视剧导入错误信息

    // 构造函数，初始化所有列表字段
    public ImportResult() {
        this.notFoundMovieTitles = new ArrayList<>();
        this.notFoundTVShowTitles = new ArrayList<>();
    }

}
