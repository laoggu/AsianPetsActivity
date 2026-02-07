package org.example.asianpetssystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.asianpetssystem.common.dto.ApiResponse;
import org.example.asianpetssystem.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@Tag(name = "文件管理", description = "文件上传下载接口")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传通用文件")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "general") String directory) {
        
        logger.info("上传文件 - originalName={}, size={}", file.getOriginalFilename(), file.getSize());
        
        String fileUrl = fileStorageService.storeFile(file, directory);
        
        Map<String, String> result = new HashMap<>();
        result.put("fileUrl", fileUrl);
        result.put("originalName", file.getOriginalFilename());
        result.put("size", String.valueOf(file.getSize()));
        
        return ApiResponse.success(result);
    }

    @PostMapping("/upload/image")
    @Operation(summary = "上传图片", description = "上传图片文件")
    public ApiResponse<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        logger.info("上传图片 - originalName={}", file.getOriginalFilename());
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ApiResponse.error(400, "只支持图片文件");
        }
        
        String fileUrl = fileStorageService.storeFile(file, "images");
        
        Map<String, String> result = new HashMap<>();
        result.put("imageUrl", fileUrl);
        result.put("originalName", file.getOriginalFilename());
        
        return ApiResponse.success(result);
    }

    @GetMapping("/download/{fileName:.+}")
    @Operation(summary = "下载文件", description = "下载指定文件")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFile(fileName);
        
        String contentType = "application/octet-stream";
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            logger.warn("无法确定文件类型");
        }
        
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
