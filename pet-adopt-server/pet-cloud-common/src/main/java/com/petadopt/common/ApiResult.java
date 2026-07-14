package com.petadopt.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(200, "操作成功", data);
    }

    public static ApiResult<Void> ok() {
        return new ApiResult<>(200, "操作成功", null);
    }

    public static ApiResult<Void> fail(int code, String message) {
        return new ApiResult<>(code, message, null);
    }
}
