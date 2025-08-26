package com.cjh.watching.watchback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjh.watching.watchback.entity.TVShow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 电视剧Mapper接口
 * - @author Cjh。
 * - @date 2025/8/26 22:45。
 **/
@Mapper
public interface TVShowMapper extends BaseMapper<TVShow> {
    // BaseMapper已经提供了基础的CRUD操作
    // 如有特殊查询需求，可以在这里添加自定义方法
}