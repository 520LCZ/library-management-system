package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 *
 * @author library
 */
public interface CategoryService extends IService<Category> {

    /**
     * 查询分类树
     */
    List<Category> tree();

    /**
     * 查询全部分类
     */
    List<Category> list();
}
