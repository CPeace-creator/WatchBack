package com.cjh.watching.watchback.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjh.watching.watchback.entity.Movie;
import com.cjh.watching.watchback.mapper.MovieMapper;
import com.cjh.watching.watchback.service.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.RecordComponent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * - @author Cjh。
 * - @date 2025/8/14 13:53。
 **/
@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, Movie> implements MovieService {


    @Override
    public List<Movie> getMovieRecent() {
        return null;
    }

    @Override
    public SaResult importMovie(MultipartFile file) {
        try {
            // 验证文件类型
            if (!file.getOriginalFilename().endsWith(".xlsx") &&
                    !file.getOriginalFilename().endsWith(".xls")) {
                return SaResult.error("仅支持Excel文件(.xlsx/.xls)");
            }

            // 读取Excel内容
            List<String> title = read(file);

            // 返回结果
            return SaResult.ok("Excel读取成功")
                    .setData(title)
                    .set("rowCount", title.size());
        } catch (Exception e) {
            return SaResult.error("Excel读取失败: " + e.getMessage());
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
}
