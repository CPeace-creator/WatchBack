package com.cjh.watching.watchback.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjh.watching.watchback.entity.Upcoming;
import com.cjh.watching.watchback.mapper.UpcomingMapper;
import com.cjh.watching.watchback.service.UpcomingService;
import com.cjh.watching.watchback.utils.PageRequest;
import org.springframework.stereotype.Service;

/**
 * - @author Cjh。
 * - @date 2025/8/29 16:23。
 **/
@Service
public class UpcomingServiceImpl extends ServiceImpl<UpcomingMapper, Upcoming> implements UpcomingService {

    @Override
    public IPage<Upcoming> getPages(PageRequest page) {
        Page<Upcoming> page1=new Page<>(page.getPageNum(),page.getPageSize());
        IPage<Upcoming> pages = baseMapper.getPages(page1);
        return pages;
    }
}
