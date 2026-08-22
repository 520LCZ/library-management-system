package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统用户 Mapper
 *
 * @author library
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 分页查询用户(关键字匹配用户名/昵称)
     */
    @Select("""
            <script>
            SELECT * FROM `user`
            <where>
              <if test="keyword != null and keyword != ''">
                (username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%'))
              </if>
            </where>
            ORDER BY id ASC
            </script>
            """)
    IPage<User> selectPageWithKeyword(Page<User> page, @Param("keyword") String keyword);
}
