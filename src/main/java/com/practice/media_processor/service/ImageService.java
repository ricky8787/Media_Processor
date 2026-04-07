package com.practice.media_processor.service;

import java.io.File;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final StorageService storageService;

    @Value("${app.queue.name}")
    private String queueName;

    public String processImageUpload(MultipartFile file) throws Exception {
        // 1. 取得副檔名 (例如 jpg, png, webp)
        String originalFilename = file.getOriginalFilename();
        String ext = "png"; // 預設值
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        // 2. 產生任務 ID
        String taskId = UUID.randomUUID().toString();

        // 3. 透過 StorageService 儲存檔案（實際上傳本機或 GCP 取決於環境設定）
        String fileName = "original_" + taskId + "." + ext;
        storageService.saveFile(fileName, file.getBytes());

        // 4. 存入 Redis：狀態設為 processing，並儲存副檔名供 Worker 讀取
        redisTemplate.opsForValue().set("status:" + taskId, "processing");
        redisTemplate.opsForValue().set("ext:" + taskId, ext);

        // 5. 轉發任務到 RabbitMQ
        rabbitTemplate.convertAndSend(queueName, taskId);

        return taskId;
    }

    public String getTaskStatus(String taskId) {
        return redisTemplate.opsForValue().get("status:" + taskId);
    }
}
