package com.library.controller;

import com.library.common.Result;
import com.library.entity.RecommendItem;
import com.library.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 图书推荐控制器(算法推荐, 无需新表)
 *
 * @author library
 */
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    /**
     * 获取推荐列表: 热门借阅 TOP 10 + 高分评论 TOP 10
     */
    @GetMapping("/list")
    public Result<Map<String, List<RecommendItem>>> list() {
        return Result.ok(recommendService.recommendList());
    }
}
