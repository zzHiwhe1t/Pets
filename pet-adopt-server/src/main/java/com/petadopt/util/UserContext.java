package com.petadopt.util;

public final class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private UserContext() {}
    public static void set(Long userId) { USER_ID.set(userId); }
    public static Long get() { return USER_ID.get(); }
    public static Long require() {
        Long id = get();
        if (id == null) throw new com.petadopt.common.BizException(401, "请先登录");
        return id;
    }
    public static void clear() { USER_ID.remove(); }
}
