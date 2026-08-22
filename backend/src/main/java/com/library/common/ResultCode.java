package com.library.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务响应状态码
 *
 * @author library
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "success"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    FAIL(500, "服务异常"),
    PARAM_ERROR(400, "参数错误"),
    BUSINESS_ERROR(1000, "业务异常");

    private final Integer code;
    private final String message;
}
