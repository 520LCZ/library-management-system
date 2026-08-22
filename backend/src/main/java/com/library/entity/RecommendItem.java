package com.library.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 推荐图书 VO(非数据库实体, 用于推荐接口返回)
 *
 * @author library
 */
@Data
public class RecommendItem {

    /**
     * 图书 ID
     */
    private Long id;

    /**
     * 书名
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 分类 ID
     */
    private Long categoryId;

    /**
     * 分类名
     */
    private String categoryName;

    /**
     * 封面
     */
    private String cover;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 推荐依据数值: 热门推荐=借阅次数, 高分推荐=平均评分
     */
    private Double score;
}
