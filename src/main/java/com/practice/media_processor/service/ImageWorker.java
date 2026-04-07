package com.practice.media_processor.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessagingTemplate;
// ... (原有 import 不變) ...

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
// (如果原本沒有 import 包含進來，不用擔心，replace_file_content 會依照 Target 匹配)

@Service
@Slf4j
@RequiredArgsConstructor // 自動生成構造函數，把 final 變數注入進來
public class ImageWorker {

    private final StringRedisTemplate redisTemplate;
    private final StorageService storageService;
    private final SimpMessagingTemplate messagingTemplate; // 注入 WebSocket 傳送器

    @RabbitListener(queues = "${app.queue.name}")
    public void processTask(String taskId) {
        log.info("開始處理任務: {}", taskId);
        try {
            // 模擬圖片處理耗時
            Thread.sleep(5000);

            // 從 Redis 取出副檔名，若無則預設為 png
            String ext = redisTemplate.opsForValue().get("ext:" + taskId);
            if (ext == null || ext.isEmpty()) {
                ext = "png";
            }

            // 從 Storage API 讀取原始圖片
            String inputFileName = "original_" + taskId + "." + ext;
            byte[] inputBytes = storageService.getFileBytes(inputFileName);
            BufferedImage originalImage = ImageIO.read(new java.io.ByteArrayInputStream(inputBytes));

            if (originalImage == null) {
                log.error("找不到原始圖片！");
                throw new RuntimeException("Invalid image file format"); // 故意拋出錯誤進 DLQ
            }

            // 模擬圖片處理：簡單的「縮圖」或是「打文字浮水印」
            Graphics2D g = originalImage.createGraphics();
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Processed by Media-Worker", 10, 50);
            g.dispose();

            // 存入處理後的結果
            String outputFileName = taskId + "." + ext;
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            ImageIO.write(originalImage, ext, outputStream);
            storageService.saveFile(outputFileName, outputStream.toByteArray());

            // 更新 Redis 狀態為 completed
            redisTemplate.opsForValue().set("status:" + taskId, "completed");
            log.info("任務處理完成: {}", taskId);

            // 【WebSocket 新增】主動推播圖片連結給前端！頻道名稱用 taskId 隔開，確保不干擾其他人
            String imageUrl = storageService.getFileUrl(outputFileName);
            messagingTemplate.convertAndSend("/topic/tasks/" + taskId, "COMPLETED:" + imageUrl);
            log.info("已透過 WebSocket 推播成功結果給用戶！網址: {}", imageUrl);

        } catch (Exception e) {
            log.error("處理任務失敗，準備進入 DLQ: {}", taskId, e);
            redisTemplate.opsForValue().set("status:" + taskId, "failed");

            // 【WebSocket 新增】告訴前端任務失敗了
            messagingTemplate.convertAndSend("/topic/tasks/" + taskId, "FAILED");

            // 關鍵！如果我們把 Exception 吞掉 (try-catch)，RabbitMQ 會以為我們「處理成功」並把它刪除。
            // 拋出 AmqpRejectAndDontRequeueException 會明確告訴 RabbitMQ：這是一顆毒藥，不要再放回原 Queue
            // 重試，請把它丟進 DLQ！
            throw new org.springframework.amqp.AmqpRejectAndDontRequeueException("Task processing failed", e);
        }
    }

}
