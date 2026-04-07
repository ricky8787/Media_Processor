package com.practice.media_processor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.practice.media_processor.service.ImageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String taskId = imageService.processImageUpload(file);
            return ResponseEntity.ok("任務已送出，ID: " + taskId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/status/{taskId}")
    public ResponseEntity<String> getStatus(@PathVariable String taskId) {
        String status = imageService.getTaskStatus(taskId);
        if (status == null) {
            return ResponseEntity.status(404).body("Task not found");
        }
        return ResponseEntity.ok(status);
    }
}
