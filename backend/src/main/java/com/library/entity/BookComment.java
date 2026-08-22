package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图书评论实体
 *
 * @author library
 */
@Data
@EqualsAndHashCode(callSuper = true)
@com.baomidou.mybatisplus.annotation.TableName("book_comment")
public class BookComment extends BaseEntity {

    /**
     * 图书 ID
     */
    private Long bookId;

    /**
     * 评论用户 ID
     */
    private Long userId;

    /**
     * 评分 1-5 星
     */
    private Integer rating;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 状态: 0待审核 1通过 2驳回
     */
    private Integer status;

    /**
     * 图书名称(非数据库字段, 联查返回)
     */
    @TableField(exist = false)
    private String bookTitle;

    /**
     * 评论人用户名(非数据库字段, 联查返回)
     */
    @TableField(exist = false)
    private String username;
}
