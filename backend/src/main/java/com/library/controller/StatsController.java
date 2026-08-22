package com.library.controller;

import com.library.common.Result;
import com.library.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 统计控制器(各维度)
 *
 * @author library
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    /**
     * 各分类借阅次数
     */
    @GetMapping("/borrow-by-category")
    public Result<List<Map<String, Object>>> borrowByCategory() {
        return Result.ok(statsService.borrowByCategory());
    }

    /**
     * 月度借阅次数
     */
    @GetMapping("/borrow-by-month")
    public Result<List<Map<String, Object>>> borrowByMonth() {
        return Result.ok(statsService.borrowByMonth());
    }

    /**
     * 借阅最多的读者 Top10
     */
    @GetMapping("/active-readers")
    public Result<List<Map<String, Object>>> activeReaders() {
        return Result.ok(statsService.activeReaders());
    }

    /**
     * 库存汇总
     */
    @GetMapping("/inventory-summary")
    public Result<Map<String, Object>> inventorySummary() {
        return Result.ok(statsService.inventorySummary());
    }
}
