package com.petadopt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PetWebConfig implements WebMvcConfigurer {
    private final GatewayUserInterceptor interceptor;
    @Value("${pet-adopt.upload-path}") private String uploadPath;

    public PetWebConfig(GatewayUserInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = uploadPath.replace("\\", "/");
        if (!path.endsWith("/")) path += "/";
        registry.addResourceHandler("/files/**").addResourceLocations(
                "file:///" + path,
                "file:///" + path + "resources/"
        );
    }
}
