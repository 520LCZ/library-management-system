package com.library.service;

import java.util.List;
import java.util.Map;

/**
 * 各维度统计接口
 *
 * @author library
 */
public interface StatsService {

    /**
     * 各分类借阅次数
     */
    List<Map<String, Object>> borrowByCategory();

    /**
     * 月度借阅次数
     */
    List<Map<String, Object>> borrowByMonth();

    /**
     * 借阅最多的读者 Top10
     */
    List<Map<String, Object>> activeReaders();

    /**
     * 库存汇总
     */
    Map<String, Object> inventorySummary();
}
