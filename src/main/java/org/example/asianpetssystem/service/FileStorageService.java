package org.example.asianpetssystem.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileStorageService {

    /**
     * 上传文件
     * @param file 文件
     * @param directory 存储目录
     * @return 文件访问URL
     */
    String storeFile(MultipartFile file, String directory);

    /**
     * 加载文件
     * @param fileName 文件名
     * @return 文件资源
     */
    Resource loadFile(String fileName);

    /**
     * 删除文件
     * @param fileName 文件名
     */
    void deleteFile(String fileName);

    /**
     * 获取文件信息
     * @param fileName 文件名
     * @return 文件信息
     */
    Map<String, Object> getFileInfo(String fileName);
}
