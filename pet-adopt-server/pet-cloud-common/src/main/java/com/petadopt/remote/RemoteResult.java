package com.petadopt.remote;

import com.petadopt.common.ApiResult;
import com.petadopt.common.BizException;

public final class RemoteResult {
    private RemoteResult() {}

    public static <T> T data(ApiResult<T> result, String unavailableMessage) {
        if (result == null) throw new BizException(503, unavailableMessage);
        if (result.getCode() != 200) throw new BizException(result.getCode(), result.getMessage());
        return result.getData();
    }
}
