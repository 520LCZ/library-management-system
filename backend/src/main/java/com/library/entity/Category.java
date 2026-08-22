package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 分类实体
 *
 * @author library
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity {

    private String name;

    private Long parentId;

    private Integer sort;

    /**
     * 子分类列表(非数据库字段, 用于树形展示)
     */
    @TableField(exist = false)
    private List<Category> children;
}
