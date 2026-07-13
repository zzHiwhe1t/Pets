package com.petadopt.config;

import com.petadopt.common.ApiResult;
import com.petadopt.mapper.UserMapper;
import com.petadopt.util.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    public AuthInterceptor(UserMapper userMapper, ObjectMapper objectMapper) { this.userMapper = userMapper; this.objectMapper = objectMapper; }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        Long userId = token == null ? null : userMapper.findIdByToken(token);
        if (userId == null) {
            if ("GET".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().startsWith(request.getContextPath() + "/pets")) return true;
            response.setStatus(401);
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(ApiResult.fail(401, "登录已过期，请重新登录")));
            return false;
        }
        UserContext.set(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
