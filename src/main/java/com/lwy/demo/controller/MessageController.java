package com.lwy.demo.controller;

import com.lwy.demo.utils.SseEmitterServer;
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

    /**
     * 用于创建连接 前端连接端口写这个  必须这么写
     */
    @GetMapping("/connect")
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
        return ResponseEntity.ok("WebSocket 推送消息给所有人");
    }
}

