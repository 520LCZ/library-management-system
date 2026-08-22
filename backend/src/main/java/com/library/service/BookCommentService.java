package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.BookComment;

/**
 * 图书评论服务接口
 *
 * @author library
 */
public interface BookCommentService extends IService<BookComment> {

    /**
     * 分页查询评论(带书名/用户名, 支持 keyword + status 过滤)
     */
    IPage<BookComment> pageList(Long page, Long size, String keyword, Integer status);

    /**
     * 审核: status=1 通过 / 2 驳回
     */
    boolean audit(Long id, Integer status);
}
