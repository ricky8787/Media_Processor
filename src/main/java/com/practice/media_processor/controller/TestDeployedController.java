package com.practice.media_processor.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestDeployedController {

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> getResult() {
        Map<String, Object> status = new HashMap<>();
        status.put("message", "App is running");
        status.put("status", "success");
        status.put("timestamp", new Date());
        return ResponseEntity.ok(status);
    }

}
