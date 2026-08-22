package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Category;
import com.library.mapper.CategoryMapper;
import com.library.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 *
 * @author library
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> tree() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId);
        List<Category> all = baseMapper.selectList(wrapper);
        // 按 parentId 分组, 组装树
        Map<Long, List<Category>> grouped = all.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() == null ? 0L : c.getParentId()));
        List<Category> roots = grouped.getOrDefault(0L, new ArrayList<>());
        buildChildren(roots, grouped);
        return roots;
    }

    private void buildChildren(List<Category> nodes, Map<Long, List<Category>> grouped) {
        for (Category node : nodes) {
            List<Category> children = grouped.getOrDefault(node.getId(), new ArrayList<>());
            node.setChildren(children);
            buildChildren(children, grouped);
        }
    }

    @Override
    public List<Category> list() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId);
        return baseMapper.selectList(wrapper);
    }
}
