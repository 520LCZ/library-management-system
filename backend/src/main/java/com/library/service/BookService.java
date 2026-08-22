package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Book;

/**
 * 图书服务接口
 *
 * @author library
 */
public interface BookService extends IService<Book> {

    /**
     * 分页查询图书(带关键字 + 分类)
     */
    IPage<Book> pageList(Long page, Long size, String keyword, Long categoryId);

    /**
     * 查询单本图书(含分类名)
     */
    Book detail(Long id);

    /**
     * 上下架
     */
    boolean changeStatus(Long id, Integer status);
}
