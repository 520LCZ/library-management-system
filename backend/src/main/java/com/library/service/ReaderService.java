package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Reader;

/**
 * 读者服务接口
 *
 * @author library
 */
public interface ReaderService extends IService<Reader> {

    /**
     * 分页查询读者
     */
    IPage<Reader> pageList(Long page, Long size, String keyword);

    /**
     * 查询读者详情(含借阅历史)
     */
    Reader detail(Long id);
}
