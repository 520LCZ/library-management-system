package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类 Mapper
 *
 * @author library
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
