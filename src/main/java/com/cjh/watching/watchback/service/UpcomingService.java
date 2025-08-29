package com.cjh.watching.watchback.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cjh.watching.watchback.entity.Upcoming;
import com.cjh.watching.watchback.utils.PageRequest;

public interface UpcomingService extends IService<Upcoming> {
    IPage<Upcoming> getPages(PageRequest page);
}
