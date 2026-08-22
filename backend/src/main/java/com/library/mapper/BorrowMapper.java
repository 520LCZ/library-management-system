package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Borrow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 借阅 Mapper
 *
 * @author library
 */
@Mapper
public interface BorrowMapper extends BaseMapper<Borrow> {

    /**
     * 分页查询借阅记录(带图书名 + 读者名)
     */
    @Select("""
            <script>
            SELECT br.id, br.book_id, br.reader_id, br.borrow_date, br.due_date,
                   br.return_date, br.status, br.create_time, br.update_time,
                   b.title AS book_title, r.name AS reader_name
            FROM borrow br
            LEFT JOIN book b ON br.book_id = b.id
            LEFT JOIN reader r ON br.reader_id = r.id
            <where>
              <if test="status != null">
                AND br.status = #{status}
              </if>
            </where>
            ORDER BY br.id DESC
            </script>
            """)
    IPage<Borrow> selectPageWithDetail(Page<Borrow> page, @Param("status") Integer status);

    /**
     * 查询逾期未还记录
     */
    @Select("""
            SELECT br.id, br.book_id, br.reader_id, br.borrow_date, br.due_date,
                   br.return_date, br.status, br.create_time, br.update_time,
                   b.title AS book_title, r.name AS reader_name
            FROM borrow br
            LEFT JOIN book b ON br.book_id = b.id
            LEFT JOIN reader r ON br.reader_id = r.id
            WHERE br.return_date IS NULL AND br.due_date < CURRENT_DATE
            ORDER BY br.due_date ASC
            """)
    List<Borrow> selectOverdueList();

    /**
     * 将所有逾期未还记录标记为已逾期状态
     */
    @Update("""
            UPDATE borrow SET status = 3
            WHERE return_date IS NULL AND due_date < CURRENT_DATE AND status != 3
            """)
    int updateOverdueStatus();
}
