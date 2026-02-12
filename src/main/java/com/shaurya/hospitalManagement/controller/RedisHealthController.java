package com.shaurya.hospitalManagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class RedisHealthController {

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/redis-health")
    public ResponseEntity<Map<String, Object>> checkRedisConnection() {
        Map<String, Object> response = new HashMap<>();

        try {
            redisTemplate.opsForValue().set("health:check", "OK");
            String value = redisTemplate.opsForValue().get("health:check");
            redisTemplate.delete("health:check");

            response.put("status", "CONNECTED");
            response.put("message", "Redis is working correctly");
            response.put("testValue", value);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("error", e.getMessage());
            response.put("cause", e.getCause() != null ? e.getCause().getMessage() : "Unknown");
            return ResponseEntity.status(500).body(response);
        }
    }
}