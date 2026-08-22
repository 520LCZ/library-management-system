package com.library.service;

import com.library.entity.RecommendItem;

import java.util.List;
import java.util.Map;

/**
 * 图书推荐服务接口
 *
 * @author library
 */
public interface RecommendService {

    /**
     * 算热门借阅 + 高分评论 TOP 列表
     */
    Map<String, List<RecommendItem>> recommendList();
}
