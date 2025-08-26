package com.cjh.watching.watchback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjh.watching.watchback.entity.UserMediaCollection;
import com.cjh.watching.watchback.mapper.UserMediaCollectionMapper;
import com.cjh.watching.watchback.service.UserMediaCollectionService;
import org.springframework.stereotype.Service;

/**
 * - @author Cjh。
 * - @date 2025/8/26 21:40。
 **/
@Service
public class UserMediaCollectionServiceImpl extends ServiceImpl<UserMediaCollectionMapper, UserMediaCollection> implements UserMediaCollectionService {
    // 注意：电影和电视剧的导入逻辑已经合并到MovieServiceImpl中
    // 此类保留用于基础的UserMediaCollection CRUD操作
}