package com.library.mapper;

import com.library.entity.RecommendItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 图书推荐 Mapper(算法查询, 无实体表)
 *
 * @author library
 */
@Mapper
public interface RecommendMapper {

    /**
     * 热门借阅 TOP N: 按 borrow 表 book_id 分组 count 倒序, 联 book + category
     */
    @Select("""
            SELECT b.id AS id, b.title AS title, b.author AS author,
                   b.category_id AS categoryId, c.name AS categoryName,
                   b.cover AS cover, b.price AS price,
                   COUNT(br.id) AS score
            FROM borrow br
            LEFT JOIN book b ON br.book_id = b.id
            LEFT JOIN category c ON b.category_id = c.id
            GROUP BY b.id, b.title, b.author, b.category_id, c.name, b.cover, b.price
            ORDER BY score DESC
            LIMIT #{limit}
            """)
    List<RecommendItem> hotTop(@Param("limit") int limit);

    /**
     * 高分评论 TOP N: book_comment status=1 按 book_id 分组 avg(rating) 倒序, 联 book + category
     */
    @Select("""
            SELECT b.id AS id, b.title AS title, b.author AS author,
                   b.category_id AS categoryId, c.name AS categoryName,
                   b.cover AS cover, b.price AS price,
                   AVG(cm.rating) AS score
            FROM book_comment cm
            LEFT JOIN book b ON cm.book_id = b.id
            LEFT JOIN category c ON b.category_id = c.id
            WHERE cm.status = 1
            GROUP BY b.id, b.title, b.author, b.category_id, c.name, b.cover, b.price
            ORDER BY score DESC
            LIMIT #{limit}
            """)
    List<RecommendItem> ratingTop(@Param("limit") int limit);
}
