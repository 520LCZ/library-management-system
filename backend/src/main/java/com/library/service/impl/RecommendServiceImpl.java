package com.library.service.impl;

import com.library.entity.RecommendItem;
import com.library.mapper.RecommendMapper;
import com.library.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 图书推荐服务实现
 * - 热门借阅: borrow 表分组 count 倒序
 * - 高分评论: book_comment(status=1) avg(rating) 倒序
 *
 * @author library
 */
@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private static final int TOP_LIMIT = 10;

    private final RecommendMapper recommendMapper;

    @Override
    public Map<String, List<RecommendItem>> recommendList() {
        Map<String, List<RecommendItem>> result = new LinkedHashMap<>(4);
        result.put("hot", recommendMapper.hotTop(TOP_LIMIT));
        result.put("rating", recommendMapper.ratingTop(TOP_LIMIT));
        return result;
    }
}
