package com.library.service;

import java.util.List;
import java.util.Map;

/**
 * 驾驶舱聚合统计接口
 *
 * @author library
 */
public interface DashboardService {

    /**
     * 驾驶舱综合统计
     */
    Map<String, Object> stats();
}
