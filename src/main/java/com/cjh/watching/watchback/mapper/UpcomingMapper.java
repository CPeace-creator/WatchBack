package com.cjh.watching.watchback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cjh.watching.watchback.entity.Upcoming;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UpcomingMapper extends BaseMapper<Upcoming> {
    IPage<Upcoming> getPages(Page<Upcoming> page1);
}
