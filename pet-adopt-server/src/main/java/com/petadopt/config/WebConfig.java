package com.petadopt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    @Value("${pet-adopt.upload-path}") private String uploadPath;
    public WebConfig(AuthInterceptor authInterceptor) { this.authInterceptor = authInterceptor; }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOriginPatterns("*").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*").allowCredentials(true).maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/auth/login", "/auth/register", "/categories/**", "/files/**", "/error");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = uploadPath.replace("\\", "/");
        if (!path.endsWith("/")) path += "/";
        // 用户上传仍写入固定根目录；项目内置素材只保留在 resources，避免根目录重复一份。
        registry.addResourceHandler("/files/**").addResourceLocations(
                "file:///" + path,
                "file:///" + path + "resources/"
        );
    }
}
