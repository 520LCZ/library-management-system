package com.library.service.impl;

import com.library.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 各维度统计实现
 *
 * @author library
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> borrowByCategory() {
        // value 为 H2 保留字, 用反引号包裹
        return queryLower(
                "SELECT c.name AS name, COUNT(br.id) AS `value` " +
                        "FROM category c " +
                        "LEFT JOIN book b ON b.category_id = c.id " +
                        "LEFT JOIN borrow br ON br.book_id = b.id " +
                        "GROUP BY c.id, c.name ORDER BY `value` DESC");
    }

    @Override
    public List<Map<String, Object>> borrowByMonth() {
        return queryLower(
                "SELECT DATE_FORMAT(borrow_date, '%Y-%m') AS month, COUNT(*) AS `count` " +
                        "FROM borrow GROUP BY month ORDER BY month");
    }

    @Override
    public List<Map<String, Object>> activeReaders() {
        return queryLower(
                "SELECT r.name AS name, COUNT(br.id) AS `value` " +
                        "FROM reader r " +
                        "LEFT JOIN borrow br ON br.reader_id = r.id " +
                        "GROUP BY r.id, r.name ORDER BY `value` DESC LIMIT 10");
    }

    @Override
    public Map<String, Object> inventorySummary() {
        Map<String, Object> result = new LinkedHashMap<>();

        Long totalBooks = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM book", Long.class);
        Long totalStock = jdbcTemplate.queryForObject("SELECT COALESCE(SUM(stock), 0) FROM book", Long.class);
        Long totalBorrowed = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM borrow WHERE status = 1", Long.class);
        result.put("totalBooks", totalBooks == null ? 0 : totalBooks);
        result.put("totalStock", totalStock == null ? 0 : totalStock);
        result.put("totalBorrowed", totalBorrowed == null ? 0 : totalBorrowed);

        result.put("byCategory", queryLower(
                "SELECT c.name AS name, COUNT(b.id) AS `value` " +
                        "FROM category c " +
                        "LEFT JOIN book b ON b.category_id = c.id " +
                        "GROUP BY c.id, c.name ORDER BY `value` DESC"));
        return result;
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
}
