package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BookComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 图书评论 Mapper
 *
 * @author library
 */
@Mapper
public interface BookCommentMapper extends BaseMapper<BookComment> {

    /**
     * 分页查询评论(带书名 + 用户名, 支持 keyword 模糊匹配书名/内容, status 过滤)
     */
    @Select("""
            <script>
            SELECT c.*, b.title AS book_title, u.username AS username
            FROM book_comment c
            LEFT JOIN book b ON c.book_id = b.id
            LEFT JOIN `user` u ON c.user_id = u.id
            <where>
              <if test="keyword != null and keyword != ''">
                (b.title LIKE CONCAT('%', #{keyword}, '%') OR c.content LIKE CONCAT('%', #{keyword}, '%'))
              </if>
              <if test="status != null">
                AND c.status = #{status}
              </if>
            </where>
            ORDER BY c.id DESC
            </script>
            """)
    IPage<BookComment> selectPageWithBook(Page<BookComment> page,
                                          @Param("keyword") String keyword,
                                          @Param("status") Integer status);
}
