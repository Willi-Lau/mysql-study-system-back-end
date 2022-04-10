package com.lwy.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@CrossOrigin // 跨域（看具体情况）
@RestController
public class MessageController {
    SseEmitter sseEmitter = null;

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        // 设置超时时间为5分钟
        sseEmitter = new SseEmitter(5*60*1000L);
        // 直接返回 SseEmitter 对象就可以和客户端连接
        return sseEmitter;
    }

    @GetMapping("/push/{message}")
    public void push(@PathVariable(name = "message") String message) throws IOException {
        sseEmitter.send(message);
    }


    public void pushByKafka(String message) throws IOException {
        sseEmitter.send(message);
    }
}

