package com.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 借阅请求 DTO
 *
 * @author library
 */
@Data
public class BorrowDTO {

    @NotNull(message = "图书ID不能为空")
    private Long bookId;

    @NotNull(message = "读者ID不能为空")
    private Long readerId;

    @NotNull(message = "借阅天数不能为空")
    private Integer days;
}
