package com.library.service.impl;

import com.library.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 驾驶舱聚合统计实现
 *
 * @author library
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> stats() {
        Map<String, Object> result = new LinkedHashMap<>();

        // KPI 指标
        Map<String, Object> kpi = new LinkedHashMap<>();
        kpi.put("bookCount", countOf("SELECT COUNT(*) FROM book"));
        kpi.put("readerCount", countOf("SELECT COUNT(*) FROM reader"));
        kpi.put("borrowingCount", countOf("SELECT COUNT(*) FROM borrow WHERE status = 1"));
        kpi.put("overdueCount", countOf("SELECT COUNT(*) FROM borrow WHERE status = 3 OR (return_date IS NULL AND due_date < CURRENT_DATE)"));
        result.put("kpi", kpi);

        // 月度借阅趋势(返回 1-12 月, 缺失补 0)
        result.put("borrowTrend", monthTrend("SELECT EXTRACT(MONTH FROM borrow_date) AS m, COUNT(*) AS c FROM borrow GROUP BY m ORDER BY m"));

        // 分类分布(各分类图书数量), value 为 H2 保留字, 需用反引号
        result.put("categoryDist", queryLower(
                "SELECT c.name AS name, COUNT(b.id) AS `value` FROM category c " +
                        "LEFT JOIN book b ON b.category_id = c.id " +
                        "GROUP BY c.id, c.name ORDER BY `value` DESC"));

        // 借阅次数 Top5 图书
        result.put("topBooks", queryLower(
                "SELECT b.title AS name, COUNT(br.id) AS `value` FROM borrow br " +
                        "LEFT JOIN book b ON br.book_id = b.id " +
                        "GROUP BY b.id, b.title ORDER BY `value` DESC LIMIT 5"));

        // 读者增长(按月统计新增读者)
        result.put("readerGrowth", monthTrend("SELECT EXTRACT(MONTH FROM register_date) AS m, COUNT(*) AS c FROM reader GROUP BY m ORDER BY m"));

        return result;
    }

    /**
     * 执行 COUNT(*) 查询并返回数值
     */
    private long countOf(String sql) {
        Long n = jdbcTemplate.queryForObject(sql, Long.class);
        return n == null ? 0 : n;
    }

    /**
     * 执行查询并将返回 Map 的 key 全部转为小写(H2 默认返回大写 key)
     */
    private List<Map<String, Object>> queryLower(String sql) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> result = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            Map<String, Object> lowered = new LinkedHashMap<>();
            for (Map.Entry<String, Object> e : row.entrySet()) {
                lowered.put(e.getKey().toLowerCase(), e.getValue());
            }
            result.add(lowered);
        }
        return result;
    }

    /**
     * 将"按月分组"查询结果补全为 12 个月, 月份格式为 "01".."12"
     */
    private List<Map<String, Object>> monthTrend(String sql) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        long[] counts = new long[13];
        for (Map<String, Object> row : rows) {
            Object mo = row.get("m");
            Object c = row.get("c");
            int month = mo instanceof Number ? ((Number) mo).intValue() : Integer.parseInt(String.valueOf(mo));
            long cnt = c instanceof Number ? ((Number) c).longValue() : Long.parseLong(String.valueOf(c));
            if (month >= 1 && month <= 12) {
                counts[month] = cnt;
            }
        }
        List<Map<String, Object>> result = new ArrayList<>(12);
        for (int i = 1; i <= 12; i++) {
            Map<String, Object> item = new LinkedHashMap<>(2);
            item.put("month", String.format("%02d", i));
            item.put("count", counts[i]);
            result.add(item);
        }
        return result;
    }
}
