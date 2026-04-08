package com.practice.media_processor.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.practice.media_processor.service.GcsStorageService;
import com.practice.media_processor.service.LocalStorageService;
import com.practice.media_processor.service.StorageService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class StorageConfig {

    @Value("${storage.type:local}") // 預設為 local
    private String storageType;

    @Bean
    public Storage gcsClient() {
        log.info("storageType: {}", storageType);
        if ("gcs".equalsIgnoreCase(storageType)) {
            return StorageOptions.getDefaultInstance().getService();
        }
        return null;
    }

    @Bean
    public StorageService storageService(Storage gcsClient) {
        log.info("==== 正在初始化儲存服務，選擇類型: {} ====", storageType);

        if ("gcs".equalsIgnoreCase(storageType)) {
            log.info("成功建立 GcsStorageService Bean");
            return new GcsStorageService(gcsClient);
        } else {
            log.info("成功建立 LocalStorageService Bean");
            return new LocalStorageService();
        }
    }
}