package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Reader;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 读者 Mapper
 *
 * @author library
 */
@Mapper
public interface ReaderMapper extends BaseMapper<Reader> {

    /**
     * 分页查询读者(带关键字)
     */
    @Select("""
            <script>
            SELECT * FROM reader
            <where>
              <if test="keyword != null and keyword != ''">
                (name LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%'))
              </if>
            </where>
            ORDER BY id DESC
            </script>
            """)
    IPage<Reader> selectPageWithKeyword(Page<Reader> page, @Param("keyword") String keyword);
}
