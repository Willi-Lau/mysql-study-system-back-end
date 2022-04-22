package com.lwy.demo.controller;

import com.lwy.demo.utils.SseEmitterServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 * 此接口为 SpringMVC SSE 作用为后端单向向前端传送数据
 */
@RestController
@CrossOrigin
public class MessageController {

    SseEmitter sseEmitter = null;

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 用于创建连接 前端连接端口写这个  必须这么写
     */
    @GetMapping("/subscribe")
    public SseEmitter connect() {
        return SseEmitterServer.connect("15");
    }

    /**
     * 发送消息
     * @param message
     * @return
     */
    @GetMapping("/push/{message}")
    public ResponseEntity<String> push(@PathVariable(name = "message") String message) {
        SseEmitterServer.batchSendMessage(message);
        logger.info("SSE发送消息"+message);
        return ResponseEntity.ok("WebSocket 推送消息给所有人");
    }

    @PostMapping("/push")
    public ResponseEntity<String> push2(@RequestParam String content) {
        SseEmitterServer.batchSendMessage(content);
        logger.info("SSE发送消息1"+content);
        return ResponseEntity.ok("WebSocket 推送消息给所有人");
    }

    //急救 http://localhost:9999/subscribe  先点这个 在刷新 vue 学生端 就可以收到
     //  http://localhost:9999/push/msg=hello
}

