package com.practice.media_processor.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;

@Slf4j
@Service
@Profile("!prod") // 如果不是 prod 環境就用本地儲存 (dev 等等)
public class LocalStorageService implements StorageService {

    private final String BASE_DIR = "/app/outputs/";

    @Override
    public void saveFile(String filePath, byte[] data) throws Exception {
        File dest = new File(BASE_DIR + filePath);
        dest.getParentFile().mkdirs(); // 確保資料庫存在
        Files.write(dest.toPath(), data);
    }

    @Override
    public byte[] getFileBytes(String filePath) throws Exception {
        File source = new File(BASE_DIR + filePath);
        return Files.readAllBytes(source.toPath());
    }

    @Override
    public String getFileUrl(String filePath) {
        log.info("讀取檔案: {}", filePath);
        // 在本地開發環境，前端會去 nginx 的 /outputs
        return "http://localhost/outputs/" + filePath;
    }
}
