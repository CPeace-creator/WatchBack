package com.cjh.watching.watchback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cjh.watching.watchback.entity.UserMediaCollection;

/**
 * - @author Cjh。
 * - @date 2025/8/26 21:39。
 **/
public interface UserMediaCollectionService extends IService<UserMediaCollection> {
    // 注意：电影和电视剧的导入逻辑已经合并到MovieServiceImpl中
    // 此接口保留用于基础的UserMediaCollection CRUD操作
}