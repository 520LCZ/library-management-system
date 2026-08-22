package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 图书 Mapper
 *
 * @author library
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {

    /**
     * 分页查询图书(带分类名)
     */
    @Select("""
            <script>
            SELECT b.*, c.name AS category_name
            FROM book b
            LEFT JOIN category c ON b.category_id = c.id
            <where>
              <if test="keyword != null and keyword != ''">
                (b.title LIKE CONCAT('%', #{keyword}, '%') OR b.author LIKE CONCAT('%', #{keyword}, '%') OR b.isbn LIKE CONCAT('%', #{keyword}, '%'))
              </if>
              <if test="categoryId != null">
                AND b.category_id = #{categoryId}
              </if>
            </where>
            ORDER BY b.id DESC
            </script>
            """)
    IPage<Book> selectPageWithCategory(Page<Book> page, @Param("keyword") String keyword, @Param("categoryId") Long categoryId);

    /**
     * 根据 ID 查询图书(带分类名)
     */
    @Select("""
            SELECT b.*, c.name AS category_name
            FROM book b
            LEFT JOIN category c ON b.category_id = c.id
            WHERE b.id = #{id}
            """)
    Book selectByIdWithCategory(@Param("id") Long id);
}
