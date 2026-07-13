package com.petadopt.controller;

import com.petadopt.common.ApiResult;
import com.petadopt.common.BizException;
import com.petadopt.util.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.util.*;

@RestController
public class UploadController {
    @Value("${pet-adopt.upload-path}") private String uploadPath;
    @Value("${pet-adopt.file-url-prefix}") private String urlPrefix;
    private static final Set<String> ALLOWED = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "webp", "gif"));

    @PostMapping("/upload")
    public ApiResult<Map<String,String>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        UserContext.require();
        if (file.isEmpty()) throw new BizException("请选择图片");
        String original = file.getOriginalFilename() == null ? "image.jpg" : file.getOriginalFilename();
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.') + 1).toLowerCase() : "";
        if (!ALLOWED.contains(ext)) throw new BizException("仅支持 jpg、png、webp、gif 图片");
        Files.createDirectories(Paths.get(uploadPath));
        String filename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path target = Paths.get(uploadPath).resolve(filename).normalize();
        if (!target.getParent().equals(Paths.get(uploadPath).toAbsolutePath().normalize())) throw new BizException("文件路径无效");
        file.transferTo(target.toFile());
        Map<String,String> result = new LinkedHashMap<>(); result.put("url", urlPrefix + filename); result.put("filename", filename);
        return ApiResult.ok(result);
    }
}
